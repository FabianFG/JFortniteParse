/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FLevelSequenceLegacyObjectReference {
	private FGUID keyGUID;
	private FGUID objectID;
	private String objectPath;

	public FLevelSequenceLegacyObjectReference(FArchive Ar) throws ReadException {
		keyGUID = new FGUID(Ar);
		objectID = new FGUID(Ar);
		objectPath = Ar.readString();
	}

	public FGUID getKeyGUID() {
		return keyGUID;
	}

	public FGUID getObjectID() {
		return objectID;
	}

	public String getObjectPath() {
		return objectPath;
	}
}
