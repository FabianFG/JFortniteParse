/**
 * 
 */
package UE4_Assets;

import annotation.FName;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FSoundFormatData {
	@FName private String formatName;
	private FByteBulkData data;

}
