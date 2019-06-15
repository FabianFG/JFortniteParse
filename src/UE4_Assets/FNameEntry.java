/**
 * 
 */
package UE4_Assets;

import java.util.Arrays;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FNameEntry {
	
	private String name;
	private int nonCasePreservingHash;
	private int casePreservingHash;
	
	public FNameEntry(FArchive Ar) throws ReadException {
		name = Ar.readString();
		nonCasePreservingHash = Ar.readUInt16();
		casePreservingHash = Ar.readUInt16();
	}

	public String getName() {
		return name;
	}

	public int getNonCasePreservingHash() {
		return nonCasePreservingHash;
	}

	public int getCasePreservingHash() {
		return casePreservingHash;
	}
}
