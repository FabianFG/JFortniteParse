/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;
import annotation.Serializable;
import annotation.UInt16;
import annotation.UInt32;
import lombok.Getter;

/**
 * @author FunGames
 *
 */
@Serializable
@Getter
public class FEngineVersion {
	
	@UInt16 private int major;
	@UInt16 private int minor;
	@UInt16 private int patch;
	@UInt32 private long changelist;
	private String branch;

	/**
	 * @param ar
	 * @throws ReadException 
	 */
	public FEngineVersion(FArchive Ar) throws ReadException {
		major = Ar.readUInt16();
		minor = Ar.readUInt16();
		patch = Ar.readUInt16();
		changelist = Ar.readUInt32();
		branch = Ar.readString();
	}	
	
}
