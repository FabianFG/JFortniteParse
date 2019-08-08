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
public class FSkinWeightVertexBuffer {
	
	private List<FSkinWeightInfo> weights;
	private int numVertices;

	public List<FSkinWeightInfo> getWeights() {
		return weights;
	}

	public int getNumVertices() {
		return numVertices;
	}
	
	@SuppressWarnings("unused")
	public FSkinWeightVertexBuffer(FArchive Ar) throws ReadException {
		FStripDataFlags flags = new FStripDataFlags(Ar);
		boolean extraBoneInfluences = Ar.readBoolean();
		numVertices = Ar.readInt32();
		if(!flags.isDataStrippedForServer()) {
			int _elementSize = Ar.readInt32();
			int elementCount = Ar.readInt32();
			int numInfluences = extraBoneInfluences ? 8 : 4;
			weights = new ArrayList<>();
			for(int i=0;i<elementCount;i++) {
				weights.add(new FSkinWeightInfo(Ar, numInfluences));
			}
		}
		
	}
}
