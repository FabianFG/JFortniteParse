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
public class FVector2D {
	private float x;
	private float y;
	
	public FVector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public FVector2D(FArchive Ar) throws ReadException {
		x = Ar.readFloat32();
		y = Ar.readFloat32();
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
		return f;
	}
}
