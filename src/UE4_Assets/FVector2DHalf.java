/**
 * 
 */
package UE4_Assets;

import annotation.Float16;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FVector2DHalf {
	@Float16 private float x;
	@Float16 private float y;
	
	public FVector2D getVector() {
		return new FVector2D(x, y);
	}
}
