/**
 * 
 */
package UE4_Packages;

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
