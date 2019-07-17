/**
 * 
 */
package UE4_Assets;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import UE4.FArchive;

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
			@SuppressWarnings("unused")
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
	
	public static class UObjectSerializer implements JsonSerializer<UObject> {


		@Override
		public JsonElement serialize(UObject src, Type typeOfSrc, JsonSerializationContext context) {
			JsonObject ob = new JsonObject();
			ob.addProperty("export_type", src.exportType);
			src.properties.forEach(tag -> {
				tag.serializeInto(ob, context);
			});
			
			return ob;
		}
		
	}
	
	
}
