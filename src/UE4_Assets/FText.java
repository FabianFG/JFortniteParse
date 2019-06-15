/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FText {
	private long flags;
	private byte historyType;
	private String nameSpace;
	private String key;
	private String sourceString;

	public FText(FArchive Ar) throws ReadException {
		flags = Ar.readUInt32();
		historyType = (byte) Ar.readInt8();
		switch(historyType) {
		case -1:
			nameSpace = "";
			key = "";
			sourceString = "";
			break;
		case 0:
			nameSpace = Ar.readString();
			key = Ar.readString();
			sourceString = Ar.readString();
			break;
		default:
			throw new ReadException("Could not read history type: " + historyType, Ar.Tell()-1);
		}
	}

	public long getFlags() {
		return flags;
	}

	public byte getHistoryType() {
		return historyType;
	}

	public String getNameSpace() {
		return nameSpace;
	}

	public String getKey() {
		return key;
	}

	public String getSourceString() {
		return sourceString;
	}
	
}
