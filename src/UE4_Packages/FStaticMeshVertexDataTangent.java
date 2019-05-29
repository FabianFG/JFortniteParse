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
public class FStaticMeshVertexDataTangent {
	
	private List<TStaticMeshVertexTangent> tangents;

	public List<TStaticMeshVertexTangent> getTangents() {
		return tangents;
	}

	public FStaticMeshVertexDataTangent(FArchive Ar, String tangentMode) throws ReadException {
		tangents = new ArrayList<>();
		int elementCount = Ar.readInt32();
		for(int i=0;i<elementCount;i++) {
			tangents.add(new TStaticMeshVertexTangent(Ar, tangentMode));
		}
	}
}
