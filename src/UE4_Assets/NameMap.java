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
public class NameMap {
	private List<FNameEntry> entries;
	private int length;
	
	public NameMap(FArchive Ar) throws DeserializationException {
		if(Ar.packageFileSummary == null) {
			throw new DeserializationException("Reading the ImportMap requires the PackageFileSummary to be not null");
		}
		Ar.nameMap = this;
		this.length = Ar.packageFileSummary.getNameCount();
		this.entries = new ArrayList<>();
		for(int i=0;i< length; i++) {
			FNameEntry entry = Ar.read(FNameEntry.class);
			entries.add(entry);
		}
		
	}
	
	public int indexOf(String s) {
		for(int i=0;i<entries.size(); i++) {
			if(entries.get(i).getName().equals(s))
				return i;
		}
		return -1;
	}
	
	public boolean contains(String s) {
		return entries.stream().filter(e -> e.getName().equals(s)).findAny().isPresent();
	}

	public FNameEntry get(int index) {
		return entries.get(index);
	}

	
	
	
}
