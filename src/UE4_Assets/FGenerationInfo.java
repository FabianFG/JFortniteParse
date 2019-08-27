/**
 * 
 */
package UE4_Assets;

import annotation.Int32;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FGenerationInfo {
	@Int32 private int exportCount;
	@Int32 private int nameCount;
}