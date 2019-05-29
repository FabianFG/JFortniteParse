/**
 * 
 */
package UE4_Packages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * @author FunGames
 *
 */
public class UObject {
	private String exportType;
	private List<FPropertyTag> properties;

	public String getExportType() {
		return exportType;
	}

	public List<FPropertyTag> getProperties() {
		return properties;
	}
	
	
	
	public UObject(String exportType, List<FPropertyTag> properties) {
		this.exportType = exportType;
		this.properties = properties;
	}

	public UObject(FArchive Ar, NameMap nameMap, ImportMap importMap, String exportType) throws ReadException {
		this.exportType = exportType;
		properties = serializeProperties(Ar, nameMap, importMap);
		boolean serializeGUID = Ar.readBoolean();
		if(serializeGUID) {
			FGUID _objectGUID = new FGUID(Ar);
		}
	}
	
	public static List<FPropertyTag> serializeProperties(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		List<FPropertyTag> properties = new ArrayList<>();
		while(true) {
			FPropertyTag tag = new FPropertyTag(Ar, nameMap, importMap, true);
			if(!tag.getName().equals("None")) {
				properties.add(tag);
			} else {
				break;
			}
		}
		return properties;
	}

	/**
	 * @param string
	 */
	public FPropertyTagType getPropertyByName(String string) {
		for(FPropertyTag tag : this.properties) {
			if(tag.getName().equals(string)) {
				return tag.getTag();
			}
		}
		//System.err.println("Couldn't find Property '" + string + "' in UObject");
		return null;
		
	}
	
	public JSONArray jsonify() {
		JSONArray propertyTags = new JSONArray();
		for (FPropertyTag propertyTag : this.getProperties()) {
			propertyTags.add(propertyTag.jsonify());
		}
		return propertyTags;

	}
	
	
}
