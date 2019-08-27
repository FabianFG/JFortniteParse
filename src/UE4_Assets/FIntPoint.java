/**
 * 
 */
package UE4_Assets;

import annotation.Serializable;
import annotation.UInt32;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FIntPoint {
	@UInt32 private long x;
	@UInt32 private long y;
}
