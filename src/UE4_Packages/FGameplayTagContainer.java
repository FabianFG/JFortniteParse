/**
 * 
 */
package UE4_Packages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
