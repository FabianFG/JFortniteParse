package me.fungames.jfortniteparse.fort.objects;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

@UStruct
public class BaseVariantDef {
    public Boolean bStartUnlocked;
    public Boolean bIsDefault;
    public Boolean bHideIfNotOwned;
    public FGameplayTag CustomizationVariantTag;
    public FText VariantName;
    public FSoftObjectPath PreviewImage;
    public FText UnlockRequirements;
    public FSoftObjectPath UnlockingItemDef;

    public String getBackendVariantName() {
        return CustomizationVariantTag != null ? CustomizationVariantTag.toString().substring("Cosmetics.Variant.Property.".length()) : null;
    }

    @UStruct
    public static class CosmeticMetaTagContainer {
        public FGameplayTagContainer MetaTagsToApply;
        public FGameplayTagContainer MetaTagsToRemove;
    }
}
