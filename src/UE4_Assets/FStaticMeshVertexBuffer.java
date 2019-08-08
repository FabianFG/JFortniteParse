/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FStaticMeshVertexBuffer {
	private int numTexCoords;
	private int numVertices;
	private FStaticMeshVertexDataTangent tangents;
	private FStaticMeshVertexDataUV uvs;

	public int getNumTexCoords() {
		return numTexCoords;
	}


	public int getNumVertices() {
		return numVertices;
	}


	public FStaticMeshVertexDataTangent getTangents() {
		return tangents;
	}


	public FStaticMeshVertexDataUV getUvs() {
		return uvs;
	}

	@SuppressWarnings("unused")
	public FStaticMeshVertexBuffer(FArchive Ar) throws ReadException {
		FStripDataFlags flags = new FStripDataFlags(Ar);
		numTexCoords = Ar.readInt32();
		numVertices = Ar.readInt32();
		boolean useFullPrecisionUvs = Ar.readBoolean();
		boolean useHighPrecisionTangent = Ar.readBoolean();
		if(!flags.isDataStrippedForServer()) {
			int _elementSize = Ar.readInt32();
			String tangentMode = useHighPrecisionTangent ? "High" : "Low";
			tangents = new FStaticMeshVertexDataTangent(Ar, tangentMode);
			int __elementSize = Ar.readInt32();
			String uvMode = useFullPrecisionUvs ? "High" : "Low";
			uvs = new FStaticMeshVertexDataUV(Ar, uvMode);
		}
	}
}
