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
public class FortWeaponMeleeItemDefinition {

	private UObject baseObject;
	private String iconName;

	public FortWeaponMeleeItemDefinition(FArchive Ar, NameMap nameMap, ImportMap importMap) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, "FortWeaponMeleeItemDefinition");
		
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
