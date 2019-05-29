/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FSoftObjectPath {
	private String assetPathName;
	private String subPathString;
	
	public FSoftObjectPath(FArchive Ar, NameMap nameMap) throws ReadException {
		assetPathName = Ar.readFName(nameMap);
		subPathString = Ar.readString();
	}

	public String getAssetPathName() {
		return assetPathName;
	}

	public String getSubPathString() {
		return subPathString;
	}
}
