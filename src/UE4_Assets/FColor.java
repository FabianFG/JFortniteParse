/**
 * 
 */
package UE4_Assets;

import annotation.Serializable;
import annotation.UInt8;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FColor {
	@UInt8 private byte r;
	@UInt8 private byte g;
	@UInt8 private byte b;
	@UInt8 private byte a;
}
