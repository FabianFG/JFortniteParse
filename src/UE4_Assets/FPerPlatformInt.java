/**
 * 
 */
package UE4_Assets;

import annotation.Serializable;
import annotation.UInt32;
import annotation.UInt8Boolean;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FPerPlatformInt {
	@UInt8Boolean private boolean cooked;
	@UInt32 private long value;
}
