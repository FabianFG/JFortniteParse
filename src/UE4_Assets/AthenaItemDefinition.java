/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import UE4.FArchive;
import UE4_Assets.FPropertyTagType.EnumProperty;
import UE4_Assets.FPropertyTagType.ObjectProperty;
import UE4_Assets.FPropertyTagType.SoftObjectProperty;
import UE4_Assets.FPropertyTagType.StructProperty;
import UE4_Assets.FPropertyTagType.TextProperty;
import UE4_Localization.Locres;

/**
 * @author FunGames
 *
 */
public class AthenaItemDefinition implements Cloneable {
	private UObject baseObject;
	private String rarity;
	private String displayName;
	private String shortDescription; // Mainly equals the type e.g. "Outfit", "Backbling"
	private String description;
	private List<String> gameplayTags; // Cosmetic Source and Set
	private boolean bHasIcons;
	private String smallPreviewImage;
	private String largePreviewImage;
	private String displayAssetPath;
	private boolean bUsesDisplayPath;
	private boolean bUsesHeroDefinition;
	private String heroDefinitionPackage;
	private String weaponDefinitionPackage;
	private boolean bUsesWeaponDefinition;
	private Optional<String> setTag = Optional.empty();
	private Optional<String> sourceTag = Optional.empty();
	private Optional<String> userFacingTag = Optional.empty();
	private List<FortCosmeticVariant> variants = new ArrayList<>();
	private boolean variantsLoaded = false;

	public List<FortCosmeticVariant> getVariants() {
		return variants;
	}

	public void addVariants(FortCosmeticVariant variants) {
		this.variants.add(variants);
	}

	public boolean usesHeroDefinition() {
		return bUsesHeroDefinition;
	}

	public String getWeaponDefinitionPackage() {
		return weaponDefinitionPackage;
	}

	public boolean usesWeaponDefinition() {
		return bUsesWeaponDefinition;
	}

	public void setVariants(List<FortCosmeticVariant> variants) {
		this.variants = variants;
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

	public AthenaItemDefinition(FArchive Ar, NameMap nameMap, ImportMap importMap, String exportName)
			throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, exportName);
		try {
			ObjectProperty heroDefinitionObject = (ObjectProperty) baseObject.getPropertyByName("HeroDefinition");
			String heroDefinitionPackageName = heroDefinitionObject != null
					? heroDefinitionObject.getStruct().getImportName()
					: null;
			heroDefinitionPackage = null;
			bUsesHeroDefinition = false;
			if (heroDefinitionPackageName != null) {
				for (FObjectImport importObject : importMap.getEntrys()) {
					if (importObject.getClassName().equals("Package")) {
						if (heroDefinitionPackageName != null) {
							if (importObject.getObjectName().endsWith(heroDefinitionPackageName)) {
								heroDefinitionPackage = importObject.getObjectName();
								bUsesHeroDefinition = true;
								break;
							}
						}
					}
				}
			}
			ObjectProperty weaponDefinitionObject = (ObjectProperty) baseObject.getPropertyByName("WeaponDefinition");
			String weaponDefinitionPackageName = weaponDefinitionObject != null
					? weaponDefinitionObject.getStruct().getImportName()
					: null;
			weaponDefinitionPackage = null;
			bUsesWeaponDefinition = false;
			if (weaponDefinitionPackageName != null) {
				for (FObjectImport importObject : importMap.getEntrys()) {
					if (importObject.getClassName().equals("Package")) {
						if (weaponDefinitionPackageName != null) {
							if (importObject.getObjectName().endsWith(weaponDefinitionPackageName)) {
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
			displayName = displayNameObject != null ? displayNameObject.getText().getString() : null;
			TextProperty shortDescriptionObject = (TextProperty) baseObject.getPropertyByName("ShortDescription");
			shortDescription = shortDescriptionObject != null ? shortDescriptionObject.getText().getString() : null;
			TextProperty descriptionObject = (TextProperty) baseObject.getPropertyByName("Description");
			description = descriptionObject != null ? descriptionObject.getText().getString() : null;
			StructProperty gameplayTagsObject = (StructProperty) baseObject.getPropertyByName("GameplayTags");
			gameplayTags = gameplayTagsObject != null
					? ((FGameplayTagContainer) gameplayTagsObject.getStruct().getStructType()).getGameplayTags()
					: null;
			if(gameplayTags != null) {
				setTag = gameplayTags.stream().filter(tag -> {
					return tag.startsWith("Cosmetics.Set");
				}).findFirst();
				sourceTag = gameplayTags.stream().filter(tag -> {
					return tag.startsWith("Cosmetics.Source");
				}).findFirst();
				userFacingTag = gameplayTags.stream().filter(tag -> {
					return tag.startsWith("Cosmetics.UserFacingFlags");
				}).findFirst();
			} else {
				setTag = Optional.empty();
				sourceTag = Optional.empty();
				userFacingTag = Optional.empty();
			}
			SoftObjectProperty smallPreviewImageObject = (SoftObjectProperty) baseObject
					.getPropertyByName("SmallPreviewImage");
			smallPreviewImage = smallPreviewImageObject != null ? smallPreviewImageObject.getText().getAssetPathName()
					: null;
			SoftObjectProperty largePreviewImageObject = (SoftObjectProperty) baseObject
					.getPropertyByName("LargePreviewImage");
			largePreviewImage = largePreviewImageObject != null ? largePreviewImageObject.getText().getAssetPathName()
					: null;

			if (largePreviewImage == null) {
				// Used for Emojis, equals large image object
				SoftObjectProperty spriteSheetObjectProperty = (SoftObjectProperty) baseObject
						.getPropertyByName("SpriteSheet");
				largePreviewImage = spriteSheetObjectProperty != null
						? spriteSheetObjectProperty.getText().getAssetPathName()
						: null;
			}
			bHasIcons = smallPreviewImage != null && largePreviewImage != null ? true : false;
			StructProperty displayAssetPathObject = (StructProperty) baseObject.getPropertyByName("DisplayAssetPath");
			displayAssetPath = displayAssetPathObject != null
					? ((FSoftObjectPath) displayAssetPathObject.getStruct().getStructType()).getAssetPathName()
					: null;
			bUsesDisplayPath = displayAssetPath != null ? true : false;
		} catch (Exception e) {
			throw new ReadException("Unable to read Item Definition", Ar.Tell());
		}
	}

	public Optional<String> getSetTag() {
		return setTag;
	}

	public void setSetTag(Optional<String> setTag) {
		this.setTag = setTag;
	}

	public void setSourceTag(Optional<String> sourceTag) {
		this.sourceTag = sourceTag;
	}

	public void setUserFacingTag(Optional<String> userFacingTag) {
		this.userFacingTag = userFacingTag;
	}

	public Optional<String> getSourceTag() {
		return sourceTag;
	}

	public Optional<String> getUserFacingTag() {
		return userFacingTag;
	}

	@Override
	public AthenaItemDefinition clone() {
		AthenaItemDefinition def = new AthenaItemDefinition();
		def.setBaseObject(this.baseObject);
		def.setbHasIcons(this.bHasIcons);
		def.setbUsesDisplayPath(this.bUsesDisplayPath);
		def.setbUsesHeroDefinition(this.bUsesHeroDefinition);
		def.setbUsesWeaponDefinition(this.bUsesWeaponDefinition);
		def.setDescription(this.description);
		def.setDisplayAssetPath(this.displayAssetPath);
		def.setDisplayName(this.displayName);
		def.setGameplayTags(this.gameplayTags);
		def.setHeroDefinitionPackage(this.heroDefinitionPackage);
		def.setLargePreviewImage(this.largePreviewImage);
		def.setRarity(this.rarity);
		def.setShortDescription(this.shortDescription);
		def.setSmallPreviewImage(this.smallPreviewImage);
		def.setWeaponDefinitionPackage(this.weaponDefinitionPackage);
		def.setSetTag(this.setTag);
		def.setSourceTag(this.sourceTag);
		def.setUserFacingTag(this.userFacingTag);
		def.setVariants(this.variants);
		def.setVariantsLoaded(this.variantsLoaded);
		return def;
	}

	public void setBaseObject(UObject baseObject) {
		this.baseObject = baseObject;
	}

	public void setGameplayTags(List<String> gameplayTags) {
		this.gameplayTags = gameplayTags;
	}

	public void setbHasIcons(boolean bHasIcons) {
		this.bHasIcons = bHasIcons;
	}

	public void setSmallPreviewImage(String smallPreviewImage) {
		this.smallPreviewImage = smallPreviewImage;
	}

	public void setLargePreviewImage(String largePreviewImage) {
		this.largePreviewImage = largePreviewImage;
	}

	public void setDisplayAssetPath(String displayAssetPath) {
		this.displayAssetPath = displayAssetPath;
	}

	public void setbUsesDisplayPath(boolean bUsesDisplayPath) {
		this.bUsesDisplayPath = bUsesDisplayPath;
	}

	public void setbUsesHeroDefinition(boolean bUsesHeroDefinition) {
		this.bUsesHeroDefinition = bUsesHeroDefinition;
	}

	public void setHeroDefinitionPackage(String heroDefinitionPackage) {
		this.heroDefinitionPackage = heroDefinitionPackage;
	}

	public void setWeaponDefinitionPackage(String weaponDefinitionPackage) {
		this.weaponDefinitionPackage = weaponDefinitionPackage;
	}

	public void setbUsesWeaponDefinition(boolean bUsesWeaponDefinition) {
		this.bUsesWeaponDefinition = bUsesWeaponDefinition;
	}

	public AthenaItemDefinition forLanguage(Optional<Locres> locres) {
		AthenaItemDefinition def = this.clone();
		if (def.getBaseObject() != null) {
			TextProperty displayNameObject = (TextProperty) def.getBaseObject().getPropertyByName("DisplayName");
			def.setDisplayName(displayNameObject != null ? displayNameObject.getText().forLocres(locres) : null);
			TextProperty shortDescriptionObject = (TextProperty) def.getBaseObject()
					.getPropertyByName("ShortDescription");
			def.setShortDescription(
					shortDescriptionObject != null ? shortDescriptionObject.getText().forLocres(locres) : null);
			TextProperty descriptionObject = (TextProperty) def.getBaseObject().getPropertyByName("Description");
			def.setDescription(descriptionObject != null ? descriptionObject.getText().forLocres(locres) : null);
			
			List<FortCosmeticVariant> vars = new ArrayList<>();
			def.getVariants().forEach(variant -> {
				vars.add(variant.forLanguage(locres));
			});
			def.setVariants(vars);
		}
		return def;
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

	public boolean isVariantsLoaded() {
		return variantsLoaded;
	}

	public void setVariantsLoaded(boolean variantsLoaded) {
		this.variantsLoaded = variantsLoaded;
	}

}
