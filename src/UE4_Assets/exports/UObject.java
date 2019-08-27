/**
 * 
 */
package UE4_Assets.exports;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import UE4.FArchive;
import UE4.deserialize.exception.DeserializationException;
import UE4_Assets.FGUID;
import UE4_Assets.FPropertyTag;
import UE4_Assets.FPropertyTagType;
import UE4_Assets.ReadException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class UObject {
	private String exportType;
	private List<FPropertyTag> properties;
	private boolean serializeGUID;
	private FGUID _objectGUID;
	
	public UObject(FArchive Ar, String exportType) throws DeserializationException, ReadException {
		this.exportType = exportType;
		properties = serializeProperties(Ar);
		serializeGUID = Ar.readBoolean();
		if(serializeGUID) {
			_objectGUID = Ar.read(FGUID.class);
		}
	}
	
	public UObject(String exportType, List<FPropertyTag> properties) {
		this.exportType = exportType;
		this.properties = properties;
		this.serializeGUID = false;
		this._objectGUID = null;
	}
	
	public static List<FPropertyTag> serializeProperties(FArchive Ar) throws DeserializationException {
		List<FPropertyTag> properties = new ArrayList<>();
		while(true) {
			FPropertyTag tag = Ar.read(FPropertyTag.class, true);
			if(tag.getName().equals("None")) {
				break;
			} else {
				properties.add(tag);
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
	
	public static class UObjectJsonSerializer implements JsonSerializer<UObject> {


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
