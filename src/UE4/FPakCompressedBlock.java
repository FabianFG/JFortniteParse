/**
 * 
 */
package UE4;

import UE4_Packages.FArchive;
import UE4_Packages.ReadException;

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
