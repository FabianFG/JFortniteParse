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
public class FVector4 {
	private float x;
	private float y;
	private float z;
	private float w;
	private int binaryLength;

	public FVector4(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
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
	public float getW() {
		return w;
	}

	public int getBinaryLength() {
		return binaryLength;
	}
	
	public List<Float> getTuple() {
		List<Float> f = new ArrayList<>();
		f.add(x);
		f.add(y);
		f.add(z);
		f.add(w);
		return f;
	}
	
	public List<Float> getTuple3() {
		List<Float> f = new ArrayList<>();
		f.add(x);
		f.add(y);
		f.add(z);
		return f;
	}
	public FVector4 getNormal() {
		float length = (float) Math.sqrt((this.x * this.x) + (this.y * this.y) + (this.z * this.z));
		if(length == 0.0f) {
			return new FVector4(0.0f, 0.0f, 0.0f , 0.0f);
		} else {
			float x = this.x / length;
			float y = this.y / length;
			float z = this.z / length;
			float w;
			if(this.w > 0.0f) {
				w = -1.0f;
			} else {
				w = 1.0f;
			}
			return new FVector4(x, y, z, w);
		}
	}
	
	public FVector4(FArchive Ar) throws ReadException {
		x = Ar.readFloat32();
		y = Ar.readFloat32();
		z = Ar.readFloat32();
		w = Ar.readFloat32();
	}
}
