/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

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
