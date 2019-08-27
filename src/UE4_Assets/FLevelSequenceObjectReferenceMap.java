/**
 * 
 */
package UE4_Assets;

import java.util.List;

import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FLevelSequenceObjectReferenceMap {
	private List<FLevelSequenceLegacyObjectReference> mapData;	
}
