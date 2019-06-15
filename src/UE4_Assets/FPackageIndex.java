/**
 * 
 */
package UE4_Assets;

import java.util.Arrays;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPackageIndex {
	private int index;
	private String importName;
	
	public FPackageIndex(FArchive Ar, ImportMap importMap) throws ReadException {
		index = Ar.readInt32();
		FObjectImport importObject = getPackage(index, importMap);
		if(importObject != null) {
			importName = importObject.getObjectName();
		}
		else {
			importName = "" + index;
		}
	}
	
	public FObjectImport getPackage(int index, ImportMap importMap) {
		if(index < 0) {
			return importMap.get((index * -1) -1);
		}
		if(index > 0) {
			return importMap.get(index-1);
		}
		else {
			return null;
		}
	}
	
	public int getIndex() {
		return index;
	}
	public String getImportName() {
		return importName;
	}
	
}
