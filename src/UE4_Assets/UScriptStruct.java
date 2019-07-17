/**
 * 
 */
package UE4_Assets;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class UScriptStruct {
	private String structName;
	private Object structType;


	public UScriptStruct(FArchive Ar, NameMap nameMap, ImportMap importMap, String structName) throws ReadException {
		this.structName = structName;
		switch(structName) {
		case "IntPoint":
			structType = new FIntPoint(Ar);
			break;
		case "Guid":
			structType = new FGUID(Ar);
			break;
		case "GameplayTagContainer":
			structType = new FGameplayTagContainer(Ar, nameMap);
			break;
		case "Color":
			structType = new FColor(Ar);
			break;
		case "LinearColor":
			structType = new FLinearColor(Ar);
			break;
		case "SoftObjectPath":
			structType = new FSoftObjectPath(Ar, nameMap);
			break;
		case "Vector2D":
		case "Box2D":
			structType = new FVector2D(Ar);
			break;
		case "Quat":
			structType = new FQuat(Ar);
			break;
		case "Vector":
			structType = new FVector(Ar);
			break;
		case "Rotator":
			structType = new FRotator(Ar);
			break;
		case "PerPlatformFloat":
			structType = new FPerPlatformFloat(Ar);
			break;
		case "PerPlatformInt":
			structType = new FPerPlatformInt(Ar);
			break;
		case "SkeletalMeshSamplingLODBuiltData":
			structType = new FWeightedRandomSampler(Ar);
			break;
		case "LevelSequenceObjectReferenceMap":
			structType = new FLevelSequenceObjectReferenceMap(Ar);
			break;
			//TODO Maybe implement more to reduce crashes
			
		default:
			//System.err.println("WARNING: Unknown struct type: " + structName + ", using FStructFallback");
			structType = new FStructFallback(Ar, nameMap, importMap);
			break;
		}
	} 

	public String getStructName() {
		return structName;
	}

	public Object getStructType() {
		return structType;
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object jsonify() {
		Object json = new JSONObject();
		if (this.structType instanceof FIntPoint) {
			FIntPoint intPoint = (FIntPoint) structType;
			((JSONObject)json).put("X", intPoint.getX());
			((JSONObject)json).put("Y", intPoint.getY());
			
		} else if (this.structType instanceof FGUID) {
			FGUID guid = (FGUID) structType;
			json = guid.getString();
			
		} else if (this.structType instanceof FGameplayTagContainer) {
			json = new JSONArray();
			FGameplayTagContainer container = (FGameplayTagContainer) structType;
			for(String s : container.getGameplayTags()) {
				((JSONArray) json).add(s);
			}
		} else if (this.structType instanceof FColor) {
			FColor color = (FColor) structType;
			((JSONObject)json).put("R", color.getR());
			((JSONObject)json).put("G", color.getG());
			((JSONObject)json).put("B", color.getB());
			((JSONObject)json).put("A", color.getA());
			
		} else if (this.structType instanceof FLinearColor) {
			FLinearColor linearColor = (FLinearColor) structType;
			((JSONObject)json).put("R", linearColor.getR());
			((JSONObject)json).put("G", linearColor.getG());
			((JSONObject)json).put("B", linearColor.getB());
			((JSONObject)json).put("A", linearColor.getA());
			
		} else if (this.structType instanceof FSoftObjectPath) {
			FSoftObjectPath objectPath = (FSoftObjectPath) structType;
			((JSONObject) json).put("asset_path", objectPath.getAssetPathName());
			((JSONObject) json).put("sub_path", objectPath.getSubPathString());
			
		} else if (this.structType instanceof FStructFallback) {
			json = new JSONArray();
			FStructFallback structFallback = (FStructFallback) structType;
			for(FPropertyTag tag : structFallback.getProperties()) {
				((JSONArray) json).add(tag.jsonify());
			}
		} else if (this.structType instanceof FVector2D) {
			FVector2D vector2D = (FVector2D) structType;
			((JSONObject)json).put("X", vector2D.getX());
			((JSONObject)json).put("Y", vector2D.getY());
			
		} else if (this.structType instanceof FQuat) {
			FQuat quat = (FQuat) structType;
			((JSONObject)json).put("X", quat.getX());
			((JSONObject)json).put("Y", quat.getY());
			((JSONObject)json).put("Z", quat.getZ());
			((JSONObject)json).put("W", quat.getW());
			
		} else if (this.structType instanceof FVector) {
			FVector quat = (FVector) structType;
			((JSONObject)json).put("X", quat.getX());
			((JSONObject)json).put("Y", quat.getY());
			((JSONObject)json).put("Z", quat.getZ());
			
		} else if (this.structType instanceof FRotator) {
			FRotator quat = (FRotator) structType;
			((JSONObject)json).put("Pitch", quat.getPitch());
			((JSONObject)json).put("Yaw", quat.getYaw());
			((JSONObject)json).put("Roll", quat.getRoll());
			
		} else if (this.structType instanceof FPerPlatformFloat) {
			FPerPlatformFloat perPlatformFloat = (FPerPlatformFloat) structType;
			json = perPlatformFloat.getValue();
			
		} else if (this.structType instanceof FPerPlatformInt) {
			FPerPlatformInt perPlatformInt = (FPerPlatformInt) structType;
			json = perPlatformInt.getValue();
			
		} else if (this.structType instanceof FWeightedRandomSampler) {
			FWeightedRandomSampler perPlatformInt = (FWeightedRandomSampler) structType;
			//TODO Parse Data
			
		} else if (this.structType instanceof FLevelSequenceLegacyObjectReference) {
			FLevelSequenceLegacyObjectReference objectReference = (FLevelSequenceLegacyObjectReference) structType;
			//TODO Parse Data
			
		}
		
		return json;
	}
}
