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
public class FRotator {
	@Float32 private float pitch;
	@Float32 private float yaw;
	@Float32 private float roll;
}
