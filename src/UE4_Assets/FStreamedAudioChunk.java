/**
 * 
 */
package UE4_Assets;

import annotation.BooleanZ;
import annotation.Int32;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FStreamedAudioChunk {
	@BooleanZ private boolean bCooked;
	private FByteBulkData data;
	@Int32 private int dataSize;
	@Int32 private int audioDataSize;
}
