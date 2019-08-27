/**
 * 
 */
package UE4_Assets;

import java.util.List;

import annotation.Int32;
import annotation.Serializable;
import annotation.Stringz;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FTexturePlatformData {
	
	@Int32 private int sizeX;
	@Int32 private int sizeY;
	@Int32 private int numSlices;
	@Stringz private String pixelFormat;
	@Int32 private int firstMip;
	private List<FTexture2DMipMap> mips;
	
}
