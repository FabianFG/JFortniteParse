/**
 * 
 */
package UE4_Packages;

import UE4_Packages.FPropertyTagType.SoftObjectProperty;

/**
 * @author FunGames
 *
 */
public class FortHeroType {

	private UObject baseObject;
	private String iconName;
	
	public FortHeroType(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, "FortHeroType");
		
		iconName = null;
		SoftObjectProperty largePreviewImage = (SoftObjectProperty) baseObject.getPropertyByName("LargePreviewImage");
		iconName = largePreviewImage != null ? largePreviewImage.getText().getAssetPathName() : null;
		//TODO has more properties, I just need the image for now
	}


	public String getIconName() {
		return iconName;
	}

	public UObject getBaseObject() {
		return baseObject;
	}
	
}
