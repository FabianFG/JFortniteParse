/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class TStaticMeshVertexUV {

	private FVector2D value_FVector2D;
	private FVector2DHalf value_FVector2DHalf;
	private boolean usesFVector2D;

	public TStaticMeshVertexUV(FArchive Ar, String uvMode) throws ReadException {
		switch(uvMode) {
		case "High":
			value_FVector2D = new FVector2D(Ar);
			usesFVector2D = true;
			break;
		case "Low":
			value_FVector2DHalf = new FVector2DHalf(Ar);
			usesFVector2D = false;
			break;
		default:
			throw new ReadException("Unknown TStaticMeshVertexTangent Mode: " + uvMode, Ar.Tell());
		}
	}

	public FVector2D getValue_FVector2D() {
		return value_FVector2D;
	}


	public FVector2DHalf getValue_FVector2DHalf() {
		return value_FVector2DHalf;
	}


	public boolean usesFVector2D() {
		return usesFVector2D;
	}
}
