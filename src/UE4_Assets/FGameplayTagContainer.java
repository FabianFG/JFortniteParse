/**
 * 
 */
package UE4_Assets;

import java.util.List;

import annotation.FName;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FGameplayTagContainer {
	@FName private List<String> gameplayTags;
}
