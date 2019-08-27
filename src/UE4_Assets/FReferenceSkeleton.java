/**
 * 
 */
package UE4_Assets;

import java.util.List;
import java.util.Map;

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
public class FReferenceSkeleton {
	private List<FMeshBoneInfo> refBoneInfo;
	private List<FTransform> refBonePose;
	@FName @Int32 private Map<String, Integer> nameToIndex;
}
