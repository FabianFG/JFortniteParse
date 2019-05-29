/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FIntPoint {
	private long x;
	private long y;
	

	public FIntPoint(FArchive Ar) throws ReadException {
		x = Ar.readUInt32();
		y = Ar.readUInt32();
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}
	
}
