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
public class FVector {
	private float x;
	private float y;
	private float z;

	public FVector(FArchive Ar) throws ReadException {
		x = Ar.readFloat32();
		y = Ar.readFloat32();
		z = Ar.readFloat32();
	}

	public float getZ() {
		return z;
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
		return f;
	}
}
