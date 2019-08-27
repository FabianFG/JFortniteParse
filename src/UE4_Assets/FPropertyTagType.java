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
import UE4.deserialize.exception.DeserializationException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author FunGames
 *
 */
@Data
@NoArgsConstructor
public class FPropertyTagType {
	private String propertyType;

	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class BoolProperty extends FPropertyTagType {
		private boolean bool;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class StructProperty extends FPropertyTagType {
		private UScriptStruct struct;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class ObjectProperty extends FPropertyTagType {
		private FPackageIndex object;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class InterfaceProperty extends FPropertyTagType {
		private UInterfaceProperty interfaceProperty;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class FloatProperty extends FPropertyTagType {
		private float float32;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class TextProperty extends FPropertyTagType {
		private FText text;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class StrProperty extends FPropertyTagType {
		private String text;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class NameProperty extends FPropertyTagType {
		private String text;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class IntProperty extends FPropertyTagType {
		private int number;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class UInt16Property extends FPropertyTagType {
		private int number;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class UInt32Property extends FPropertyTagType {
		private long number;

	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class UInt64Property extends FPropertyTagType {
		private BigInteger number;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class ArrayProperty extends FPropertyTagType {
		private UScriptArray array;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class MapProperty extends FPropertyTagType {
		private UScriptMap map;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class ByteProperty extends FPropertyTagType {
		private byte byteValue;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class EnumProperty extends FPropertyTagType {
		private String text;
	}
	
	@Data
	@EqualsAndHashCode(callSuper=false)
	@AllArgsConstructor
	public static class SoftObjectProperty extends FPropertyTagType {
		private FSoftObjectPath object;
	}
	
	public static FPropertyTagType readFPropertyTagType(FArchive Ar, String propertyType, FPropertyTagData tagData) throws ReadException, DeserializationException {
		switch(propertyType) {
			case "BoolProperty":
				switch(tagData.getClass().getSimpleName()) {
					case "BoolProperty":
						boolean b = ((FPropertyTagData.BoolProperty) tagData).isBool();
						return new FPropertyTagType.BoolProperty(b);
					default: 
						throw new ReadException("Given bool property does not have bool data", Ar.Tell());
				}
			case "StructProperty":
				switch(tagData.getClass().getSimpleName()) {
				case "StructProperty":
					String name = ((FPropertyTagData.StructProperty) tagData).getName();
					UScriptStruct struct = Ar.read(UScriptStruct.class, name);
					return new StructProperty(struct);
				default:
					throw new ReadException("Given struct property does not have struct data", Ar.Tell());
				}
			case "ObjectProperty":
				return new ObjectProperty(Ar.read(FPackageIndex.class));
			case "InterfaceProperty":
				return new InterfaceProperty(Ar.read(UInterfaceProperty.class));
			case "FloatProperty":
				return new FloatProperty(Ar.readFloat32());
			case "TextProperty":
				return new TextProperty(Ar.read(FText.class));
			case "StrProperty":
				return new StrProperty(Ar.readString());
			case "NameProperty":
				return new NameProperty(Ar.readFName());
			case "IntProperty":
				return new IntProperty(Ar.readInt32());
			case "UInt16Property":
				return new UInt16Property(Ar.readUInt16());
			case "UInt32Property":
				return new UInt32Property(Ar.readUInt32());
			case "UInt64Property":
				return new UInt64Property(Ar.readUInt64());
			case "ArrayProperty":
				switch(tagData.getClass().getSimpleName()) {
				case "ArrayProperty":
					return new ArrayProperty(Ar.read(UScriptArray.class, ((FPropertyTagData.ArrayProperty) tagData).getProperty()));
				default: 
					throw new ReadException("Cannot read array from given non-array", Ar.Tell());
				}
			case "MapProperty":
				switch(tagData.getClass().getSimpleName()) {
				case "MapProperty":
					return new MapProperty(Ar.read(UScriptMap.class, ((FPropertyTagData.MapProperty) tagData).getKey(),((FPropertyTagData.MapProperty) tagData).getProperty()));
				default:
					throw new ReadException("Given map data does not have map data", Ar.Tell());
				}
			case "ByteProperty":
				switch(tagData.getClass().getSimpleName()) {
					case "ByteProperty":
						String name = ((FPropertyTagData.ByteProperty) tagData).getName();
						if(name.equals("None")) {
							return new ByteProperty(Ar.readUInt8());
						}
						else {
							return new NameProperty(Ar.readFName());
						}
					default: 
						throw new ReadException("Given byte property does not have byte data", Ar.Tell());
				}
			case "EnumProperty":
				switch(tagData.getClass().getSimpleName()) {
					case "EnumProperty":
						String val = ((FPropertyTagData.EnumProperty) tagData).getProperty();
						if(val.equals("None")) {
							return new EnumProperty("None");
						}
						else {
							return new EnumProperty(Ar.readFName());
						}
					default:
						throw new ReadException("Given enum property does not have enum data", Ar.Tell());
				}
			case "SoftObjectProperty":
				return new SoftObjectProperty(Ar.read(FSoftObjectPath.class));
			default: 
				System.err.println("Could not read property type: " + propertyType + " at pos " + Ar.Tell());
				return null;
		}
	}



	public JsonElement jsonify(JsonSerializationContext context) {
		// TODO Auto-generated method stub
		JsonElement json = new JsonObject();
		switch(this.propertyType) {
		case "ArrayProperty":
			UScriptArray array = ((FPropertyTagType.ArrayProperty) this).getArray();
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
			JsonObject ob = new JsonObject();
			ob.add("namespace", new JsonPrimitive(text.getNameSpace()));
			ob.add("key", new JsonPrimitive(text.getKey()));
			ob.add("text", new JsonPrimitive(text.getString()));
			json = ob;
			break;
		case "SoftObjectProperty":
			JsonObject softObjectProperty = new JsonObject();
			FSoftObjectPath path = ((FPropertyTagType.SoftObjectProperty) this).getObject();
			softObjectProperty.add("asset_path", new JsonPrimitive(path.getAssetPathName()));
			softObjectProperty.add("sub_path", new JsonPrimitive(path.getSubPathString()));
			json = softObjectProperty;
			break;
		case "BoolProperty":
			boolean b = ((FPropertyTagType.BoolProperty) this).isBool();
			json = new JsonPrimitive(b);
			break;
		case "ObjectProperty":
			FPackageIndex index = ((FPropertyTagType.ObjectProperty) this).getObject();
			json = new JsonPrimitive(index.getImportName());
			break;
		case "InterfaceProperty":
			UInterfaceProperty interfaceP = ((FPropertyTagType.InterfaceProperty) this).getInterfaceProperty();
			json = new JsonPrimitive(interfaceP.getInterfaceNumber());
			break;
		case "FloatProperty":
			float floatP = ((FPropertyTagType.FloatProperty) this).getFloat32();
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
