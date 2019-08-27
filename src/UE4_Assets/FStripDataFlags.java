/**
 * 
 */
package UE4_Assets;

import annotation.Serializable;
import annotation.UInt8;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FStripDataFlags {
	@UInt8 private byte globalStripFlags;
	@UInt8 private byte classStripFlags;

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
