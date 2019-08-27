/**
 * 
 */
package UE4;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import UE4.deserialize.exception.DeserializationException;
import UE4.serialize.CustomSerializer;
import UE4.serialize.exception.SerializationException;
import UE4_Assets.ExportMap;
import UE4_Assets.FNameEntry;
import UE4_Assets.FPackageFileSummary;
import UE4_Assets.FStripDataFlags;
import UE4_Assets.ImportMap;
import UE4_Assets.NameMap;
import UE4_Assets.ReadException;
import UE4_Localization.Locres;
import annotation.CustomSerializable;
import annotation.FName;
import annotation.FixedArraySize;
import annotation.Float16;
import annotation.Float32;
import annotation.Int32;
import annotation.Int64;
import annotation.OnlyIf;
import annotation.Serializable;
import annotation.Stringz;
import annotation.StripFlagCheck;
import annotation.UInt16;
import annotation.UInt32;
import annotation.UInt8Boolean;
import annotation.UeExclude;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;

/**
 * @author FunGames
 *
 */
@Log4j(topic = "FArchiveDeserializer")
public class FArchiveWriter implements Cloneable {
	public boolean LittleEndian;

	private int ArPos;

	private final OutputStream out;
	public FPackageFileSummary packageFileSummary;
	public NameMap nameMap;
	public ImportMap importMap;
	public ExportMap exportMap;

	// if uexp reader
	private Map<String, FArchiveWriter> payloads;
	public int uassetSize;
	public int uexpSize;

	@Override
	public FArchiveWriter clone() {
		FArchiveWriter c = new FArchiveWriter(out);
		c.LittleEndian = LittleEndian;
		c.ArPos = ArPos;
		c.nameMap = nameMap;
		c.importMap = importMap;
		c.exportMap = exportMap;
		c.payloads = payloads;
		c.uassetSize = uassetSize;
		c.uexpSize = uexpSize;
		return c;
	}
	public FArchiveWriter(OutputStream out) {
		this.ArPos = 0;
		this.out = out;
		this.LittleEndian = true;
		this.payloads = new HashMap<>();
	}

	public void addPayload(String tag, FArchiveWriter payload) {
		this.payloads.put(tag, payload);
	}

	public FArchiveWriter getPayload(String tag) {
		return this.payloads.get(tag);
	}

	public int Tell() {
		return this.ArPos;
	}

	public int getUexpOffset() {
		return this.uassetSize;
	}

	public int getUbulkOffset() {
		return this.uassetSize + this.uexpSize;
	}

	public void write(Object c, Object... args) throws SerializationException {
		serialize(c, null, args);
	}

	@SuppressWarnings("all")
	public void serialize(Object o, AnnotatedElement a, Object... args) throws SerializationException {
		try {
			Class c = o.getClass();
			if(c.isAnnotationPresent(CustomSerializable.class)) {
				if(!CustomSerializer.class.isAssignableFrom(c)) {
					throw new SerializationException("Classes annotated with CustomSerializable need to implement CustomSerializer");
				}
				CustomSerializer s = (CustomSerializer) o;
				s.serialize(this);
				log.debug("Deserialized " + c + " using the custom deserializer constructor");
			} else if (c.isAnnotationPresent(Serializable.class)) {
				serializeSerializableClass(o);
			} else {
				if (a == null)
					a = new FakeAnnotatedElement(new Annotation[0]);
				serializeJavaGeneric(o, a);
			}
		} catch (Exception e) {
			throw new SerializationException("Serialization failed", e);
		}
	}
	
	private static Class<?> toNonPrimitive(Class<?> primitive) {
		switch(primitive.getSimpleName()) {
		case "boolean":
			return Boolean.class;
		case "byte":
			return Byte.class;
		case "char":
			return Character.class;
		case "short":
			return Short.class;
		case "int":
			return Integer.class;
		case "long":
			return Long.class;
		case "float":
			return Float.class;
		case "double":
			return Double.class;
		default:
			return primitive;
		}
	}

	@SuppressWarnings("all")
	private void serializeSerializableClass(Object o) throws SerializationException, DeserializationException, IllegalArgumentException, IllegalAccessException {
		Class c = o.getClass();
		for (Field f : c.getDeclaredFields()) {
			if (f.isAnnotationPresent(UeExclude.class))
				continue;
			if (f.isAnnotationPresent(OnlyIf.class)) {
				OnlyIf a = f.getAnnotation(OnlyIf.class);
				String req = a.value();
				if (req == null || req.isEmpty())
					throw new SerializationException("OnlyIf annotation requires a value not empty");
				Optional<Field> reqFO = Arrays.stream(c.getDeclaredFields()).filter(ff -> {
					return req.equals(ff.getName());
				}).findFirst();
				if (!reqFO.isPresent())
					throw new DeserializationException("Couldn't find declared field annotated with OnlyIf");
				Field reqF = reqFO.get();
				reqF.setAccessible(true);
				if (reqF.getType().equals(boolean.class) || reqF.getType().equals(Boolean.class)) {
					try {
						if (reqF.getBoolean(o) != a.req()) {
							log.debug("OnlyIf requirement was not fulfilled, Skipping " + f.getName());
							continue;
						} else {
							log.debug("OnlyIf requirement was fulfilled, continuing with serializing " + f.getName());
						}
					} catch (NullPointerException e) {
						throw new DeserializationException(
								"fields required by an IfOnly field needs to be assigned before requiring it");
					}
				}
			}
			f.setAccessible(true);
			log.debug("Field: " + f.getName() + ", Type: " + f.getType().getSimpleName());
			serialize(f.get(o), f);
		}
	}

	@AllArgsConstructor
	private static class FakeAnnotatedElement implements AnnotatedElement {

		private Annotation[] annotations;

		@SuppressWarnings("unchecked")
		@Override
		public <T extends Annotation> T getAnnotation(Class<T> annotationClass) {
			return (T) Arrays.stream(annotations).filter(a -> {
				return a.annotationType().equals(annotationClass);
			}).findFirst().orElse(null);
		}

		@Override
		public Annotation[] getAnnotations() {
			return annotations;
		}

		@Override
		public Annotation[] getDeclaredAnnotations() {
			return annotations;
		}

	}

	@SuppressWarnings({ "all" })
	private void serializeJavaGeneric(Object o, @NonNull AnnotatedElement a) throws IllegalArgumentException,
			IllegalAccessException, ReadException, InvocationTargetException, SerializationException {
		if (a.isAnnotationPresent(UeExclude.class))
			return;

		Class c = o.getClass();
		if (c.isArray()) {
			int length = Array.getLength(o);
			if (!a.isAnnotationPresent(FixedArraySize.class)) {
				writeInt32(length);
			}
			for (int i = 0; i < length; i++) {
				serialize(Array.get(o, i), a);
			}
		} else if (Map.class.isAssignableFrom(c)) {
			List<Annotation> ans = Arrays.stream(a.getAnnotations()).filter(aa -> {
				return !(aa.getClass().equals(FixedArraySize.class) || aa.getClass().equals(OnlyIf.class));
			}).collect(Collectors.toList());
			if (ans.size() != 2) {
				throw new SerializationException("A map needs exactly 2 type annotations");
			}
			Annotation keyAn = ans.get(0);
			AnnotatedElement keyAnnotatedElement = new FakeAnnotatedElement(new Annotation[] { keyAn });
			Annotation valueAn = ans.get(1);
			AnnotatedElement valueAnnotatedElement = new FakeAnnotatedElement(new Annotation[] { valueAn });
			Map map = (Map) o;
			Iterator keySet = map.keySet().iterator();
			if (!a.isAnnotationPresent(FixedArraySize.class)) {
				writeInt32(map.size());
			}
			for (int i = 0; i < map.size(); i++) {
				Object key = keySet.next();
				serialize(key, keyAnnotatedElement);
				serialize(map.get(key), valueAnnotatedElement);
			}
		} else if (List.class.isAssignableFrom(c)) {
			List list = (List) o;
			if (!a.isAnnotationPresent(FixedArraySize.class)) {
				writeInt32(list.size());
			}
			for (int i = 0; i < list.size(); i++) {
				serialize(list.get(i), a);
			}
		} else if (c.isPrimitive() || Number.class.isAssignableFrom(c) || c.equals(Character.class)
				|| c.equals(Boolean.class)) {
			switch (c.getSimpleName()) {
			case "Character":
			case "char":
				writeInt8((char) o);
				break;
			case "Byte":
			case "byte":
				writeUInt8((byte) o);
				break;
			case "Short":
			case "short":
				writeInt16((short) o);
				break;
			case "Integer":
			case "int":
				if (a.isAnnotationPresent(UInt16.class)) {
					writeUInt16((int) o);
				} else if (a.isAnnotationPresent(Int32.class)) {
					writeInt32((int) o);
				} else {
					// Assume that we have a Int32 here
					log.debug("WARN: Missing annotation, assuming Int32");
					writeInt32((int) o);
				}
				break;
			case "Long":
			case "long":
				if (a.isAnnotationPresent(UInt32.class)) {
					writeUInt32((long) o);
				} else if (a.isAnnotationPresent(Int64.class)) {
					writeInt64((long) o);
				} else {
					// Assume that we have a Int64 here
					log.debug("WARN: Missing annotation, assuming Int64");
					writeInt64((long) o);
				}
				break;
			case "Double":
			case "Float":
			case "double":
			case "float":
				if (a.isAnnotationPresent(Float32.class)) {
					writeFloat32((float) o);
				} else if (a.isAnnotationPresent(Float16.class)) {
					writeFloat16((float) o);
				} else {
					// Assume that we have a Float32 here
					log.debug("WARN: Missing annotation, assuming Float32");
					writeFloat32((float) o);
				}
				break;
			case "Boolean":
			case "boolean":
				if (a.isAnnotationPresent(annotation.BooleanZ.class)) {
					writeBoolean((boolean) o);
				} else if (a.isAnnotationPresent(UInt8Boolean.class)) {
					writeBooleanToUInt8((boolean) o);
				} else {
					log.debug("WARN: Missing annotation, assuming Boolean");
					writeBoolean((boolean) o);
				}
				break;
			default:
				throw new SerializationException("Not serializable primitive: " + c.getName());
			}
		} else if (c.equals(String.class)) {
			if (a.isAnnotationPresent(Stringz.class)) {
				writeString((String) o);
			} else if (a.isAnnotationPresent(FName.class)) {
				writeFName((String) o);
			} else {
				// Assume that we have a String here
				log.debug("WARN: Missing annotation, assuming String");
				writeString((String) o);
			}

		} else {
			throw new SerializationException("Unable to write java generic type: " + c.getSimpleName());
		}
	}

//	private Method getAfterDeserializationMethod(Class<?> c) {
//		for (Method m : c.getDeclaredMethods()) {
//			m.setAccessible(true);
//			if (m.isAnnotationPresent(AfterDeserializationMethod.class)) {
//				return m;
//			}
//		}
//		return null;
//	}

	// int8
	public void writeInt8(char o) {
		write(new byte[] {(byte) o});
	}

	// uint8
	public void writeUInt8(byte o) {
		write(new byte[] {o});
	}

	// int16
	public void writeInt16(short o) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.putShort(o);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		write(bb.array());
	}

	// uint16
	public void writeUInt16(int o) {
		ByteBuffer bb = ByteBuffer.allocate(2);
		bb.putShort((short) (o & 0xFFFF));
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		write(bb.array());
	}

	// boolean
	public void writeBoolean(boolean o) {
		writeInt32(o ? 1 : 0);
	}

	// boolean
	public void writeBooleanToUInt8(boolean o) {
		writeUInt8((byte) (o ? 1 : 0));
	}

	// int32
	public void writeInt32(int o) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt(o);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		write(bb.array());
	}

	// uint32
	public void writeUInt32(long o) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putInt((int) (o & 0xFFFFFFFFL));
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		write(bb.array());
	}

	// int64
	public void writeInt64(long o) {
		ByteBuffer bb = ByteBuffer.allocate(8);
		bb.putLong(o);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		write(bb.array());
	}

	// uint64
	public void writeUInt64(BigInteger o) {
		write(o.toByteArray());
	}

	// float32
	public void writeFloat32(float o) {
		ByteBuffer bb = ByteBuffer.allocate(4);
		bb.putFloat(o);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		write(bb.array());
	}

	// float16
	public void writeFloat16(float o) {
		int hbits = fromFloat(o);
		writeUInt16(hbits);
	}

	// FString
	public void writeString(String s) {
		if(s.isEmpty()) {
			writeInt32(0);
			return;
		}
		byte[] bytes = null;
		boolean utf16 = false;
		try {
		    bytes = s.getBytes("UTF-8");
		    utf16 = false;
		} catch (UnsupportedEncodingException e) {
			try {
				bytes = s.getBytes("UTF-16");
				utf16 = true;
			} catch (UnsupportedEncodingException e1) {
				log.error("String encoding failed");
			}
			
		}
		int finalLength = bytes.length + (utf16 ? 2 : 1);
		if (finalLength > 65536 || finalLength < -65536) {
			throw new IllegalArgumentException("Invalid String length : " + finalLength);
		}
		writeInt32(finalLength);
		write(bytes);
		if(utf16) writeUInt16(0); 
		else writeUInt8((byte) 0x00);
	}

	// FName
	public void writeFName(String s) {
		if(!nameMap.contains(s)) {
			nameMap.getEntries().add(new FNameEntry(s));
		}
		writeInt32(nameMap.indexOf(s));
		writeInt32(0); //Extra Index
	}
	
	// returns all higher 16 bits as 0 for all results
	public static int fromFloat( float fval ) {
	    int fbits = Float.floatToIntBits( fval );
	    int sign = fbits >>> 16 & 0x8000;          // sign only
	    int val = ( fbits & 0x7fffffff ) + 0x1000; // rounded value

	    if( val >= 0x47800000 )               // might be or become NaN/Inf
	    {                                     // avoid Inf due to rounding
	        if( ( fbits & 0x7fffffff ) >= 0x47800000 )
	        {                                 // is or must become NaN/Inf
	            if( val < 0x7f800000 )        // was value but too large
	                return sign | 0x7c00;     // make it +/-Inf
	            return sign | 0x7c00 |        // remains +/-Inf or NaN
	                ( fbits & 0x007fffff ) >>> 13; // keep NaN (and Inf) bits
	        }
	        return sign | 0x7bff;             // unrounded not quite Inf
	    }
	    if( val >= 0x38800000 )               // remains normalized value
	        return sign | val - 0x38000000 >>> 13; // exp - 127 + 15
	    if( val < 0x33000000 )                // too small for subnormal
	        return sign;                      // becomes +/-0
	    val = ( fbits & 0x7fffffff ) >>> 23;  // tmp exp for subnormal calc
	    return sign | ( ( fbits & 0x7fffff | 0x800000 ) // add subnormal bit
	         + ( 0x800000 >>> val - 102 )     // round depending on cut off
	      >>> 126 - val );   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
	}

	public void write(byte[] o) {
		try {
			out.write(o);
		} catch (IOException e) {
			log.fatal("Failed to write to bytes to OutputStream");
		}
	}
}
