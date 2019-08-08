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
public class FGameplayTagContainer {
	private List<String> gameplayTags;

	public FGameplayTagContainer(FArchive Ar, NameMap nameMap) throws ReadException {
		long length = Ar.readUInt32();
		gameplayTags = new ArrayList<>();
		for(int i=0;i<length;i++) {
			gameplayTags.add(Ar.readFName(nameMap));
		}
	}

	public List<String> getGameplayTags() {
		return gameplayTags;
	}
	
}
