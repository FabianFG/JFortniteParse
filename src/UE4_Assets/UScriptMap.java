/**
 * 
 */
package UE4_Assets;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;

import UE4.FArchive;
import UE4.deserialize.exception.DeserializationException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class UScriptMap {
	private int numKeyToRemove;
	private int length;
	private Map<FPropertyTagType, FPropertyTagType> mapData;
	
	public UScriptMap(FArchive Ar, String keyType, String valueType) throws ReadException, DeserializationException {
		numKeyToRemove = Ar.readInt32();
		if(numKeyToRemove != 0) 
			throw new ReadException("Could not read MapProperty with types: " + keyType + " " + valueType + ", numKeyToRemove is unsupported", Ar.Tell());
		length = Ar.readInt32();
		mapData = new HashMap<>();
		for(int i=0; i<length; i++) {
			FPropertyTagType key = readMapValue(Ar, keyType, "StructProperty");
			key.setPropertyType(keyType);
			FPropertyTagType value = readMapValue(Ar, valueType, "StructProperty");
			value.setPropertyType(valueType);
			mapData.put(key, value);
		}
	}

	private static FPropertyTagType readMapValue(FArchive Ar, String innerType, String structType) throws ReadException, DeserializationException {
		FPropertyTagType tagType;
		switch(innerType) {
		case "BoolProperty":
			tagType = new FPropertyTagType.BoolProperty(Ar.readBooleanFromUInt8());
			return tagType;
		case "EnumProperty":
			tagType = new FPropertyTagType.EnumProperty(Ar.readFName());
			return tagType;
		case "UInt32Property":
			tagType = new FPropertyTagType.UInt32Property(Ar.readUInt32());
			return tagType;
		case "StructProperty":
			tagType = new FPropertyTagType.StructProperty(Ar.read(UScriptStruct.class, structType));
			return tagType;
		case "NameProperty":
			tagType = new FPropertyTagType.NameProperty(Ar.readFName());
			return tagType;
		case "TextProperty":
			tagType = new FPropertyTagType.TextProperty(Ar.read(FText.class));
			return tagType;
		case "StrProperty":
			tagType = new FPropertyTagType.StrProperty(Ar.readString());
			return tagType;
		default:
			tagType = new FPropertyTagType.StructProperty(Ar.read(UScriptStruct.class, structType));
			return tagType;
		}
	}

	/**
	 * @return
	 */
	public JsonArray jsonify(JsonSerializationContext context) {
		JsonArray result = new JsonArray();
		for(FPropertyTagType tag : mapData.keySet()) {
			JsonObject mapEntry = new JsonObject();
			mapEntry.add("key", tag.jsonify(context));
			mapEntry.add("value", mapData.get(tag).jsonify(context));
			result.add(mapEntry);
			
		}
		return result;
	}
}
