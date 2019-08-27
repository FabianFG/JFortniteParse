/**
 * 
 */
package UE4_Assets;

import annotation.BooleanZ;
import annotation.FName;
import annotation.OnlyIf;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FSkeletalMaterial {

	private FPackageIndex materialInterface;
	@BooleanZ private boolean serializeSlotName;
	@OnlyIf("serializeSlotName") @FName private String materialSlotName;
	private FMeshUVChannelInfo uvChannelData;	
}
