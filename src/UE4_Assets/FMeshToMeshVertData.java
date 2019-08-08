/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FMeshToMeshVertData {
	private FVector4 positionBaryCoords;
	private FVector4 normalBaryCoords;
	private FVector4 tangentBaryCoords;
	private int[] sourceMeshVertIndices; //Size 4
	private long[] padding; //Size 2

	public FVector4 getPositionBaryCoords() {
		return positionBaryCoords;
	}

	public FVector4 getNormalBaryCoords() {
		return normalBaryCoords;
	}

	public FVector4 getTangentBaryCoords() {
		return tangentBaryCoords;
	}

	public int[] getSourceMeshVertIndices() {
		return sourceMeshVertIndices;
	}

	public long[] getPadding() {
		return padding;
	}
	
	public FMeshToMeshVertData(FArchive Ar) throws ReadException {
		positionBaryCoords = new FVector4(Ar);
		normalBaryCoords = new FVector4(Ar);
		tangentBaryCoords = new FVector4(Ar);
		sourceMeshVertIndices = new int[4];
		for(int i=0;i<sourceMeshVertIndices.length; i++) {
			sourceMeshVertIndices[i] = Ar.readUInt16();
		}
		padding = new long[2];
		for(int i=0; i<padding.length;i++) {
			padding[i] = Ar.readUInt32();
		}
	}
}
