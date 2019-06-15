/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FStripDataFlags {
	private byte globalStripFlags;
	private byte classStripFlags;

	public FStripDataFlags(FArchive Ar) throws ReadException {
		globalStripFlags = Ar.readUInt8();
		classStripFlags = Ar.readUInt8();
	}

	public byte getGlobalStripFlags() {
		return globalStripFlags;
	}

	public byte getClassStripFlags() {
		return classStripFlags;
	}
	public boolean isEditorDataStripped() {
		if((this.globalStripFlags & 1) == 0) {
			return false;
		} else {
			return true;
		}
	}
	public boolean isDataStrippedForServer() {
		if((this.globalStripFlags & 2) == 0) {
			return false;
		} else {
			return true;
		}
	}
	public boolean isClassDataStripped(int flag) {
		if((this.globalStripFlags & flag) == 0) {
			return false;
		} else {
			return true;
		}
	}
	
	
	
}
