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
public class NameMap {
	private List<FNameEntry> entrys;
	private int length;
	
	public NameMap(FArchive Ar, int itemCount) throws ReadException {
		this.length = itemCount;
		this.entrys = new ArrayList<>();
		for(int i=0;i< itemCount; i++) {
			FNameEntry entry = new FNameEntry(Ar);
			entrys.add(entry);
		}
	}

	public List<FNameEntry> getEntrys() {
		return entrys;
	}
	public FNameEntry get(int index) {
		return entrys.get(index);
	}

	public int getLength() {
		return length;
	}

	
	
	
}
