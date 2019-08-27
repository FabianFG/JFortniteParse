/**
 * 
 */
package UE4;

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
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import UE4.deserialize.exception.DeserializationException;
import UE4_Assets.ExportMap;
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
import lombok.extern.log4j.Log4j;

/**
 * @author FunGames
 *
 */
@Log4j(topic = "FArchiveDeserializer")
public class FArchive implements Cloneable {
	public boolean LittleEndian;

	private int ArPos;
	private int ArStopper;

	public byte[] data;
	public FPackageFileSummary packageFileSummary;
	public NameMap nameMap;
	public ImportMap importMap;
	public ExportMap exportMap;

	// if uexp reader
	private Map<String, FArchive> payloads;
	public int uassetSize;
	public int uexpSize;
	public Optional<Locres> locres;

	public final Objenesis objenesis = new ObjenesisStd();

	@Override
	public FArchive clone() {
		FArchive c = new FArchive();
		c.LittleEndian = LittleEndian;
		c.ArPos = ArPos;
		c.ArStopper = ArStopper;
		c.data = data;
		c.nameMap = nameMap;
		c.importMap = importMap;
		c.exportMap = exportMap;
		c.payloads = payloads;
		c.uassetSize = uassetSize;
		c.uexpSize = uexpSize;
		c.locres = locres;
		return c;
	}

	public FArchive() {
		this.ArPos = 0;
		this.ArStopper = 0;
		this.LittleEndian = true;
		this.payloads = new HashMap<>();
		this.locres = Optional.empty();
	}

	public FArchive(byte[] data) {
		this.ArPos = 0;
		this.data = data;
		this.ArStopper = data.length;
		this.LittleEndian = true;
		this.payloads = new HashMap<>();
		this.locres = Optional.empty();
	}

	public boolean IsCompressed() {
		return false; // Compression not supported
	}

	public void Seek(int Pos) throws ReadException {
		if (Pos <= this.ArStopper) {
			this.ArPos = Pos;
		} else {
			throw new ReadException(String.format("Seeking behind stopper (%d > %d", Pos, this.ArStopper));
		}
	}

	public void addPayload(String tag, FArchive payload) {
		this.payloads.put(tag, payload);
	}

	public FArchive getPayload(String tag) {
		return this.payloads.get(tag);
	}

	public int Tell() {
		return this.ArPos;
	}

	public int GetFileSize() {
		return 0;
	}

	public void SetStopper(int Pos) {
		this.ArStopper = Pos;
	}

	public int GetStopper() {
		return this.ArStopper;
	}

	public boolean IsStopper() {
		int stopper = GetStopper();
		return (stopper != 0) && (Tell() == stopper);
	}

	public static String StaticGetName() {
		return "FArchive";
	}

	public String GetName() {
		return StaticGetName();
	}

	public boolean IsA(String type) {
		return "FArchive".equals(type);
	}

	public int getUexpOffset() {
		return this.uassetSize;
	}

	public int getUbulkOffset() {
		return this.uassetSize + this.uexpSize;
	}

	public <T> T read(Class<T> c, Object... args) throws DeserializationException {
		return deserialize(c, null, args);
	}

	@SuppressWarnings("all")
	public <T> T deserialize(Type t, AnnotatedElement a, Object... args) throws DeserializationException {
		try {
			if (!(t instanceof Class)) {
				if (a == null)
					a = new FakeAnnotatedElement(new Annotation[0]);
				return deserializeJavaGeneric(t, a);
			}
			Class<T> c = (Class<T>) t;
			if(c.isAnnotationPresent(CustomSerializable.class)) {
				Optional<Constructor<?>> cc = Arrays.stream(c.getDeclaredConstructors()).filter(ccc -> {
					Class<?>[] parameters = ccc.getParameterTypes();
					if(parameters.length == 0)
						return false;
					if(!(parameters[0].equals(FArchive.class)))
						return false;
					return true;
				}).findFirst();
				if(!cc.isPresent())
					throw new DeserializationException("CustomSerializable needs an constructor that takes FArchive");
				Constructor constructor = cc.get();
				List<Object> neededArgs = new ArrayList<>();
				neededArgs.add(this);
				Class[] reqArgs = Arrays.copyOfRange(constructor.getParameterTypes(), 1, constructor.getParameterTypes().length);
				for(int i=0; i<reqArgs.length; i++) {
					if(args.length - 1 < i || !(args[i].getClass().equals(toNonPrimitive(reqArgs[i]))))
						throw new DeserializationException("Needs the following args: " + Arrays.toString(reqArgs));
					neededArgs.add(args[i]);
				}
				try {
					T result = (T) constructor.newInstance(neededArgs.toArray());
					log.debug("Deserialized " + c + " using the custom deserializer constructor");
					return result;
				} catch(InvocationTargetException e) {
					throw new DeserializationException("Failed while using the CustomSerializable Constructor", e.getCause());
				}
			} else if (c.isAnnotationPresent(Serializable.class)) {
				return deserializeSerializableClass(c);
			} else {
				if (a == null)
					a = new FakeAnnotatedElement(new Annotation[0]);
				return deserializeJavaGeneric(t, a);
			}
		} catch (Exception e) {
			throw new DeserializationException("Deserialization failed", e);
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
	private <T> T deserializeSerializableClass(Class<T> c) throws IllegalArgumentException, IllegalAccessException,
			DeserializationException, ReadException, InvocationTargetException {
		Object o = objenesis.newInstance(c);
		for (Field f : c.getDeclaredFields()) {
			if (f.isAnnotationPresent(UeExclude.class))
				continue;
			if (f.isAnnotationPresent(OnlyIf.class)) {
				OnlyIf a = f.getAnnotation(OnlyIf.class);
				String req = a.value();
				if (req == null || req.isEmpty())
					throw new DeserializationException("OnlyIf annotation requires a value not empty");
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
			if (f.isAnnotationPresent(StripFlagCheck.class)) {
				List<StripFlagCheck> checks = Arrays.stream(f.getAnnotations())
						.filter(a -> a.annotationType().equals(StripFlagCheck.class)).map(an -> (StripFlagCheck) an)
						.collect(Collectors.toList());
				for (StripFlagCheck a : checks) {
					if (a.var() == null || a.var().isEmpty() || a.method() == null || a.method().isEmpty())
						throw new DeserializationException("StripFlagCheck requires var and method to be not null");
					String variableName = a.var();
					Optional<Field> variableO = Arrays.stream(c.getDeclaredFields()).filter(ff -> {
						return variableName.equals(ff.getName());
					}).findFirst();
					if (!variableO.isPresent())
						throw new DeserializationException(
								"Couldn't find declared field annotated with StripFlagCheck");
					Field variable = variableO.get();
					variable.setAccessible(true);
					Object flagsO = null;
					try {
						flagsO = variable.get(o);
						if (flagsO == null)
							throw new NullPointerException();
					} catch (NullPointerException e) {
						throw new DeserializationException(
								"Field var required with StripFlagCheck needs to be assigned before its available to check");
					}
					if (!(flagsO instanceof FStripDataFlags)) {
						throw new DeserializationException(
								"Field var required with StripFlagCheck is not a instance of FStripDataFlags");
					}
					FStripDataFlags flags = (FStripDataFlags) flagsO;
					String methodName = a.method();
					switch (methodName) {
					case "editorData":
						if (flags.isEditorDataStripped() != a.req())
							continue;
						break;
					case "forServer":
						if (flags.isDataStrippedForServer() != a.req())
							continue;
						break;
					case "classData":
						if (a.flag() < 0)
							throw new DeserializationException(
									"StripFlagCheck for class data requires flag to be assigned");
						if (flags.isClassDataStripped(a.flag()) != a.req())
							continue;
						break;
					default:
						throw new DeserializationException("Unknown check in StripFlagCheck: " + methodName);
					}
				}
			}
			f.setAccessible(true);
			log.debug("Field: " + f.getName() + ", Type: " + f.getType().getSimpleName());
			f.set(o, deserialize(f.getGenericType(), f));
		}
//		Method afterDeserialize = getAfterDeserializationMethod(c);
//		if (afterDeserialize != null) {
//			Class<?>[] ps = afterDeserialize.getParameterTypes();
//			if (ps.length == 0) {
//				afterDeserialize.invoke(o);
//				log.debug("Invoked after deserialization method of " + c.getSimpleName() + " with no parameters");
//			} else if (ps.length == 1) {
//				if (ps[0].equals(this.getClass())) {
//					afterDeserialize.invoke(o, this);
//					log.debug("Invoked after deserialization method of " + c.getSimpleName()
//							+ " with FArchive as parameter");
//				}
//			} else {
//				log.warn("Invalid after deserialization method, must take no parameters or just an FArchive");
//			}
//
//		}
		return (T) o;
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
	private <T> T deserializeJavaGeneric(Type t, AnnotatedElement a) throws IllegalArgumentException,
			IllegalAccessException, ReadException, DeserializationException, InvocationTargetException {
		if (a.isAnnotationPresent(UeExclude.class))
			return null;

		Class c = null;
		if (t instanceof Class)
			c = (Class) t;
		else if (t instanceof ParameterizedType)
			c = (Class) ((ParameterizedType) t).getRawType();
		if (c.isArray()) {
			Class<?> arrayC = c.getComponentType();
			int length;
			if (a.isAnnotationPresent(FixedArraySize.class)) {
				FixedArraySize as = a.getAnnotation(FixedArraySize.class);
				length = as.value();
				log.debug("Found FixedArraySize annotation, deserializing array with length " + length);
			} else {
				length = readInt32();
				log.debug("No FixedArraySize annotation, deserializing array with length " + length + " from Int32");
			}
			List values = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				values.add(deserialize(arrayC, a));
			}
			Object myArray = Array.newInstance(arrayC, values.size());
			for (int i = 0; i < values.size(); i++) {
				Array.set(myArray, i, values.get(i));
			}
			return (T) myArray;
		} else if (c.equals(Map.class)) {
			ParameterizedType type = (ParameterizedType) c.getGenericSuperclass();
			if (type == null) {
				if (t instanceof ParameterizedType) {
					type = (ParameterizedType) t;
				}
			}
			if (type.getActualTypeArguments().length != 2)
				throw new DeserializationException("Deserializing List needs a parameterized map");
			int length;
			if (a.isAnnotationPresent(FixedArraySize.class)) {
				FixedArraySize as = a.getAnnotation(FixedArraySize.class);
				length = as.value();
				log.debug("Found FixedArraySize annotation, deserializing map with length " + length);
			} else {
				length = readInt32();
				log.debug("No FixedArraySize annotation, deserializing map with length " + length + " from Int32");
			}
			Type keyType = type.getActualTypeArguments()[0];
			Type valueType = type.getActualTypeArguments()[1];
			List<Annotation> ans = Arrays.stream(a.getAnnotations()).filter(aa -> {
				return !(aa.getClass().equals(FixedArraySize.class) || aa.getClass().equals(OnlyIf.class));
			}).collect(Collectors.toList());
			if (ans.size() != 2) {
				throw new DeserializationException("A map needs exactly 2 type annotations");
			}
			Annotation keyAn = ans.get(0);
			AnnotatedElement keyAnnotatedElement = new FakeAnnotatedElement(new Annotation[] { keyAn });
			Annotation valueAn = ans.get(1);
			AnnotatedElement valueAnnotatedElement = new FakeAnnotatedElement(new Annotation[] { valueAn });
			Map map = new HashMap<>();
			for (int i = 0; i < length; i++) {
				map.put(deserialize(keyType, keyAnnotatedElement), deserialize(valueType, valueAnnotatedElement));
			}
			return (T) map;
		} else if (c.equals(List.class)) {
			ParameterizedType type = (ParameterizedType) c.getGenericSuperclass();
			if (type == null)
				if (t instanceof ParameterizedType) {
					type = (ParameterizedType) t;
				}
			assert type.getActualTypeArguments().length <= 1;
			if (type.getActualTypeArguments().length == 0)
				throw new DeserializationException("Deserializing List needs a parameterized list");
			int length;
			if (a.isAnnotationPresent(FixedArraySize.class)) {
				FixedArraySize as = a.getAnnotation(FixedArraySize.class);
				length = as.value();
				log.debug("Found FixedArraySize annotation, deserializing list with length " + length);
			} else {
				length = readInt32();
				log.debug("No FixedArraySize annotation, deserializing list with length " + length + " from Int32");
			}
			Type listC = type.getActualTypeArguments()[0];
			List values = new ArrayList<>();
			for (int i = 0; i < length; i++) {
				values.add(deserialize(listC, a));
			}
			return (T) values;
		} else if (c.isPrimitive() || Number.class.isAssignableFrom(c) || c.equals(Character.class)
				|| c.equals(Boolean.class)) {
			switch (c.getSimpleName()) {
			case "Character":
			case "char":
				return (T) new Character(readInt8());
			case "Byte":
			case "byte":
				return (T) new Byte(readUInt8());
			case "Short":
			case "short":
				return (T) new Short(readInt16());
			case "Integer":
			case "int":
				if (a.isAnnotationPresent(UInt16.class)) {
					return (T) new Integer(readUInt16());
				} else if (a.isAnnotationPresent(Int32.class)) {
					return (T) new Integer(readInt32());
				} else {
					// Assume that we have a Int32 here
					log.debug("WARN: Missing annotation, assuming Int32");
					return (T) new Integer(readInt32());
				}
			case "Long":
			case "long":
				if (a.isAnnotationPresent(UInt32.class)) {
					return (T) new Long(readUInt32());
				} else if (a.isAnnotationPresent(Int64.class)) {
					return (T) new Long(readInt64());
				} else {
					// Assume that we have a Int64 here
					log.debug("WARN: Missing annotation, assuming Int64");
					return (T) new Long(readInt64());
				}
			case "Double":
			case "Float":
			case "double":
			case "float":
				if (a.isAnnotationPresent(Float32.class)) {
					return (T) new Float(readFloat32());
				} else if (a.isAnnotationPresent(Float16.class)) {
					return (T) new Float(readInt16());
				} else {
					// Assume that we have a Float32 here
					log.debug("WARN: Missing annotation, assuming Float32");
					return (T) new Float(readFloat32());
				}
			case "Boolean":
			case "boolean":
				if (a.isAnnotationPresent(annotation.BooleanZ.class)) {
					return (T) new Boolean(readBoolean());
				} else if (a.isAnnotationPresent(UInt8Boolean.class)) {
					return (T) new Boolean(readBooleanFromUInt8());
				} else {
					log.debug("WARN: Missing annotation, assuming Boolean");
					return (T) new Boolean(readBoolean());
				}
			default:
				throw new DeserializationException("Not deserializable primitive: " + c.getName());
			}
		} else if (c.equals(String.class)) {
			if (a.isAnnotationPresent(Stringz.class)) {
				return (T) readString();
			} else if (a.isAnnotationPresent(FName.class)) {
				return (T) readFName();
			} else {
				// Assume that we have a String here
				log.debug("WARN: Missing annotation, assuming String");
				return (T) readString();
			}

		} else {
			throw new DeserializationException("Unable to load java generic type: " + c.getSimpleName());
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
	public char readInt8() throws ReadException {
		return (char) serialize(1)[0];
	}

	// uint8
	public byte readUInt8() throws ReadException {
		return serialize(1)[0];
	}

	// int16
	public short readInt16() throws ReadException {
		byte[] d = serialize(2);
		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getShort();
	}

	// uint16
	public int readUInt16() throws ReadException {
		byte[] d = serialize(2);
		;
		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getShort() & 0xFFFF;
	}

	// boolean
	public boolean readBoolean() throws ReadException {
		int b32 = this.readInt32();
		if (b32 == 0) {
			return false;
		} else if (b32 == 1) {
			return true;
		} else {
			// System.err.println("WARNING: Bool has no bool value at " + (this.Tell() - 4)
			// + ", using 'true' as default");
			return true;
		}
	}

	// boolean
	public boolean readBooleanFromUInt8() throws ReadException {
		byte b8 = this.readUInt8();
		if (b8 == 0) {
			return false;
		} else if (b8 == 1) {
			return true;
		} else {
			// System.err.println("WARNING: Bool has no bool value at " + (this.Tell() - 1)
			// + ", using 'true' as default");
			return true;
		}
	}

	// int32
	public int readInt32() throws ReadException {
		byte[] d = serialize(4);
		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt();
	}

	// uint32
	public long readUInt32() throws ReadException {
		byte[] d = serialize(4);

		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getInt() & 0xFFFFFFFFL;
	}

	// int64
	public long readInt64() throws ReadException {
		byte[] d = serialize(8);

		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getLong();
	}

	// uint64
	public BigInteger readUInt64() throws ReadException {
		byte[] d = serialize(8);
		;
		return new BigInteger(1/* positive */, d);
	}

	// float32
	public float readFloat32() throws ReadException {
		byte[] d = serialize(4);
		ByteBuffer bb = ByteBuffer.wrap(d);
		if (this.LittleEndian)
			bb.order(ByteOrder.LITTLE_ENDIAN);
		return bb.getFloat();
	}

	// float16
	public float readFloat16() throws ReadException {
		int hbits = readUInt16();

		return toFloat(hbits);
	}

	// FString
	public String readString() throws ReadException {
		int length = this.readInt32();
		if (length > 65536 || length < -65536) {
			throw new ReadException("Invalid String length : " + length);
		}

		if (length == 0) {
			return "";
		}

		String string = "";
		if (length < 0) {
			int utf16length = length * -1;
			int[] stringData = new int[utf16length - 1];
			for (int i = 0; i < utf16length - 1; i++) {
				stringData[i] = this.readUInt16();
			}
			int nullTerminator = this.readUInt16();
			if (nullTerminator != 0) {
				throw new ReadException("Serialized FString is not null-terminated", this.ArPos);
			}
			string = new String(stringData, 0, utf16length - 1);
		} else if (length > 0) {
			for (int i = 0; i < length - 1; i++) {
				string += this.readInt8();
			}
			byte nullTerminator = this.readUInt8();
			if (nullTerminator != 0x00) {
				throw new ReadException("Serialized FString is not null-terminated", this.ArPos);
			}
		}
		return string;

	}

	// FName
	public String readFName() throws ReadException {
		int indexPos = this.Tell();
		int nameIndex = this.readInt32();
		this.readInt32(); // name number ?
		if (nameIndex >= 0 && nameIndex < nameMap.getLength()) {
			return nameMap.get(nameIndex).getName();
		} else {
			throw new ReadException(
					String.format("FName could not be read at offset %d Requested Index: %d, Name Map Size: %d",
							indexPos, nameIndex, nameMap.getLength()));
		}
	}

	// converts uint16 to float16
	private static float toFloat(int hbits) {
		int mant = hbits & 0x03ff; // 10 bits mantissa
		int exp = hbits & 0x7c00; // 5 bits exponent
		if (exp == 0x7c00) // NaN/Inf
			exp = 0x3fc00; // -> NaN/Inf
		else if (exp != 0) // normalized value
		{
			exp += 0x1c000; // exp - 15 + 127
			if (mant == 0 && exp > 0x1c400) // smooth transition
				return Float.intBitsToFloat((hbits & 0x8000) << 16 | exp << 13 | 0x3ff);
		} else if (mant != 0) // && exp==0 -> subnormal
		{
			exp = 0x1c400; // make it normal
			do {
				mant <<= 1; // mantissa * 2
				exp -= 0x400; // decrease exp by 1
			} while ((mant & 0x400) == 0); // while not normal
			mant &= 0x3ff; // discard subnormal bit
		} // else +/-0 -> +/-0
		return Float.intBitsToFloat( // combine all parts
				(hbits & 0x8000) << 16 // sign << ( 31 - 15 )
						| (exp | mant) << 13); // value << ( 23 - 10 )
	}

	public byte[] serialize(int size) throws ReadException {
		byte[] res = new byte[size];
		if ((this.ArPos + size) <= this.ArStopper) {
			res = Arrays.copyOfRange(this.data, this.ArPos, this.ArPos + size);
			this.ArPos += size;
			return res;
		} else {
			throw new ReadException(
					String.format("Serializing behind stopper (%d + %d > %d", this.ArPos, size, this.ArStopper));
		}
	}
}
