package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.enums.*;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortItemDefinition extends McpItemDefinitionBase {
    public List<FortCosmeticVariant> ItemVariants;
    public EFortRarity Rarity = EFortRarity.Uncommon;
    public EFortItemType ItemType;
    public EFortItemType PrimaryAssetIdItemTypeOverride;
    public EFortInventoryFilter FilterOverride;
    public EFortItemTier Tier;
    public EFortItemTier MaxTier;
    public EFortTemplateAccess Access;
    public Boolean bIsAccountItem;
    public Boolean bNeverPersisted;
    public Boolean bAllowMultipleStacks;
    public Boolean bAutoBalanceStacks;
    public Boolean bForceAutoPickup;
    public boolean bInventorySizeLimited = true;
    public FText ItemTypeNameOverride;
    public FText DisplayName;
    public FText ShortDescription;
    public FText Description;
    public FText DisplayNamePrefix;
    public FText SearchTags;
    public FGameplayTagContainer GameplayTags;
    public FGameplayTagContainer AutomationTags;
    public FGameplayTagContainer SecondaryCategoryOverrideTags;
    public FGameplayTagContainer TertiaryCategoryOverrideTags;
    /*public FScalableFloat MaxStackSize;
    public FScalableFloat PurchaseItemLimit;*/
    @UProperty(skipPrevious = 2)
    public Float FrontendPreviewScale;
    public FSoftObjectPath /*SoftClassPath*/ TooltipClass;
    public FSoftObjectPath StatList;
    //public FCurveTableRowHandle RatingLookup;
    @UProperty(skipPrevious = 1)
    public FSoftObjectPath WidePreviewImage;
    public FSoftObjectPath SmallPreviewImage;
    public FSoftObjectPath LargePreviewImage;
    public FSoftObjectPath DisplayAssetPath;
    public FPackageIndex Series;
    public FVector FrontendPreviewPivotOffset;
    public FRotator FrontendPreviewInitialRotation;
    public FSoftObjectPath FrontendPreviewMeshOverride;
    public FSoftObjectPath FrontendPreviewSkeletalMeshOverride;

    public FName getSet() {
        return GameplayTags.getValue("Cosmetics.Set");
    }

    public FName getSource() {
        return GameplayTags.getValue("Cosmetics.Source");
    }

    public FName getUserFacingFlags() {
        return GameplayTags.getValue("Cosmetics.UserFacingFlags");
    }
}
