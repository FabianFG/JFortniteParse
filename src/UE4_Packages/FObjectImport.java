/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FObjectImport {
	private String classPackage;
	private String className;
	private FPackageIndex outerIndex;
	private String objectName;
		
	public FObjectImport(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		classPackage = Ar.readFName(nameMap);
		className = Ar.readFName(nameMap);
		outerIndex = new FPackageIndex(Ar, importMap);
		objectName = Ar.readFName(nameMap);
	}

	public String getClassPackage() {
		return classPackage;
	}

	public String getClassName() {
		return className;
	}

	public FPackageIndex getOuterIndex() {
		return outerIndex;
	}

	public String getObjectName() {
		return objectName;
	}


}
