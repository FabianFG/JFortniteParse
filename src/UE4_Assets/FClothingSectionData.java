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
public class FClothingSectionData {
	private FGUID assetGUID;
	@Int32 private int assetLodIndex;
}
