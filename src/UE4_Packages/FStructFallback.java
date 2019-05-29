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

	public List<FPropertyTag> getProperties() {
		return properties;
	}

}
