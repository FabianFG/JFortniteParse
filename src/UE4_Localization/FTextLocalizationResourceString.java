/**
 * 
 */
package UE4_Localization;

import annotation.Int32;
import annotation.Serializable;
import annotation.Stringz;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FTextLocalizationResourceString {
	@Stringz private String data;
	@Int32 private int refCount;
}
