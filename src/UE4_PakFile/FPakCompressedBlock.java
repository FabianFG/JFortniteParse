/**
 * 
 */
package UE4_PakFile;

import UE4.FArchive;
import UE4_Assets.ReadException;

/**
 * @author FunGames
 *
 */
public class FPakCompressedBlock {
	public long compressedStart;
	public long compressedEnd;

	public FPakCompressedBlock(FArchive Ar) throws ReadException {
		compressedStart = Ar.readInt64();
		compressedEnd = Ar.readInt64();
	}
}
