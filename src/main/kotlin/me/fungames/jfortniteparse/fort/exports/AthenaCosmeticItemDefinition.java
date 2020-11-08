package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.UProperty;

import java.util.List;

public class AthenaCosmeticItemDefinition extends FortAccountItemDefinition {
    public boolean bIsShuffleTile = false;
    public Boolean bIsOwnedByCampaignHero;
    public Boolean bHasMoreThanOneCharacterPartVariant;
    public Boolean bHideIfNotOwned;
    public Boolean bInitializedConfiguredDynamicInstallBundles;
    public Boolean bDynamicInstallBundlesError;
    public Boolean bDynamicInstallBundlesComplete;
    public Double DynamicInstallBundlesUpdateStartTime;
    public EVariantUnlockType VariantUnlockType;
    public FRotator PreviewPawnRotationOffset;
    //public List<FoleySoundLibrary> FoleyLibraries;
    @UProperty(skipPrevious = 1)
    public FGameplayTagContainer DisallowedCosmeticTags;
    public FGameplayTagContainer MetaTags;
    public List<FName> /*FName*/ VariantChannelsToNeverSendToMCP;
    //public List<AthenaCosmeticMaterialOverride> MaterialOverrides;
    @UProperty(skipPrevious = 1)
    public FGameplayTagContainer ObservedPlayerStats;
    public List<FPackageIndex> /*List<UFortMontageItemDefinitionBase>*/ BuiltInEmotes;
    public List<FPackageIndex> /*List<UFortCosmeticVariant>*/ ItemVariants;
    public FName /*FName*/ VariantChannelToUseForThumbnails;
    //public List<FortCosmeticVariantPreview> ItemVariantPreviews;
    @UProperty(skipPrevious = 1)
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
}
