/**
 * 
 */
package UE4_Packages;

import java.util.List;

import UE4_Packages.FPropertyTagType.EnumProperty;
import UE4_Packages.FPropertyTagType.ObjectProperty;
import UE4_Packages.FPropertyTagType.SoftObjectProperty;
import UE4_Packages.FPropertyTagType.StructProperty;
import UE4_Packages.FPropertyTagType.TextProperty;

/**
 * @author FunGames
 *
 */
public class AthenaItemDefinition {
	private UObject baseObject;
	private String rarity;
	private String displayName;
	private String shortDescription; //Mainly equals the type e.g. "Outfit", "Backbling"
	private String description;
	private List<String> gameplayTags; //Cosmetic Source and Set
	private boolean bHasIcons;
	private String smallPreviewImage;
	private String largePreviewImage;
	private String displayAssetPath;
	private boolean bUsesDisplayPath;
	private boolean bUsesHeroDefinition;
	private String heroDefinitionPackage;
	private String weaponDefinitionPackage;
	private boolean bUsesWeaponDefinition;
	
	public boolean usesHeroDefinition() {
		return bUsesHeroDefinition;
	}


	public String getWeaponDefinitionPackage() {
		return weaponDefinitionPackage;
	}


	public boolean usesWeaponDefinition() {
		return bUsesWeaponDefinition;
	}


	public String getHeroDefinitionPackage() {
		return heroDefinitionPackage;
	}


	public UObject getBaseObject() {
		return baseObject;
	}

	public String getDisplayAssetPath() {
		return displayAssetPath;
	}


	public boolean usesDisplayPath() {
		return bUsesDisplayPath;
	}


	public boolean hasIcons() {
		return bHasIcons;
	}


	public String getSmallPreviewImage() {
		return smallPreviewImage;
	}


	public String getLargePreviewImage() {
		return largePreviewImage;
	}

	public AthenaItemDefinition(FArchive Ar, NameMap nameMap, ImportMap importMap, String exportName) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, exportName);
		try {
			ObjectProperty heroDefinitionObject = (ObjectProperty) baseObject.getPropertyByName("HeroDefinition");
			String heroDefinitionPackageName = heroDefinitionObject != null ? heroDefinitionObject.getStruct().getImportName() : null;
			heroDefinitionPackage = null;
			bUsesHeroDefinition = false;
			if(heroDefinitionPackageName != null) {
				for(FObjectImport importObject : importMap.getEntrys()) {
					if(importObject.getClassName().equals("Package")) {
						if(heroDefinitionPackageName != null) {
							if(importObject.getObjectName().endsWith(heroDefinitionPackageName)) {
								heroDefinitionPackage = importObject.getObjectName();
								bUsesHeroDefinition = true;
								break;
							}
						}
					}
				}
			}
			ObjectProperty weaponDefinitionObject = (ObjectProperty) baseObject.getPropertyByName("WeaponDefinition");
			String weaponDefinitionPackageName = weaponDefinitionObject != null ? weaponDefinitionObject.getStruct().getImportName() : null;
			weaponDefinitionPackage = null;
			bUsesWeaponDefinition = false;
			if(weaponDefinitionPackageName != null) {
				for(FObjectImport importObject : importMap.getEntrys()) {
					if(importObject.getClassName().equals("Package")) {
						if(weaponDefinitionPackageName != null) {
							if(importObject.getObjectName().endsWith(weaponDefinitionPackageName)) {
								weaponDefinitionPackage = importObject.getObjectName();
								bUsesWeaponDefinition = true;
								break;
							}
						}
					}
				}
			}
			
			EnumProperty rarityObject = (EnumProperty) baseObject.getPropertyByName("Rarity");
			rarity = rarityObject != null ? rarityObject.getText() : null;
			TextProperty displayNameObject = (TextProperty) baseObject.getPropertyByName("DisplayName");
			displayName = displayNameObject != null ? displayNameObject.getText().getSourceString() : null;
			TextProperty shortDescriptionObject = (TextProperty) baseObject.getPropertyByName("ShortDescription");
			shortDescription = shortDescriptionObject != null ? shortDescriptionObject.getText().getSourceString() : null;
			TextProperty descriptionObject = (TextProperty) baseObject.getPropertyByName("Description");
			description = descriptionObject != null ? descriptionObject.getText().getSourceString() : null;
			StructProperty gameplayTagsObject = (StructProperty) baseObject.getPropertyByName("GameplayTags");
			gameplayTags = gameplayTagsObject != null ? ((FGameplayTagContainer)gameplayTagsObject.getStruct().getStructType()).getGameplayTags() : null;
			SoftObjectProperty smallPreviewImageObject = (SoftObjectProperty) baseObject.getPropertyByName("SmallPreviewImage");
			smallPreviewImage = smallPreviewImageObject != null ? smallPreviewImageObject.getText().getAssetPathName() : null;
			SoftObjectProperty largePreviewImageObject = (SoftObjectProperty) baseObject.getPropertyByName("LargePreviewImage");
			largePreviewImage = largePreviewImageObject != null ? largePreviewImageObject.getText().getAssetPathName() : null;
			
			if(largePreviewImage == null) {
				//Used for Emojis, equals large image object
				SoftObjectProperty spriteSheetObjectProperty = (SoftObjectProperty) baseObject.getPropertyByName("SpriteSheet");
				largePreviewImage = spriteSheetObjectProperty != null ? spriteSheetObjectProperty.getText().getAssetPathName() : null;
			}
			bHasIcons = smallPreviewImage != null && largePreviewImage != null ? true : false;
			StructProperty displayAssetPathObject = (StructProperty) baseObject.getPropertyByName("DisplayAssetPath");
			displayAssetPath = displayAssetPathObject != null ? ((FSoftObjectPath)displayAssetPathObject.getStruct().getStructType()).getAssetPathName() : null;
			bUsesDisplayPath = displayAssetPath != null ? true : false;
		} catch(Exception e) {
			throw new ReadException("Unable to read Item Definition", Ar.Tell());
		}
	}



	/**
	 * 
	 */
	public AthenaItemDefinition() {
		
	}
	


	public void setRarity(String rarity) {
		this.rarity = rarity;
	}


	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}


	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getRarity() {
		return rarity;
	}


	public String getDisplayName() {
		return displayName;
	}


	public String getShortDescription() {
		return shortDescription;
	}


	public String getDescription() {
		return description;
	}


	public List<String> getGameplayTags() {
		return gameplayTags;
	}
	
}
