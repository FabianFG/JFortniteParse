/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FGenerationInfo {
	private int exportCount;
	private int nameCount;
	
	
	public FGenerationInfo(FArchive Ar) throws ReadException {
		exportCount = Ar.readInt32();
		nameCount = Ar.readInt32();
	}


	public int getExportCount() {
		return exportCount;
	}

	public int getNameCount() {
		return nameCount;
	}
	
	
}