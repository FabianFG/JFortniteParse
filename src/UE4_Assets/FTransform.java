/**
 * 
 */
package UE4_Assets;

import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FTransform {
	private FQuat rotation;
	private FVector translation;
	private FVector scale3D;
}
