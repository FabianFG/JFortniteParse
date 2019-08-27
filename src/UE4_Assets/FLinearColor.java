/**
 * 
 */
package UE4_Assets;

import annotation.Float32;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FLinearColor {
	@Float32 private float r;
	@Float32 private float g;
	@Float32 private float b;
	@Float32 private float a;
}
