/**
 * 
 */
package UE4_Packages;

/**
 * @author FunGames
 *
 */
public class FClothingSectionData {
	
	private FGUID assetGUID;
	private int assetLodIndex;

	public FGUID getAssetGUID() {
		return assetGUID;
	}

	public int getAssetLodIndex() {
		return assetLodIndex;
	}

	public FClothingSectionData(FArchive Ar) throws ReadException {
		assetGUID = new FGUID(Ar);
		assetLodIndex = Ar.readInt32();
	}
}
