/**
 * 
 */
package UE4_Assets;

import com.google.gson.JsonElement;
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
public class FPropertyTag {
	private String name;
	private String propertyType;
	private int size;
	private int arrayIndex;
	private FPropertyTagData tagData;
	private boolean hasPropertyGUID;
	private FGUID propertyGUID;
	private FPropertyTagType tag;
	
	public FPropertyTag(FArchive Ar, boolean readData) throws ReadException, DeserializationException {
		name = Ar.readFName();
		if(!name.equals("None")) {
			propertyType = Ar.readFName();
			size = Ar.readInt32();
			arrayIndex = Ar.readInt32();
			switch(propertyType) {
			case "StructProperty":
				tagData = Ar.read(FPropertyTagData.StructProperty.class);
				break;
			case "BoolProperty":
				tagData = Ar.read(FPropertyTagData.BoolProperty.class);
				break;
			case "EnumProperty":
				tagData = Ar.read(FPropertyTagData.EnumProperty.class);
				break;
			case "ByteProperty":
				tagData = Ar.read(FPropertyTagData.ByteProperty.class);
				break;
			case "ArrayProperty":
				tagData = Ar.read(FPropertyTagData.ArrayProperty.class);
				break;
			case "MapProperty":
				tagData = Ar.read(FPropertyTagData.MapProperty.class);
				break;
			case "SetProperty":
				tagData = Ar.read(FPropertyTagData.SetProperty.class);
				break;
			}
			
			// MapProperty doesn't seem to store the inner types as their types when they're UStructs.
			//TODO Implement that
			
			boolean hasPropertyGUID = Ar.readBooleanFromUInt8();
			if(hasPropertyGUID) {
				propertyGUID = Ar.read(FGUID.class);
			}
			
			int pos = Ar.Tell();
			if(readData) {
				tag = FPropertyTagType.readFPropertyTagType(Ar, propertyType, tagData);
				tag.setPropertyType(propertyType);
			}
			int finalPos = pos + size;
			if(readData && finalPos != Ar.Tell()) {
				//System.err.println("Could not read entire property: " + name + " (" + propertyType + ")");
			}
			if(readData) {
				//Even if the property wasn't read properly 
				//we don't need to crash here because we know the expected size
				Ar.Seek(finalPos);
			}
			
		}
	}

	public void serializeInto(JsonObject ob, JsonSerializationContext context) {
		JsonElement json = tag.jsonify(context);

		ob.add(name, json);
	}

}
