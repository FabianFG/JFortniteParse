/**
 * 
 */
package UE4_Packages;

import java.util.Arrays;

/**
 * @author FunGames
 *
 */
public class FEngineVersion {
	
	private int major;
	private int minor;
	private int patch;
	private long changelist;
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

	public int getMajor() {
		return major;
	}

	public int getMinor() {
		return minor;
	}

	public int getPatch() {
		return patch;
	}

	public long getChangelist() {
		return changelist;
	}

	public String getBranch() {
		return branch;
	}
	
	
	
}
