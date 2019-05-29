/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FMeshBoneInfo {
	private String name;
	private int parentIndex;

	public String getName() {
		return name;
	}

	public int getParentIndex() {
		return parentIndex;
	}
	
	public FMeshBoneInfo(FArchive Ar, NameMap nameMap) throws ReadException {
		name = Ar.readFName(nameMap);
		parentIndex = Ar.readInt32();
	}
	
}
