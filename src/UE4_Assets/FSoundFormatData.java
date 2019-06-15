/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;

/**
 * @author FunGames
 *
 */
public class FSoundFormatData {
	private String formatName; //FName
	private FByteBulkData data;

	public FSoundFormatData(FArchive Ar, NameMap nameMap) throws ReadException {
		formatName = Ar.readFName(nameMap);
		data = new FByteBulkData(Ar);
	}

	public String getFormatName() {
		return formatName;
	}

	public FByteBulkData getData() {
		return data;
	}
}
