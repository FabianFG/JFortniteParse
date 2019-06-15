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
public class FQuat {
	private float x;
	private float y;
	private float z;
	private float w;

	public FQuat(FArchive Ar) throws ReadException {
		x = Ar.readFloat32();
		y = Ar.readFloat32();
		z = Ar.readFloat32();
		w = Ar.readFloat32();
	}

	public float getZ() {
		return z;
	}

	public float getW() {
		return w;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public List<Float> getTuple() {
		List<Float> f = new ArrayList<>();
		f.add(x);
		f.add(y);
		f.add(z);
		f.add(w);
		return f;
	}
}
