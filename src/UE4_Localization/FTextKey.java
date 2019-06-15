/**
 * 
 */
package UE4_Localization;

import UE4.FArchive;
import UE4_Assets.ReadException;

/**
 * @author FunGames
 *
 */
public class FTextKey {
	private long stringHash;
	private String text;
	
	public FTextKey(FArchive Ar) throws ReadException {
		this.stringHash = Ar.readUInt32();
		this.text = Ar.readString();
	}

	public long getStringHash() {
		return stringHash;
	}

	public String getText() {
		return text;
	}
}
