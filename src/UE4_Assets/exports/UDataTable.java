/**
 * 
 */
package UE4_Assets.exports;

import java.util.HashMap;
import java.util.Map;

import UE4.FArchive;
import UE4.deserialize.exception.DeserializationException;
import UE4_Assets.ReadException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class UDataTable {
	
	private UObject baseObject;
	private Map<String, UObject> rows;
	
	public UDataTable(FArchive Ar) throws DeserializationException, ReadException {
		baseObject = Ar.read(UObject.class, "DataTable");
		
		int numRows = Ar.readInt32();
		
		rows = new HashMap<>();
		for(int i=0; i< numRows; i++) {
			String rowName = Ar.readFName();
			UObject rowObject = new UObject(rowName, UObject.serializeProperties(Ar));
			rows.put(rowName, rowObject);
		}
	}
}
