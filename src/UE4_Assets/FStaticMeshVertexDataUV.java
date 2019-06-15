/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FStaticMeshVertexDataUV {
	
	private List<TStaticMeshVertexUV> vertexDataUV;

	public List<TStaticMeshVertexUV> getVertexDataUV() {
		return vertexDataUV;
	}
	
	public FStaticMeshVertexDataUV(FArchive Ar, String uvMode) throws ReadException {
		vertexDataUV = new ArrayList<>();
		int elementCount = Ar.readInt32();
		for(int i=0;i<elementCount;i++) {
			vertexDataUV.add(new TStaticMeshVertexUV(Ar, uvMode));
		}
	}
}
