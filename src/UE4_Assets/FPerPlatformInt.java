/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPerPlatformInt {
	private boolean cooked;
	private long value;

	public FPerPlatformInt(FArchive Ar) throws ReadException {
		cooked = Ar.readBooleanFromUInt8();
		value = Ar.readUInt32();
	}
	public boolean getCooked() {
		return cooked;
	}
	public long getValue() {
		return value;
	}
}
