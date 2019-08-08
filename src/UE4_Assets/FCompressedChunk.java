/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FCompressedChunk {
	
	private int uncompressedOffset;
	private int uncompressedSize;
	private int compressedOffset;
	private int compressedSize;
	
	/**
	 * @param fArchive
	 * @throws ReadException 
	 */
	public FCompressedChunk(FArchive Ar) throws ReadException {
		uncompressedOffset = Ar.readInt32();
		uncompressedSize = Ar.readInt32();
		compressedOffset = Ar.readInt32();
		compressedSize = Ar.readInt32();
	}
	public int getUncompressedOffset() {
		return uncompressedOffset;
	}
	public int getUncompressedSize() {
		return uncompressedSize;
	}
	public int getCompressedOffset() {
		return compressedOffset;
	}
	public int getCompressedSize() {
		return compressedSize;
	}
	
}
