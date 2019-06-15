/**
 * 
 */
package UE4_Assets;

import UE4.FArchive;
import UE4_Assets.FPropertyTagType.SoftObjectProperty;

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
