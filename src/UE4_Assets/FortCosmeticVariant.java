/**
 * 
 */
package UE4_Assets;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import UE4.FArchive;
import UE4_Assets.FPropertyTagType.ArrayProperty;
import UE4_Assets.FPropertyTagType.BoolProperty;
import UE4_Assets.FPropertyTagType.StructProperty;
import UE4_Localization.Locres;

/**
 * @author FunGames
 *
 */
public class FortCosmeticVariant {
	private FText variantChannelName;
	private String variantChannelTag;
	private List<CosmeticVariantContainer> variants = new ArrayList<>();
	private UObject baseObject;
	
	public FortCosmeticVariant() {}
	
	public FText getVariantChannelName() {
		return variantChannelName;
	}
	public void setVariantChannelName(FText variantChannelName) {
		this.variantChannelName = variantChannelName;
	}
	public String getVariantChannelTag() {
		return variantChannelTag;
	}
	public void setVariantChannelTag(String variantChannelTag) {
		this.variantChannelTag = variantChannelTag;
	}
	public UObject getBaseObject() {
		return baseObject;
	}
	public void setBaseObject(UObject baseObject) {
		this.baseObject = baseObject;
	}
	public List<CosmeticVariantContainer> getVariants() {
		return variants;
	}
	public void setVariants(List<CosmeticVariantContainer> variants) {
		this.variants = variants;
	}
	
	public FortCosmeticVariant(FArchive Ar, NameMap nameMap, ImportMap importMap, String className) throws ReadException {
		baseObject = new UObject(Ar, nameMap, importMap, className);
		FPropertyTagType channelNameTag = baseObject.getPropertyByName("VariantChannelName");
		this.variantChannelName = channelNameTag != null && channelNameTag instanceof FPropertyTagType.TextProperty ? ((FPropertyTagType.TextProperty) channelNameTag).getText() : null;
		FPropertyTagType channelTagTag = baseObject.getPropertyByName("VariantChannelTag");
		if(channelTagTag instanceof FPropertyTagType.StructProperty) {
			UScriptStruct tagStruct = ((FPropertyTagType.StructProperty) channelTagTag).getStruct();
			if(tagStruct.getStructType() instanceof FStructFallback) {
				FStructFallback props = (FStructFallback) tagStruct.getStructType();
				Optional<FPropertyTag> tagTag = props.getPropertyByName("TagName");
				tagTag.ifPresent(tag -> {
					if(tag.getTag() instanceof FPropertyTagType.NameProperty) {
						this.variantChannelTag = ((FPropertyTagType.NameProperty)tag.getTag()).getText();
					}
				});
			}
		}
		
		String searchTag = null;
		switch(className) {
		case "FortCosmeticCharacterPartVariant":
			searchTag = "PartOptions";
			break;
		case "FortCosmeticMaterialVariant":
			searchTag = "MaterialOptions";
			break;
		case "FortCosmeticParticleVariant":
			searchTag = "ParticleOptions";
			break;
		default:
			searchTag = className.replace("FortCosmetic", "").replace("Variant", "");
			break;
		}
		
		FPropertyTagType optionsTag = baseObject.getPropertyByName(searchTag);
		if(optionsTag == null)
			return;
		if(optionsTag instanceof ArrayProperty) {
			ArrayProperty partArray = (ArrayProperty) optionsTag;
			for(FPropertyTagType part : partArray.getNumber().getData()) {
				if(part instanceof StructProperty && ((StructProperty) part).getStruct().getStructType() instanceof FStructFallback) {
					FStructFallback variantProperties = (FStructFallback)((StructProperty) part).getStruct().getStructType();
					CosmeticVariantContainer container = new CosmeticVariantContainer();
					Optional<FPropertyTag> bStartUnlocked = variantProperties.getPropertyByName("bStartUnlocked");
					bStartUnlocked.ifPresent(tag -> {
						if(tag.getTag() instanceof BoolProperty) {
							container.setbStartUnlocked(((BoolProperty) tag.getTag()).getBool());
						}
					});
					Optional<FPropertyTag> bIsDefault = variantProperties.getPropertyByName("bIsDefault");
					bIsDefault.ifPresent(tag -> {
						if(tag.getTag() instanceof BoolProperty) {
							container.setbIsDefault(((BoolProperty) tag.getTag()).getBool());
						}
					});
					Optional<FPropertyTag> bHideIfNotOwned = variantProperties.getPropertyByName("bHideIfNotOwned");
					bHideIfNotOwned.ifPresent(tag -> {
						if(tag.getTag() instanceof BoolProperty) {
							container.setbHideIfNotOwned(((BoolProperty) tag.getTag()).getBool());
						}
					});
					Optional<FPropertyTag> CustomizationVariantTag = variantProperties.getPropertyByName("CustomizationVariantTag");
					CustomizationVariantTag.ifPresent(tag -> {
						if(tag.getTag() instanceof FPropertyTagType.StructProperty) {
							UScriptStruct tagStruct = ((FPropertyTagType.StructProperty) tag.getTag()).getStruct();
							if(tagStruct.getStructType() instanceof FStructFallback) {
								FStructFallback props = (FStructFallback) tagStruct.getStructType();
								Optional<FPropertyTag> tagTag = props.getPropertyByName("TagName");
								tagTag.ifPresent(tagTagTag -> {
									if(tagTagTag.getTag() instanceof FPropertyTagType.NameProperty) {
										container.setCustomizationVariantTag(((FPropertyTagType.NameProperty)tagTagTag.getTag()).getText());
									}
								});
							}
						}
					});
					Optional<FPropertyTag> VariantName = variantProperties.getPropertyByName("VariantName");
					VariantName.ifPresent(tag -> {
						container.setVariantName(tag.getTag() != null && tag.getTag() instanceof FPropertyTagType.TextProperty ? ((FPropertyTagType.TextProperty) tag.getTag()).getText() : null);
					});
					Optional<FPropertyTag> PreviewImage = variantProperties.getPropertyByName("PreviewImage");
					PreviewImage.ifPresent(tag -> {
						container.setPreviewImage(tag.getTag() != null && tag.getTag() instanceof FPropertyTagType.SoftObjectProperty ? ((FPropertyTagType.SoftObjectProperty) tag.getTag()).getText().getAssetPathName() : null);
					});
					variants.add(container);
				}
			}
		}
	}
	/**
	 * @param locres
	 * @return
	 */
	public FortCosmeticVariant forLanguage(Optional<Locres> locres) {
		if(variantChannelName != null)
			this.variantChannelName = this.variantChannelName.cloneForLocres(locres);
		variants.forEach(varContainer -> {
			varContainer.setVariantName(varContainer.getVariantName().cloneForLocres(locres));
		});
		return this;
	}
	
}
