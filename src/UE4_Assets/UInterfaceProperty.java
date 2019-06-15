/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class UInterfaceProperty {
	private long interfaceNumber;

	public long getInterfaceNumber() {
		return interfaceNumber;
	}

	
	public UInterfaceProperty(FArchive Ar) throws ReadException {
		interfaceNumber = Ar.readUInt32();
	}
	
}
