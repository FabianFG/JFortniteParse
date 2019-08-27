/**
 * 
 */
package UE4_Assets.exports;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import UE4.deserialize.NewableWithUObject;
import UE4_Assets.FText;
import UE4_Assets.ImportMap;
import UE4_Localization.Locres;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author FunGames
 *
 */
@Data
@NoArgsConstructor
public class AthenaItemDefinition implements Cloneable, NewableWithUObject {
	private UObject baseObject;
	private String rarity;
	private FText displayNameFText;
	private FText shortDescriptionFText; // Mainly equals the type e.g. "Outfit", "Backbling"
	private FText descriptionFText;
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

	public void addVariants(FortCosmeticVariant variants) {
		this.variants.add(variants);
	}
	
	@Override
	public void init(UObject uObject, Gson gson, ImportMap importMap) {
		JsonObject ob = (JsonObject) gson.toJsonTree(uObject);
		setTag = Optional.empty();
		sourceTag = Optional.empty();
		userFacingTag = Optional.empty();
		Optional.ofNullable(ob.get("HeroDefinition")).ifPresent(e -> {
			heroDefinitionPackage = importMap.getPackageImportByName(e.getAsString());
			bUsesHeroDefinition = true;
		});
		Optional.ofNullable(ob.get("WeaponDefinition")).ifPresent(e -> {
			weaponDefinitionPackage = importMap.getPackageImportByName(e.getAsString());
			bUsesWeaponDefinition = true;
		});
		Optional.ofNullable(ob.get("Rarity")).ifPresent(e -> {
			rarity = e.getAsString();
		});
		Optional.ofNullable(ob.get("DisplayName")).ifPresent(e -> {
			displayNameFText = new FText(e.getAsJsonObject().get("namespace").getAsString(), e.getAsJsonObject().get("key").getAsString(), e.getAsJsonObject().get("text").getAsString());
			displayName = displayNameFText.getString();
		});
		Optional.ofNullable(ob.get("ShortDescription")).ifPresent(e -> {
			shortDescriptionFText = new FText(e.getAsJsonObject().get("namespace").getAsString(), e.getAsJsonObject().get("key").getAsString(), e.getAsJsonObject().get("text").getAsString());
			shortDescription = shortDescriptionFText.getString();
		});
		Optional.ofNullable(ob.get("Description")).ifPresent(e -> {
			descriptionFText = new FText(e.getAsJsonObject().get("namespace").getAsString(), e.getAsJsonObject().get("key").getAsString(), e.getAsJsonObject().get("text").getAsString());
			description = descriptionFText.getString();
		});
		Optional.ofNullable(ob.get("GameplayTags")).ifPresent(e -> {
			gameplayTags = new ArrayList<>();
			e.getAsJsonArray().forEach(e1 -> {
				gameplayTags.add(e1.getAsString());
			});
			setTag = gameplayTags.stream().filter(tag -> {
				return tag.startsWith("Cosmetics.Set");
			}).findFirst();
			sourceTag = gameplayTags.stream().filter(tag -> {
				return tag.startsWith("Cosmetics.Source");
			}).findFirst();
			userFacingTag = gameplayTags.stream().filter(tag -> {
				return tag.startsWith("Cosmetics.UserFacingFlags");
			}).findFirst();
		});
		Optional.ofNullable(ob.get("SmallPreviewImage")).ifPresent(e -> {
			smallPreviewImage = e.getAsJsonObject().get("asset_path").getAsString();
		});
		Optional.ofNullable(ob.get("LargePreviewImage")).ifPresent(e -> {
			largePreviewImage = e.getAsJsonObject().get("asset_path").getAsString();
		});
		if(largePreviewImage == null) {
			// Used for Emojis, equals large image object
			Optional.ofNullable(ob.get("SpriteSheet")).ifPresent(e -> {
				largePreviewImage = e.getAsJsonObject().get("asset_path").getAsString();
			});
		}
		bHasIcons = smallPreviewImage != null && largePreviewImage != null;
		Optional.ofNullable(ob.get("DisplayAssetPath")).ifPresent(e -> {
			displayAssetPath = e.getAsJsonObject().get("asset_path").getAsString();
			bUsesDisplayPath = true;
		});
		variants = new ArrayList<>();
	}

	@Override
	public AthenaItemDefinition clone() {
		AthenaItemDefinition def = new AthenaItemDefinition();
		def.setBaseObject(this.baseObject);
		def.setBHasIcons(this.bHasIcons);
		def.setBUsesDisplayPath(this.bUsesDisplayPath);
		def.setBUsesHeroDefinition(this.bUsesHeroDefinition);
		def.setBUsesWeaponDefinition(this.bUsesWeaponDefinition);
		def.setDescription(this.description);
		def.setDescriptionFText(this.descriptionFText);
		def.setDisplayAssetPath(this.displayAssetPath);
		def.setDisplayName(this.displayName);
		def.setDisplayNameFText(this.displayNameFText);
		def.setGameplayTags(this.gameplayTags);
		def.setHeroDefinitionPackage(this.heroDefinitionPackage);
		def.setLargePreviewImage(this.largePreviewImage);
		def.setRarity(this.rarity);
		def.setShortDescription(this.shortDescription);
		def.setShortDescriptionFText(this.shortDescriptionFText);
		def.setSmallPreviewImage(this.smallPreviewImage);
		def.setWeaponDefinitionPackage(this.weaponDefinitionPackage);
		def.setSetTag(this.setTag);
		def.setSourceTag(this.sourceTag);
		def.setUserFacingTag(this.userFacingTag);
		def.setVariants(this.variants);
		def.setVariantsLoaded(this.variantsLoaded);
		return def;
	}

	public AthenaItemDefinition forLanguage(Optional<Locres> locres) {
		AthenaItemDefinition def = this.clone();
		if (def.getBaseObject() != null) {
			if(def.getDisplayNameFText() != null) {
				def.setDisplayNameFText(def.getDisplayNameFText().cloneForLocres(locres));
				def.setDisplayName(def.getDisplayNameFText().getString());
			}
			if(def.getShortDescriptionFText() != null) {
				def.setShortDescriptionFText(def.getShortDescriptionFText().cloneForLocres(locres));
				def.setShortDescription(def.getShortDescriptionFText().getString());
			}
			if(def.getDescriptionFText() != null) {
				def.setDescriptionFText(def.getDescriptionFText().cloneForLocres(locres));
				def.setDescription(def.getDescriptionFText().getString());
			}			
			List<FortCosmeticVariant> vars = new ArrayList<>();
			def.getVariants().forEach(variant -> {
				vars.add(variant.forLanguage(locres));
			});
			def.setVariants(vars);
		}
		return def;
	}

}
