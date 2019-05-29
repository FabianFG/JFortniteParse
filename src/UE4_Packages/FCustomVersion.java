/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FCustomVersion {
	public FGUID getKey() {
		return key;
	}

	public int getVersion() {
		return version;
	}

	private FGUID key;
	private int version;

	
	public FCustomVersion(FArchive Ar) throws ReadException {
		key = new FGUID(Ar);
		version = Ar.readInt32();
		
	}


	
	
}
