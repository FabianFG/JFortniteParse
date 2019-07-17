/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FStructFallback {
	private List<FPropertyTag> properties;

	public FStructFallback(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		properties = new ArrayList<>();
		while(true) {
			FPropertyTag tag = new FPropertyTag(Ar, nameMap, importMap, true);
			if(!tag.getName().equals("None")) {
				properties.add(tag);
			} else {
				break;
			}
		}
	}
	
	public Optional<FPropertyTag> getPropertyByName(String tagName) {
		return properties.stream().filter(tag -> {
			return tag.getName().equals(tagName);
		}).findFirst();
	}

	public List<FPropertyTag> getProperties() {
		return properties;
	}

}
