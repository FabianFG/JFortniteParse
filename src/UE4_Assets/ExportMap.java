/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;

import UE4.FArchive;
import UE4.deserialize.exception.DeserializationException;
import annotation.CustomSerializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@CustomSerializable
public class ExportMap {
	private int itemCount;
	private List<FObjectExport> entries;
	private int uassetSize;
	
	public ExportMap(FArchive Ar) throws DeserializationException {
		if (Ar.packageFileSummary == null || Ar.nameMap == null || Ar.importMap == null)
			throw new DeserializationException(
					"Deserializing the ExportMap needs the PackageFileSummary, the NameMap and the ImportMap to be not null");
		Ar.exportMap = this;
		itemCount = Ar.packageFileSummary.getExportCount();
		entries = new ArrayList<>();
		for (int i = 0; i < itemCount; i++) {
			entries.add(Ar.read(FObjectExport.class));
		}	
		
	}

	public FObjectExport get(int index) {
		return entries.get(index);
	}
}
