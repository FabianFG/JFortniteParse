/**
 * 
 */
package UE4_Assets;

import java.util.Arrays;

import javax.xml.bind.DatatypeConverter;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPropertyTag {
	private String name;
	private String propertyType;
	private FPropertyTagData tagData;
	private int size;
	private int arrayIndex;
	private FGUID propertyGUID;
	private FPropertyTagType tag;

	public String getName() {
		return name;
	}

	public String getPropertyType() {
		return propertyType;
	}

	public FPropertyTagData getTagData() {
		return tagData;
	}

	public int getSize() {
		return size;
	}

	public int getArrayIndex() {
		return arrayIndex;
	}

	public FGUID getPropertyGUID() {
		return propertyGUID;
	}

	public FPropertyTagType getTag() {
		return tag;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject jsonify() {
		JSONObject res = new JSONObject();
		
		Object json = this.tag.jsonify();
		
		
		res.put(this.name, json);
		
		return res;
	}
	
	public FPropertyTag(FArchive Ar, NameMap nameMap, ImportMap importMap, boolean readData) throws ReadException {
		name = Ar.readFName(nameMap);
		if(!name.equals("None")) {
			propertyType = Ar.readFName(nameMap);
			size = Ar.readInt32();
			arrayIndex = Ar.readInt32();
			tagData = null;
			switch(propertyType) {
			case "StructProperty":
				String nameData = Ar.readFName(nameMap);
				FGUID guid = new FGUID(Ar);
				tagData = new FPropertyTagData.StructProperty(nameData, guid);
				break;
			case "BoolProperty":
				boolean b = Ar.readBooleanFromUInt8();
				tagData = new FPropertyTagData.BoolProperty(b);
				break;
			case "EnumProperty":
				String text = Ar.readFName(nameMap);
				tagData = new FPropertyTagData.EnumProperty(text);
				break;
			case "ByteProperty":
				String text1 = Ar.readFName(nameMap);
				tagData = new FPropertyTagData.ByteProperty(text1);
				break;
			case "ArrayProperty":
				String text2 = Ar.readFName(nameMap);
				tagData = new FPropertyTagData.ArrayProperty(text2);
				break;
			case "MapProperty":
				String text3 = Ar.readFName(nameMap);
				String text4 = Ar.readFName(nameMap);
				tagData = new FPropertyTagData.MapProperty(text3, text4);
				break;
			case "SetProperty":
				String text5 = Ar.readFName(nameMap);
				tagData = new FPropertyTagData.SetProperty(text5);
				break;
			default:
				tagData = null;
			}
			
			// MapProperty doesn't seem to store the inner types as their types when they're UStructs.
			//TODO Implement that
			
			boolean hasPropertyGUID = Ar.readBooleanFromUInt8();
			if(hasPropertyGUID) {
				propertyGUID = new FGUID(Ar);
			} else {
				propertyGUID = null;
			}
			
			String propertyDesc = "Property Tag: " + name + " (" + propertyType + ")";
			//System.out.println(propertyDesc);
			
			int pos = Ar.Tell();
			if(readData) {
				tag = FPropertyTagType.readFPropertyTagType(Ar, nameMap, importMap, propertyType, tagData);
			} else {
				tag = null;
			}
			int finalPos = pos + size;
			if(readData) {
				//Even if the property wasn't read properly 
				//we don't need to crash here because we know the expected size
				Ar.Seek(finalPos);
			}
			if(readData && finalPos != Ar.Tell()) {
				System.err.println("Could not read entire property: " + name + " (" + propertyType + ")");
			}
		}
	}


}
