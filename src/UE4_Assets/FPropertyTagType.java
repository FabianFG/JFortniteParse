/**
 * 
 */
package UE4_Assets;

import java.math.BigInteger;

import javax.xml.bind.DatatypeConverter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPropertyTagType {
	private String propertyType;
	
	public FPropertyTagType(String propertyType) {
		this.propertyType = propertyType;
	}
	
	
	public String getPropertyType() {
		return propertyType;
	}


	public static class BoolProperty extends FPropertyTagType {
		private boolean bool;
		
		public BoolProperty(boolean bool, String propertyType) {
			super(propertyType);
			this.bool = bool;
		}

		public boolean getBool() {
			return bool;
		}

	}
	public static class StructProperty extends FPropertyTagType {
		private UScriptStruct struct;
		
		public StructProperty(UScriptStruct struct, String propertyType) {
			super(propertyType);
			this.struct = struct;
		}

		public UScriptStruct getStruct() {
			return struct;
		}

	}
	public static class ObjectProperty extends FPropertyTagType {
		private FPackageIndex object;
		
		public ObjectProperty(FPackageIndex object, String propertyType) {
			super(propertyType);
			this.object = object;
		}

		public FPackageIndex getStruct() {
			return object;
		}

	}
	public static class InterfaceProperty extends FPropertyTagType {
		private UInterfaceProperty interfaceProperty;
		
		public InterfaceProperty(UInterfaceProperty interfaceProperty, String propertyType) {
			super(propertyType);
			this.interfaceProperty = interfaceProperty;
		}

		public UInterfaceProperty getStruct() {
			return interfaceProperty;
		}

	}
	public static class FloatProperty extends FPropertyTagType {
		private float float32;
		
		public FloatProperty(float float32, String propertyType) {
			super(propertyType);
			this.float32 = float32;
		}

		public float getFloat() {
			return float32;
		}

	}
	public static class TextProperty extends FPropertyTagType {
		private FText text;
		
		public TextProperty(FText text, String propertyType) {
			super(propertyType);
			this.text = text;
		}

		public FText getText() {
			return text;
		}
	}
	public static class StrProperty extends FPropertyTagType {
		private String text;
		
		public StrProperty(String text, String propertyType) {
			super(propertyType);
			this.text = text;
		}

		public String getText() {
			return text;
		}

	}
	public static class NameProperty extends FPropertyTagType {
		private String text;
		
		public NameProperty(String text, String propertyType) {
			super(propertyType);
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}
	public static class IntProperty extends FPropertyTagType {
		private int number;
		
		public IntProperty(int number, String propertyType) {
			super(propertyType);
			this.number = number;
		}

		public int getNumber() {
			return number;
		}
	}
	public static class UInt16Property extends FPropertyTagType {
		private int number;
		
		public UInt16Property(int number, String propertyType) {
			super(propertyType);
			this.number = number;
		}

		public int getNumber() {
			return number;
		}
	}
	public static class UInt32Property extends FPropertyTagType {
		private long number;
		
		public UInt32Property(long number, String propertyType) {
			super(propertyType);
			this.number = number;
		}

		public long getNumber() {
			return number;
		}
	}
	public static class UInt64Property extends FPropertyTagType {
		private BigInteger number;
		
		public UInt64Property(BigInteger number, String propertyType) {
			super(propertyType);
			this.number = number;
		}

		public BigInteger getNumber() {
			return number;
		}

	}
	public static class ArrayProperty extends FPropertyTagType {
		private UScriptArray array;
		
		public ArrayProperty(UScriptArray array, String propertyType) {
			super(propertyType);
			this.array = array;
		}

		public UScriptArray getNumber() {
			return array;
		}
	}
	public static class MapProperty extends FPropertyTagType {
		private UScriptMap map;
		
		public MapProperty(UScriptMap map, String propertyType) {
			super(propertyType);
			this.map = map;
		}

		public UScriptMap getNumber() {
			return map;
		}
	}
	public static class ByteProperty extends FPropertyTagType {
		private byte byteValue;
		
		public ByteProperty(byte byteValue, String propertyType) {
			super(propertyType);
			this.byteValue = byteValue;
		}

		public byte getByteValue() {
			return byteValue;
		}
	}
	public static class EnumProperty extends FPropertyTagType {
		private String text;
		
		public EnumProperty(String text, String propertyType) {
			super(propertyType);
			this.text = text;
		}

		public String getText() {
			return text;
		}
	}
	public static class SoftObjectProperty extends FPropertyTagType {
		private FSoftObjectPath object;
		
		public SoftObjectProperty(FSoftObjectPath object, String propertyType) {
			super(propertyType);
			this.object = object;
		}

		public FSoftObjectPath getText() {
			return object;
		}
	}
	
	public static class SoftObjectPropertyMap extends FPropertyTagType {
		private FGUID object;
		
		public SoftObjectPropertyMap(FGUID object, String propertyType) {
			super(propertyType);
			this.object = object;
		}

		public FGUID getGuid() {
			return object;
		}
	}
	
	public static FPropertyTagType readFPropertyTagType(FArchive Ar, NameMap nameMap, ImportMap importMap, String propertyType, FPropertyTagData tagData) throws ReadException {
		switch(propertyType) {
			case "BoolProperty":
				switch(tagData.getClass().getSimpleName()) {
					case "BoolProperty":
						boolean b = ((FPropertyTagData.BoolProperty) tagData).getBool();
						return new BoolProperty(b, propertyType);
					default: 
						throw new ReadException("Given bool property does not have bool data", Ar.Tell());
				}
			case "StructProperty":
				switch(tagData.getClass().getSimpleName()) {
				case "StructProperty":
					String name = ((FPropertyTagData.StructProperty) tagData).getName();
					UScriptStruct struct = new UScriptStruct(Ar, nameMap, importMap, name);
					return new StructProperty(struct,propertyType);
				default:
					throw new ReadException("Given struct property does not have struct data", Ar.Tell());
				}
			case "ObjectProperty":
				return new ObjectProperty(new FPackageIndex(Ar, importMap),propertyType);
			case "InterfaceProperty":
				return new InterfaceProperty(new UInterfaceProperty(Ar),propertyType);
			case "FloatProperty":
				return new FloatProperty(Ar.readFloat32(),propertyType);
			case "TextProperty":
				return new TextProperty(new FText(Ar),propertyType);
			case "StrProperty":
				return new StrProperty(Ar.readString(),propertyType);
			case "NameProperty":
				return new NameProperty(Ar.readFName(nameMap), propertyType);
			case "IntProperty":
				return new IntProperty(Ar.readInt32(),propertyType);
			case "UInt16Property":
				return new UInt16Property(Ar.readUInt16(),propertyType);
			case "UInt32Property":
				return new UInt32Property(Ar.readUInt32(),propertyType);
			case "UInt64Property":
				return new UInt64Property(Ar.readUInt64(),propertyType);
			case "ArrayProperty":
				switch(tagData.getClass().getSimpleName()) {
				case "ArrayProperty":
					return new ArrayProperty(new UScriptArray(Ar, ((FPropertyTagData.ArrayProperty) tagData).getProperty(), nameMap, importMap), propertyType);
				default: 
					throw new ReadException("Cannot read array from given non-array", Ar.Tell());
				}
			case "MapProperty":
				switch(tagData.getClass().getSimpleName()) {
				case "MapProperty":
					return new MapProperty(new UScriptMap(Ar, nameMap, importMap, ((FPropertyTagData.MapProperty) tagData).getKey(),((FPropertyTagData.MapProperty) tagData).getProperty()), propertyType);
				default:
					throw new ReadException("Given map data does not have map data", Ar.Tell());
				}
			case "ByteProperty":
				switch(tagData.getClass().getSimpleName()) {
					case "ByteProperty":
						String name = ((FPropertyTagData.ByteProperty) tagData).getName();
						if(name.equals("None")) {
							return new ByteProperty(Ar.readUInt8(), propertyType);
						}
						else {
							return new NameProperty(Ar.readFName(nameMap), propertyType);
						}
					default: 
						throw new ReadException("Given byte property does not have byte data", Ar.Tell());
				}
			case "EnumProperty":
				switch(tagData.getClass().getSimpleName()) {
					case "EnumProperty":
						String val = ((FPropertyTagData.EnumProperty) tagData).getProperty();
						if(val.equals("None")) {
							return new EnumProperty("None", propertyType);
						}
						else {
							return new EnumProperty(Ar.readFName(nameMap), propertyType);
						}
					default:
						throw new ReadException("Given enum property does not have enum data", Ar.Tell());
				}
			case "SoftObjectProperty":
				return new SoftObjectProperty(new FSoftObjectPath(Ar, nameMap),propertyType);
			
			default: 
				System.err.println("Could not read property type: " + propertyType + " at pos " + Ar.Tell());
				return null;
		}
	}

	/**
	 * @return
	 */
	public JsonElement jsonify(JsonSerializationContext context) {
		// TODO Auto-generated method stub
		JsonElement json = new JsonObject();
		switch(this.propertyType) {
		case "ArrayProperty":
			UScriptArray array = ((FPropertyTagType.ArrayProperty) this).getNumber();
			json = array.jsonify(context);
			break;
		case "StructProperty":
			UScriptStruct struct = ((FPropertyTagType.StructProperty) this).getStruct();
			json = struct.jsonify(context);
			break;
		case "MapProperty":
			UScriptMap map = ((FPropertyTagType.MapProperty) this).map;
			json = map.jsonify(context);
			break;
		case "TextProperty":
			FText text = ((FPropertyTagType.TextProperty) this).getText();
			json = new JsonPrimitive(text.getString());
			break;
		case "SoftObjectProperty":
			JsonObject softObjectProperty = new JsonObject();
			FSoftObjectPath path = ((FPropertyTagType.SoftObjectProperty) this).getText();
			softObjectProperty.add("asset_path", new JsonPrimitive(path.getAssetPathName()));
			softObjectProperty.add("sub_path", new JsonPrimitive(path.getSubPathString()));
			json = softObjectProperty;
			break;
		case "BoolProperty":
			boolean b = ((FPropertyTagType.BoolProperty) this).getBool();
			json = new JsonPrimitive(b);
			break;
		case "ObjectProperty":
			FPackageIndex index = ((FPropertyTagType.ObjectProperty) this).getStruct();
			json = new JsonPrimitive(index.getImportName());
			break;
		case "InterfaceProperty":
			UInterfaceProperty interfaceP = ((FPropertyTagType.InterfaceProperty) this).getStruct();
			json = new JsonPrimitive(interfaceP.getInterfaceNumber());
			break;
		case "FloatProperty":
			float floatP = ((FPropertyTagType.FloatProperty) this).getFloat();
			json = new JsonPrimitive(floatP);
			break;
		case "StrProperty":
			String str = ((FPropertyTagType.StrProperty) this).getText();
			json = new JsonPrimitive(str);
			break;
		case "NameProperty":
			String string = ((FPropertyTagType.NameProperty) this).getText();
			json = new JsonPrimitive(string);
			break;
		case "IntProperty":
			int intP = ((FPropertyTagType.IntProperty) this).getNumber();
			json = new JsonPrimitive(intP);
			break;
		case "UInt16Property":
			int uint16P = ((FPropertyTagType.UInt16Property) this).getNumber();
			json = new JsonPrimitive(uint16P);
			break;
		case "UInt32Property":
			long uint32P = ((FPropertyTagType.UInt32Property) this).getNumber();
			json = new JsonPrimitive(uint32P);
			break;
		case "UInt64Property":
			BigInteger uint64P = ((FPropertyTagType.UInt64Property) this).getNumber();
			json = new JsonPrimitive(uint64P.longValue());
			break;
		case "ByteProperty":
			if(this instanceof FPropertyTagType.ByteProperty) {
				byte uint8P = ((FPropertyTagType.ByteProperty) this).getByteValue();
				json = new JsonPrimitive(DatatypeConverter.printByte(uint8P));
			}
			else if(this instanceof FPropertyTagType.NameProperty) {
				String string2 = ((FPropertyTagType.NameProperty) this).getText();
				json = new JsonPrimitive(string2);
			}
			break;
		case "EnumProperty":
			String enumP = ((FPropertyTagType.EnumProperty) this).getText();
			json = new JsonPrimitive(enumP);
			break;
		}
		return json;
	}
}
