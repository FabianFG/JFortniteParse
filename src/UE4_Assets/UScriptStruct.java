/**
 * 
 */
package UE4_Assets;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
public class UScriptStruct {
	private String structName;
	private Object structType;
	
	public UScriptStruct(FArchive Ar, String structName) throws DeserializationException {
		this.structName = structName;
		switch(structName) {
		case "IntPoint":
			this.structType = Ar.read(FIntPoint.class);
			break;
		case "Guid":
			this.structType = Ar.read(FGUID.class);
			break;
		case "GameplayTagContainer":
			this.structType = Ar.read(FGameplayTagContainer.class);
			break;
		case "Color":
			this.structType = Ar.read(FColor.class);
			break;
		case "LinearColor":
			this.structType = Ar.read(FLinearColor.class);
			break;
		case "SoftObjectPath":
			this.structType = Ar.read(FSoftObjectPath.class);
			break;
		case "Vector2D":
		case "Box2D":
			this.structType = Ar.read(FVector2D.class);
			break;
		case "Quat":
			this.structType = Ar.read(FQuat.class);
			break;
		case "Vector":
			this.structType = Ar.read(FVector.class);
			break;
		case "Rotator":
			this.structType = Ar.read(FRotator.class);
			break;
		case "PerPlatformFloat":
			this.structType = Ar.read(FPerPlatformFloat.class);
			break;
		case "PerPlatformInt":
			this.structType = Ar.read(FPerPlatformInt.class);
			break;
		case "SkeletalMeshSamplingLODBuiltData":
			this.structType = Ar.read(FWeightedRandomSampler.class);
			break;
		case "LevelSequenceObjectReferenceMap":
			this.structType = Ar.read(FLevelSequenceObjectReferenceMap.class);
			break;
			//TODO Maybe implement more to reduce crashes
			
		default:
			//System.err.println("WARNING: Unknown struct type: " + structName + ", using FStructFallback");
			this.structType = Ar.read(FStructFallback.class);
			break;
		
		}
	}

	/**
	 * @return
	 */
	public JsonElement jsonify(JsonSerializationContext context) {
		JsonElement json = new JsonObject();
		if (this.structType instanceof FIntPoint) {
			FIntPoint intPoint = (FIntPoint) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("X", intPoint.getX());
			ob.addProperty("Y", intPoint.getY());
			json = ob;
			
		} else if (this.structType instanceof FGUID) {
			FGUID guid = (FGUID) structType;
			json = new JsonPrimitive(guid.getString());
			
		} else if (this.structType instanceof FGameplayTagContainer) {
			JsonArray ar = new JsonArray();
			FGameplayTagContainer container = (FGameplayTagContainer) structType;
			for(String s : container.getGameplayTags()) {
				ar.add(s);
			}
			json = ar;
		} else if (this.structType instanceof FColor) {
			FColor color = (FColor) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("R", color.getR());
			ob.addProperty("G", color.getG());
			ob.addProperty("B", color.getB());
			ob.addProperty("A", color.getA());
			json = ob;
			
		} else if (this.structType instanceof FLinearColor) {
			FLinearColor linearColor = (FLinearColor) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("R", linearColor.getR());
			ob.addProperty("G", linearColor.getG());
			ob.addProperty("B", linearColor.getB());
			ob.addProperty("A", linearColor.getA());
			json = ob;
			
		} else if (this.structType instanceof FSoftObjectPath) {
			FSoftObjectPath objectPath = (FSoftObjectPath) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("asset_path", objectPath.getAssetPathName());
			ob.addProperty("sub_path", objectPath.getSubPathString());
			json = ob;
			
		} else if (this.structType instanceof FStructFallback) {
			JsonObject ob = new JsonObject();
			FStructFallback structFallback = (FStructFallback) structType;
			for(FPropertyTag tag : structFallback.getProperties()) {
				ob.add(tag.getName(), tag.getTag().jsonify(context));
			}
			json = ob;
		} else if (this.structType instanceof FVector2D) {
			FVector2D vector2D = (FVector2D) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("X", vector2D.getX());
			ob.addProperty("Y", vector2D.getY());
			json = ob;
			
		} else if (this.structType instanceof FQuat) {
			FQuat quat = (FQuat) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("X", quat.getX());
			ob.addProperty("Y", quat.getY());
			ob.addProperty("Z", quat.getZ());
			ob.addProperty("W", quat.getW());
			json = ob;
			
		} else if (this.structType instanceof FVector) {
			FVector quat = (FVector) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("X", quat.getX());
			ob.addProperty("Y", quat.getY());
			ob.addProperty("Z", quat.getZ());
			json = ob;
			
		} else if (this.structType instanceof FRotator) {
			FRotator quat = (FRotator) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("Pitch", quat.getPitch());
			ob.addProperty("Yaw", quat.getYaw());
			ob.addProperty("Roll", quat.getRoll());
			json = ob;
			
		} else if (this.structType instanceof FPerPlatformFloat) {
			FPerPlatformFloat perPlatformFloat = (FPerPlatformFloat) structType;
			json = new JsonPrimitive(perPlatformFloat.getValue());
			
		} else if (this.structType instanceof FPerPlatformInt) {
			FPerPlatformInt perPlatformInt = (FPerPlatformInt) structType;
			json = new JsonPrimitive(perPlatformInt.getValue());
			
		} else if (this.structType instanceof FWeightedRandomSampler) {
			FWeightedRandomSampler weightRandomSampler = (FWeightedRandomSampler) structType;
			JsonObject ob = new JsonObject();
			JsonArray alias = new JsonArray();
			weightRandomSampler.getAlias().forEach(i -> alias.add(i));
			ob.add("alias", alias);
			JsonArray prob = new JsonArray();
			weightRandomSampler.getProb().forEach(i -> prob.add(i));
			ob.add("prob", prob);
			ob.addProperty("totalWeight", weightRandomSampler.getTotalWeight());
			json = ob;
		} else if (this.structType instanceof FLevelSequenceLegacyObjectReference) {
			FLevelSequenceLegacyObjectReference objectReference = (FLevelSequenceLegacyObjectReference) structType;
			JsonObject ob = new JsonObject();
			ob.addProperty("key_guid", objectReference.getKeyGUID().getString());
			ob.addProperty("object_id", objectReference.getObjectID().getString());
			ob.addProperty("object_path", objectReference.getObjectPath());
			json = ob;
			//TODO Parse Data
			
		}
		
		return json;
	}
}
