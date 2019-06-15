/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPackedRGBA16N {
	private short x;
	private short y;
	private short z;
	private short w;

	public short getX() {
		return x;
	}

	public short getY() {
		return y;
	}

	public short getZ() {
		return z;
	}

	public short getW() {
		return w;
	}
	
	public FPackedRGBA16N(FArchive Ar) throws ReadException {
		x = Ar.readInt16();
		y = Ar.readInt16();
		z = Ar.readInt16();
		w = Ar.readInt16();
	}	
}
