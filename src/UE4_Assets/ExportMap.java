/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class ExportMap {
	private int itemCount;
	private List<FObjectExport> entrys;

	/**
	 * @param uassetAr
	 * @param nameMap
	 * @param importMap
	 * @throws ReadException 
	 */
	public ExportMap(FArchive Ar, int exportCount, NameMap nameMap, ImportMap importMap) throws ReadException {
		this.itemCount = exportCount;
		this.entrys = new ArrayList<>();
		for(int i=0; i<itemCount; i++) {
			FObjectExport entry = new FObjectExport(Ar, importMap, nameMap);
			entrys.add(entry);
		}
	}

	public int getItemCount() {
		return itemCount;
	}

	public List<FObjectExport> getEntrys() {
		return entrys;
	}

	
	public FObjectExport get(int index) {
		return entrys.get(index);
	}
	
	
	
	
	
}
