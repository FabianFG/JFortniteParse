/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPackedNormal {
	private byte x;
	private byte y;
	private byte z;
	private byte w;

	public byte getX() {
		return x;
	}

	public byte getY() {
		return y;
	}

	public byte getZ() {
		return z;
	}

	public byte getW() {
		return w;
	}
	
	public FVector4 getVector() {
		float x = rescaleInt8(this.x);
		float y = rescaleInt8(this.y);
		float z = rescaleInt8(this.z);
		float w = rescaleInt8(this.w);
		return new FVector4(x,y,z,w);
	}
	
	private float rescaleInt8(byte x) {
		Double factor = (1.0/ 127.0);
		Double value = ((Byte)x).doubleValue();
		Double result = factor * value;
		return result.floatValue();
	}
	
	public FPackedNormal(FArchive Ar) throws ReadException {
		x = (byte) Ar.readInt8();
		y = (byte) Ar.readInt8();
		z = (byte) Ar.readInt8();
		w = (byte) Ar.readInt8();
	}	
}
