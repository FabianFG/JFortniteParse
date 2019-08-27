/**
 * 
 */
package UE4_PakFile;

import annotation.Int64;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FPakCompressedBlock {
	@Int64 private long compressedStart;
	@Int64 private long compressedEnd;
}
