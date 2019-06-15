/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FLinearColor {
	private float r;
	private float g;
	private float b;
	private float a;

	public FLinearColor(FArchive Ar) throws ReadException {
		r = Ar.readFloat32();
		g = Ar.readFloat32();
		b = Ar.readFloat32();
		a = Ar.readFloat32();
	}

	public float getR() {
		return r;
	}

	public float getG() {
		return g;
	}

	public float getB() {
		return b;
	}

	public float getA() {
		return a;
	}

}
