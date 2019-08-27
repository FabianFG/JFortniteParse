/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
public class ImportMap {

	private int itemCount;
	private List<FObjectImport> entries;

	public ImportMap(FArchive Ar) throws DeserializationException {
		if (Ar.packageFileSummary == null || Ar.nameMap == null)
			throw new DeserializationException(
					"Deserializing the ImportMap needs the PackageFileSummary and the NameMap to be not null");
		Ar.importMap = this;
		this.itemCount = Ar.packageFileSummary.getImportCount();
		this.entries = new ArrayList<>();
		for (int i = 0; i < itemCount; i++) {
			this.entries.add(Ar.read(FObjectImport.class));
		}

	}

	public String getPackageImportByName(String packageObject) {
		Optional<FObjectImport> importR = entries.stream().filter(importO -> {
			if (importO.getClassName().equals("Package"))
				return importO.getObjectName().endsWith(packageObject);
			else
				return false;
		}).findFirst();
		return importR.isPresent() ? importR.get().getObjectName() : null;
	}

	public FObjectImport get(int index) {
		if (index >= 0 && index < entries.size()) {
			return entries.get(index);
		} else {
			return null;
		}
	}

}
