/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FIndexLengthPair {
	private long word1;
	private long word2;

	

	public long getWord1() {
		return word1;
	}
	public long getWord2() {
		return word2;
	}
	
	public FIndexLengthPair(FArchive Ar) throws ReadException {
		word1 = Ar.readUInt32();
		word2 = Ar.readUInt32();
	}
}
