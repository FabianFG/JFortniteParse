/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class TStaticMeshVertexTangent {
	private FPackedRGBA16N normal_FPackedRGBA16N;
	private FPackedNormal normal_FPackedNormal;
	private FPackedRGBA16N tangent_FPackedRGBA16N;
	private FPackedNormal tangent_FPackedNormal;
	private boolean usesFPackedNormal;


	public FPackedRGBA16N getNormal_FPackedRGBA16N() {
		return normal_FPackedRGBA16N;
	}

	public FPackedNormal getNormal_FPackedNormal() {
		return normal_FPackedNormal;
	}

	public FPackedRGBA16N getTangent_FPackedRGBA16N() {
		return tangent_FPackedRGBA16N;
	}

	public FPackedNormal getTangent_FPackedNormal() {
		return tangent_FPackedNormal;
	}

	public boolean usesFPackedNormal() {
		return usesFPackedNormal;
	}
	
	public TStaticMeshVertexTangent(FArchive Ar, String tangentMode) throws ReadException {
		switch(tangentMode) {
		case "High":
			tangent_FPackedRGBA16N = new FPackedRGBA16N(Ar);
			normal_FPackedRGBA16N = new FPackedRGBA16N(Ar);
			usesFPackedNormal = false;
			break;
		case "Low":
			tangent_FPackedNormal = new FPackedNormal(Ar);
			normal_FPackedNormal = new FPackedNormal(Ar);
			usesFPackedNormal = true;
			break;
		default:
			throw new ReadException("Unknown TStaticMeshVertexTangent Mode: " + tangentMode, Ar.Tell());
		}
	}
}
