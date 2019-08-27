/**
 * 
 */
package UE4_Assets;

import annotation.Serializable;
import annotation.Stringz;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FLevelSequenceLegacyObjectReference {
	private FGUID keyGUID;
	private FGUID objectID;
	@Stringz private String objectPath;
}
