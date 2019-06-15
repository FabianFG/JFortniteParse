/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FReferenceSkeleton {
	private List<FMeshBoneInfo> refBoneInfo;
	private List<FTransform> refBonePose;
	private Map<String, Integer> nameToIndex;

	public List<FMeshBoneInfo> getRefBoneInfo() {
		return refBoneInfo;
	}

	public List<FTransform> getRefBonePose() {
		return refBonePose;
	}

	public Map<String, Integer> getNameToIndex() {
		return nameToIndex;
	}
	
	public FReferenceSkeleton(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		refBoneInfo = new ArrayList<>();
		int elemCount = Ar.readInt32();
		for(int i=0;i<elemCount;i++) {
			refBoneInfo.add(new FMeshBoneInfo(Ar, nameMap));
		}
		
		refBonePose = new ArrayList<>();
		int elemCount2 = Ar.readInt32();
		for(int i=0;i<elemCount2;i++) {
			refBonePose.add(new FTransform(Ar));
		}
		
		nameToIndex = new HashMap<>();
		long itemCount = Ar.readUInt32();
		for(int i=0;i<itemCount;i++) {
			nameToIndex.put(Ar.readFName(nameMap), Ar.readInt32());
		}
		
	}
	
}
