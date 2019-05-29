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
public class FPositionVertexBuffer {
	
	private List<FVector> verts;
	private int stride;
	private int numVerts;
	
	public List<FVector> getVerts() {
		return verts;
	}

	public int getStride() {
		return stride;
	}

	public int getNumVerts() {
		return numVerts;
	}

	public FPositionVertexBuffer(FArchive Ar) throws ReadException {
		stride = Ar.readInt32();
		numVerts = Ar.readInt32();
		int _elementSize = Ar.readInt32();
		verts = new ArrayList<>();
		int elementCount = Ar.readInt32();
		for(int i=0;i<elementCount;i++) {
			verts.add(new FVector(Ar));
		}
	}
}
