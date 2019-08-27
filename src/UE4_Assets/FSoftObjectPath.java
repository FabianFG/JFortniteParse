/**
 * 
 */
package UE4_Assets;

import annotation.FName;
import annotation.Serializable;
import annotation.Stringz;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FSoftObjectPath {
	@FName private String assetPathName;
	@Stringz private String subPathString;
}
