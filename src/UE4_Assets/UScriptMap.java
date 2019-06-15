/**
 * 
 */
package UE4_Assets;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class UScriptMap {
	private Map<FPropertyTagType, FPropertyTagType> mapData;


	public UScriptMap(FArchive Ar, NameMap nameMap, ImportMap importMap, String keyType, String valueType) throws ReadException {
		int numKeyToRemove = Ar.readInt32();
		if(numKeyToRemove != 0) {
			throw new ReadException("Could not read MapProperty with types: " + keyType + " " + valueType + ", numKeyToRemove is unsupported", Ar.Tell());
		}
		int num = Ar.readInt32();
		mapData = new HashMap<>();
		for(int i=0;i<num; i++) {
			FPropertyTagType key = readMapValue(Ar, keyType, "StructProperty", nameMap, importMap);
			FPropertyTagType value = readMapValue(Ar, valueType, "StructProperty", nameMap, importMap);
			mapData.put(key, value);
		}
	}

	public Map<FPropertyTagType, FPropertyTagType> getMapData() {
		return mapData;
	}

	private FPropertyTagType readMapValue(FArchive Ar, String innerType, String structType, NameMap nameMap, ImportMap importMap) throws ReadException {
		FPropertyTagType tagType;
		switch(innerType) {
		case "BoolProperty":
			tagType = new FPropertyTagType.BoolProperty(Ar.readBooleanFromUInt8(), innerType);
			return tagType;
		case "EnumProperty":
			tagType = new FPropertyTagType.EnumProperty(Ar.readFName(nameMap), innerType);
			return tagType;
		case "UInt32Property":
			tagType = new FPropertyTagType.UInt32Property(Ar.readUInt32(), innerType);
			return tagType;
		case "StructProperty":
			tagType = new FPropertyTagType.StructProperty(new UScriptStruct(Ar, nameMap, importMap, structType), innerType);
			return tagType;
		case "NameProperty":
			tagType = new FPropertyTagType.NameProperty(Ar.readFName(nameMap), innerType);
			return tagType;
		case "TextProperty":
			tagType = new FPropertyTagType.TextProperty(new FText(Ar), innerType);
			return tagType;
		case "StrProperty":
			tagType = new FPropertyTagType.StrProperty(Ar.readString(), innerType);
			return tagType;
		default:
			tagType = new FPropertyTagType.StructProperty(new UScriptStruct(Ar, nameMap, importMap, innerType), innerType);
			return tagType;
		}
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray jsonify() {
		JSONArray result = new JSONArray();
		for(FPropertyTagType tag : mapData.keySet()) {
			JSONObject mapEntry = new JSONObject();
			mapEntry.put("key", tag.jsonify());
			mapEntry.put("value", mapData.get(tag).jsonify());
			result.add(mapEntry);
			
		}
		return result;
	}
}
