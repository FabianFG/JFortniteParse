/**
 * 
 */
package UE4_Packages;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FunGames
 *
 */
public class FLevelSequenceObjectReferenceMap {
	private List<FLevelSequenceLegacyObjectReference> mapData;
	
	public FLevelSequenceObjectReferenceMap(FArchive Ar) throws ReadException {
		mapData = new ArrayList<>();
		int elementCount = Ar.readInt32();
		for(int i=0;i<elementCount;i++) {
			mapData.add(new FLevelSequenceLegacyObjectReference(Ar));
		}
	}

	public List<FLevelSequenceLegacyObjectReference> getMapData() {
		return mapData;
	}
	
}
