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
public class FBoxSphereBounds {	
	private FVector origin;
	private FVector boxExtent;
	@Float32 private float sphereRadius;
}
