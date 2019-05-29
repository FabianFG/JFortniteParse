/**
 * 
 */
package UE4_Packages;

import java.util.HashMap;
import java.util.Map;

/**
 * @author FunGames
 *
 */
public class UDataTable {
	
	private UObject baseObject;
	private Map<String, UObject> rows;
	
	public UDataTable(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		this.baseObject = new UObject(Ar, nameMap, importMap, "RowStruct");
		int numRows = Ar.readInt32();
		
		rows = new HashMap<>();
		for(int i=0; i< numRows; i++) {
			String rowName = Ar.readFName(nameMap);
			UObject rowObject = new UObject(rowName, UObject.serializeProperties(Ar, nameMap, importMap));
			rows.put(rowName, rowObject);
		}
	}

	public UObject getBaseObject() {
		return baseObject;
	}

	public Map<String, UObject> getRows() {
		return rows;
	}
}
