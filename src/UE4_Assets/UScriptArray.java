/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
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
public class UScriptArray {
	private long elementCount;
	private FPropertyTag arrayTag;
	private List<FPropertyTagType> contents;
	
	public UScriptArray(FArchive Ar, String innerType) throws ReadException, DeserializationException {
		int beginOffset = Ar.Tell();
		elementCount = Ar.readUInt32();
		if(innerType.equals("StructProperty") || innerType.equals("ArrayProperty")) {
			arrayTag = Ar.read(FPropertyTag.class, false);
			if(arrayTag == null) {
				throw new ReadException("Could not read ArrayProperty with inner type: " + innerType, beginOffset);
			}
		}
		FPropertyTagData innerTagData = null;
		if(arrayTag != null) {
			innerTagData = arrayTag.getTagData();
		}
		contents = new ArrayList<>();
		for(int i=0;i<elementCount; i++) {
			if(innerType.equals("BoolProperty")) {
				FPropertyTagType.BoolProperty t = new FPropertyTagType.BoolProperty(Ar.readBooleanFromUInt8());
				t.setPropertyType(innerType);
				contents.add(t);
				continue;
			} else if(innerType.equals("ByteProperty")) {
				FPropertyTagType.ByteProperty t = new FPropertyTagType.ByteProperty(Ar.readUInt8());
				t.setPropertyType(innerType);
				contents.add(t);
				continue;
			}
			FPropertyTagType t = FPropertyTagType.readFPropertyTagType(Ar, innerType, innerTagData);
			t.setPropertyType(innerType);
			contents.add(t);
		}
	}

	public JsonArray jsonify(JsonSerializationContext context) {
		JsonArray a = new JsonArray();
		for(FPropertyTagType tagType : this.contents) {
			a.add(tagType.jsonify(context));
		}
		return a;
	}

}
