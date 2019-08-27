/**
 * 
 */
package UE4_Assets;

import annotation.Float32;
import annotation.Serializable;
import annotation.UInt8Boolean;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FPerPlatformFloat {
	@UInt8Boolean private boolean cooked;
	@Float32 private float value;
}
