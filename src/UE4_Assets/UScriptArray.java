/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;

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

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONArray jsonify() {
		JSONArray a = new JSONArray();
		for(FPropertyTagType tagType : this.contents) {
			a.add(tagType.jsonify());
		}
		return a;
	}

}
