package me.fungames.jfortniteparse.fort.exports;

import kotlin.UInt;
import me.fungames.jfortniteparse.fort.exports.variants.FortCosmeticVariant;
import me.fungames.jfortniteparse.fort.objects.CosmeticVariantInfo;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;
import java.util.Map;

public class AthenaCosmeticItemDefinition extends FortAccountItemDefinition {
    public boolean bIsShuffleTile = false;
    public Boolean bIsOwnedByCampaignHero;
    public Boolean bHasMoreThanOneCharacterPartVariant;
    public Boolean bHideIfNotOwned;
    public Boolean bInitializedConfiguredDynamicInstallBundles;
    public Boolean bDynamicInstallBundlesError;
    public Boolean bDynamicInstallBundlesCancelled;
    public Boolean bDynamicInstallBundlesComplete;
    public Double DynamicInstallBundlesUpdateStartTime;
    public UInt DynamicInstallBundleRequestRefCount;
    public Integer DynamicInstallBundleRequestRetryCount;
    public EVariantUnlockType VariantUnlockType;
    public FRotator PreviewPawnRotationOffset;
    public List<FPackageIndex /*FoleySoundLibrary*/> FoleyLibraries;
    public FGameplayTagContainer DisallowedCosmeticTags;
    public FGameplayTagContainer MetaTags;
    public List<FGameplayTag> VariantChannelsToNeverSendToMCP;
    public Map<CosmeticVariantInfo, FSoftObjectPath> ReactivePreviewDrivers;
    public List<AthenaCosmeticMaterialOverride> MaterialOverrides;
    public FGameplayTagContainer ObservedPlayerStats;
    public List<FPackageIndex> /*List<UFortMontageItemDefinitionBase>*/ BuiltInEmotes;
    public List<FortCosmeticVariant> ItemVariants;
    public FGameplayTag VariantChannelToUseForThumbnails;
    public List<FortCosmeticVariantPreview> ItemVariantPreviews;
    public FText DirectAquisitionStyleDisclaimerOverride;
    //public List<FortCosmeticAdaptiveStatPreview> ItemObservedStatPreviews;
    @UProperty(skipPrevious = 1)
    public FText UnlockRequirements;
    public FSoftObjectPath UnlockingItemDef;
    public FSoftObjectPath /*SoftClassPath*/ ItemPreviewActorClass;
    public FSoftObjectPath ItemPreviewParticleSystem;
    public FSoftObjectPath ItemPreviewMontage_Male;
    public FSoftObjectPath ItemPreviewMontage_Female;
    public FSoftObjectPath ItemPreviewHero;
    public List<FName> ConfiguredDynamicInstallBundles;
    public List<FName> PendingDynamicInstallBundles;
    public FGameplayTagContainer ExclusiveRequiresOutfitTags;
    public FText CustomExclusiveCallout;
    public FText ExclusiveDesciption;
    public FSoftObjectPath ExclusiveIcon;

    public enum EVariantUnlockType {
        UnlockAll, ExclusiveChoice
    }

    @UStruct
    public static class WeirdVariantStruct {
        public FGameplayTag Unknown0, Unknown1;
    }

    @UStruct
    public static class AthenaCosmeticMaterialOverride {
        public FName ComponentName;
        public Integer MaterialOverrideIndex;
        public FSoftObjectPath OverrideMaterial;
    }

    @UStruct
    public static class FortCosmeticVariantPreview {
        public FText UnlockCondition;
        public Float PreviewTime;
        public List<McpVariantChannelInfo> VariantOptions;
        public List<FortCosmeticVariantPreviewElement> AdditionalItems;
    }

    @UStruct
    public static class McpVariantChannelInfo extends CosmeticVariantInfo {
        public FGameplayTagContainer OwnedVariantTags;
        public FPackageIndex /*FortItemDefinition*/ ItemVariantIsUsedFor;
        public String CustomData;
    }

    @UStruct
    public static class FortCosmeticVariantPreviewElement {
        public List<McpVariantChannelInfo> VariantOptions;
        public FPackageIndex /*AthenaCosmeticItemDefinition*/ Item;
    }
}
