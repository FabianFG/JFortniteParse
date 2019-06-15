/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FTransform {

	private FQuat rotation;
	private FVector translation;
	private FVector scale3D;

	public FQuat getRotation() {
		return rotation;
	}

	public FVector getTranslation() {
		return translation;
	}

	public FVector getScale3D() {
		return scale3D;
	}
	
	public FTransform(FArchive Ar) throws ReadException {
		rotation = new FQuat(Ar);
		translation = new FVector(Ar);
		scale3D = new FVector(Ar);
	}
}
