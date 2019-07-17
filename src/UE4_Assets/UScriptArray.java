/**
 * 
 */
package UE4_Assets;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class UScriptArray {
	private FPropertyTag arrayTag;
	private List<FPropertyTagType> contents;

	public UScriptArray(FArchive Ar, String innerType, NameMap nameMap, ImportMap importMap) throws ReadException {
		int beginOffset = Ar.Tell(); //For the error message
		long elementCount = Ar.readUInt32();
		arrayTag = null;
		if(innerType.equals("StructProperty") || innerType.equals("ArrayProperty")) {
			arrayTag = new FPropertyTag(Ar, nameMap, importMap, false);
			if(arrayTag == null) {
				throw new ReadException("Could not read ArrayProperty with inner type: " + innerType, beginOffset);
			}
		}
		FPropertyTagData innerTagData = null;
		if(arrayTag != null) {
			innerTagData = arrayTag.getTagData();
		}
		contents = new ArrayList<>();
		for(int i=0;i<elementCount;i++) {
			if(innerType.equals("BoolProperty")) {
				contents.add(new FPropertyTagType.BoolProperty(Ar.readBooleanFromUInt8(), innerType));
				continue;
			} else if(innerType.equals("ByteProperty")) {
				contents.add(new FPropertyTagType.ByteProperty(Ar.readUInt8(), innerType));
				continue;
			}
			contents.add(FPropertyTagType.readFPropertyTagType(Ar, nameMap, importMap, innerType, innerTagData));
			
		}
	}

	public FPropertyTag getArrayTag() {
		return arrayTag;
	}

	public List<FPropertyTagType> getData() {
		return contents;
	}
	
	public static class UScriptArraySerializer implements JsonSerializer<UScriptArray> {

		@Override
		public JsonElement serialize(UScriptArray src, Type typeOfSrc, JsonSerializationContext context) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

	/**
	 * @return
	 */
	public JsonArray jsonify(JsonSerializationContext context) {
		JsonArray a = new JsonArray();
		for(FPropertyTagType tagType : this.contents) {
			a.add(tagType.jsonify(context));
		}
		return a;
	}

}
