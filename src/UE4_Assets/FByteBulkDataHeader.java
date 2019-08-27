/**
 * 
 */
package UE4_Assets;

import annotation.Int32;
import annotation.Int64;
import annotation.Serializable;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FByteBulkDataHeader {
	
	@Int32 private int bulkDataFlags;
	@Int32 private int elementCount;
	@Int32 private int sizeOnDisk;
	@Int64 private long offsetInFile;

}
