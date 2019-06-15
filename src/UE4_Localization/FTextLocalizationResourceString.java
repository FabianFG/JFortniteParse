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
public class FTextLocalizationResourceString {
	private String data;
	private int refCount;
	
	public FTextLocalizationResourceString(FArchive Ar) throws ReadException {
		this.data = Ar.readString();
		this.refCount = Ar.readInt32();
	}

	public String getData() {
		return data;
	}

	public int getRefCount() {
		return refCount;
	}
	
	
}
