/**
 * 
 */
package UE4_Assets;

import annotation.FixedArraySize;
import annotation.Serializable;
import annotation.UInt16;
import annotation.UInt32;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FMeshToMeshVertData {
	private FVector4 positionBaryCoords;
	private FVector4 normalBaryCoords;
	private FVector4 tangentBaryCoords;
	@FixedArraySize(4) @UInt16 private int[] sourceMeshVertIndices;
	@FixedArraySize(2) @UInt32 private long[] padding;
}
