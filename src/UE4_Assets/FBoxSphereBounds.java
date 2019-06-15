/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FBoxSphereBounds {
	
	private FVector origin;
	private FVector boxExtent;
	private float sphereRadius;

	public FVector getOrigin() {
		return origin;
	}

	public FVector getBoxExtent() {
		return boxExtent;
	}

	public float getSphereRadius() {
		return sphereRadius;
	}
	
	public FBoxSphereBounds(FArchive Ar) throws ReadException {
		origin = new FVector(Ar);
		boxExtent = new FVector(Ar);
		sphereRadius = Ar.readFloat32();
	}
}
