/**
 * 
 */
package UE4_Assets;

import java.util.Map;
import java.util.Optional;

import UE4.FArchive;
import UE4_Localization.Locres;

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
	
	private String finalString;
	
	private boolean fake;
	
	public FText(String s) {
		this.fake = true;
		this.sourceString = s;
		this.finalString = s;
	}

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
			
			Ar.locres.ifPresent(locres -> {
				Map<String, String> neededNameSpace = locres.getTexts(getNameSpace());
				if(neededNameSpace == null)
					return;
				finalString = neededNameSpace.get(key);
			});
			break;
		default:
			throw new ReadException("Could not read history type: " + historyType, Ar.Tell()-1);
		}
	}
	
	private String tempTranslate = null;
	
	public String forLocres(Optional<Locres> locresO) {
		tempTranslate = null;
		if(fake)
			return sourceString;
		locresO.ifPresent(locres -> {
			Map<String, String> neededNameSpace = locres.getTexts(getNameSpace());
			if(neededNameSpace == null)
				return;
			tempTranslate = neededNameSpace.get(key);
		});
		return tempTranslate != null ? tempTranslate : sourceString;
	}
	public FText cloneForLocres(Optional<Locres> locresO) {
		if(fake)
			return this;
		locresO.ifPresent(locres -> {
			Map<String, String> neededNameSpace = locres.getTexts(getNameSpace());
			if(neededNameSpace == null)
				return;
			finalString = neededNameSpace.get(key);
		});
		
		return this;
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

	public String getString() {
		
		return finalString != null ? finalString : sourceString;
	}
	
}
