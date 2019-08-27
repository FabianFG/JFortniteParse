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
public class FCompressedChunk {
	@Int32 private int uncompressedOffset;
	@Int32 private int uncompressedSize;
	@Int32 private int compressedOffset;
	@Int32 private int compressedSize;
}
