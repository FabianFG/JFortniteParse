/**
 * 
 */
package UE4_Assets;

import annotation.BooleanZ;
import annotation.Int32;
import annotation.OnlyIf;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FTexture2DMipMap {
	@BooleanZ private boolean cooked;
	private FByteBulkData data;
	@Int32 private int sizeX;
	@Int32 private int sizeY;
	@Int32 private int sizeZ;
	@OnlyIf(value = "cooked", req=false) private String _unused;
}
