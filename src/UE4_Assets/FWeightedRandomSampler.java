/**
 * 
 */
package UE4_Assets;

import java.util.List;

import annotation.Float32;
import annotation.Int32;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FWeightedRandomSampler {
	@Float32 private List<Float> prob;
	@Int32 private List<Integer> alias;
	@Float32 private float totalWeight;
}
