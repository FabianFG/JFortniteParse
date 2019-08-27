/**
 * 
 */
package UE4_Assets;

import annotation.FName;
import annotation.Int32;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FMeshBoneInfo {
	@FName private String name;
	@Int32 private int parentIndex;
}
