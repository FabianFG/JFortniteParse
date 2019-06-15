/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FPerPlatformFloat {
	private boolean cooked;
	private float value;
	
	public FPerPlatformFloat(FArchive Ar) throws ReadException {
		cooked = Ar.readBooleanFromUInt8();
		value = Ar.readFloat32();
	}
	public boolean getCooked() {
		return cooked;
	}
	public float getValue() {
		return value;
	}
}
