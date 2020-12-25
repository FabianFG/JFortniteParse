package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.UDataAsset;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortItemVariantData extends UDataAsset {
    public List<FortVariantData> Variants;

    @UStruct
    public static class FortVariantData {
        public FText VariantName;
        public List<FSoftObjectPath> OverrideMaterials;
        public FScalableFloat Weight;
        public FGameplayTag ItemFilterTag;
        public FGameplayTag CollectionTag;
        public FGameplayTag AnalyticsTag;
        public FGameplayTagContainer POITags;
        public FGameplayTagContainer TODTags;
        public FGameplayTagContainer RequiredTags;
    }
}
