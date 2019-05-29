/**
 * 
 */
package Converters;

/**
 * @author FunGames
 *
 */
public class PixelFormatInfo {
	public int FourCC;				// 0 when not DDS-compatible
	public byte BlockSizeX;
	public byte BlockSizeY;
	public byte BytesPerBlock;
	public int X360AlignX;			// 0 when unknown or not supported on XBox360	
	public int X360AlignY;
	public byte Float;				// 0 for RGBA8, 1 for RGBA32
	public String Name;
	

	public PixelFormatInfo(int fourCC, byte blockSizeX, byte blockSizeY, byte bytesPerBlock, int x360AlignX,
			int x360AlignY, byte f, String name) {
		FourCC = fourCC;
		BlockSizeX = blockSizeX;
		BlockSizeY = blockSizeY;
		BytesPerBlock = bytesPerBlock;
		X360AlignX = x360AlignX;
		X360AlignY = x360AlignY;
		Float = f;
		Name = name;
	}
	
	
}
