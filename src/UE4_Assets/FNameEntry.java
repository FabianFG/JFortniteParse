/**
 * 
 */
package UE4_Assets;

import annotation.Serializable;
import annotation.Stringz;
import annotation.UInt16;
import lombok.Data;

/**
 * @author FunGames
 *
 */
@Data
@Serializable
public class FNameEntry {	
	@Stringz private String name;
	@UInt16 private int nonCasePreservingHash;
	@UInt16 private int casePreservingHash;
	
	public FNameEntry(String name) {
		this.name = name;
		this.nonCasePreservingHash = 0;
		this.casePreservingHash = 0;
	}
}
