/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FVector2DHalf {
	private float x;
	private float y;

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public FVector2D getVector() {
		return new FVector2D(x, y);
	}
	
	public FVector2DHalf(FArchive Ar) throws ReadException {
		x = Ar.readFloat16();
		y = Ar.readFloat16();
	}
}
