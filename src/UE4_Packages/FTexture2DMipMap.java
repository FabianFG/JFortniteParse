/**
 * 
 */
package UE4_Packages;

import java.util.Arrays;

/**
 * @author FunGames
 *
 */
public class FTexture2DMipMap {
	private FByteBulkData data;
	private int sizeX;
	private int sizeY;
	private int sizeZ;

	public FTexture2DMipMap(FArchive Ar) throws ReadException {
		boolean cooked = Ar.readBoolean();
		data = new FByteBulkData(Ar);
		sizeX = Ar.readInt32();
		sizeY = Ar.readInt32();
		sizeZ = Ar.readInt32();
		if(!cooked) {
			String u = Ar.readString();
		}
	}

	public FByteBulkData getData() {
		return data;
	}

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	public int getSizeZ() {
		return sizeZ;
	}
}
