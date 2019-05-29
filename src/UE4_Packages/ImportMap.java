/**
 * 
 */
package UE4_Packages;

import java.util.ArrayList;
import java.util.List;

/**
 * @author FunGames
 *
 */
public class ImportMap {
	private int itemCount;
	private List<FObjectImport> entrys;

	public ImportMap(FArchive Ar, int importCount, NameMap nameMap) throws ReadException {
		this.entrys = new ArrayList<>();
		for (int i = 0; i < importCount; i++) {
			FObjectImport entry = new FObjectImport(Ar, nameMap, this);
			this.entrys.add(entry);
		}
	}

	public int getItemCount() {
		return itemCount;
	}

	public List<FObjectImport> getEntrys() {
		return entrys;
	}

	public FObjectImport get(int index) {
		if (index >= 0 && index < entrys.size()) {
			return entrys.get(index);
		} else {
			return null;
		}
	}

}
