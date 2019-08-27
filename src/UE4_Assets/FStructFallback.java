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
public class FStructFallback {
	private List<FPropertyTag> properties;
	
	public FStructFallback(FArchive Ar) throws DeserializationException {
		properties = new ArrayList<>();
		while(true) {
			FPropertyTag tag = Ar.read(FPropertyTag.class, true);
			if(tag.getName().equals("None")) {
				break;
			}
			properties.add(tag);
		}
	}
	
	public Optional<FPropertyTag> getPropertyByName(String tagName) {
		return properties.stream().filter(tag -> {
			return tag.getName().equals(tagName);
		}).findFirst();
	}
}
