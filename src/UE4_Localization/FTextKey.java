/**
 * 
 */
package UE4_Localization;

import annotation.Serializable;
import annotation.Stringz;
import annotation.UInt32;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FTextKey {
	@UInt32 private long stringHash;
	@Stringz private String text;
}
