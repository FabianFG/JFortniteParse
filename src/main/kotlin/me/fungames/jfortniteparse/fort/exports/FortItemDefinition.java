package me.fungames.jfortniteparse.fort.exports;

import kotlin.Lazy;
import me.fungames.jfortniteparse.fort.enums.*;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.exports.FCurveTableRowHandle;
import me.fungames.jfortniteparse.ue4.assets.objects.FInstancedStruct;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class FortItemDefinition extends ItemDefinitionBase {
    //public MulticastInlineDelegateProperty OnItemCountChanged;
    @UProperty(skipPrevious = 1)
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
    public FText SearchTags;
    public FGameplayTagContainer GameplayTags;
    public FGameplayTagContainer AutomationTags;
    public FGameplayTagContainer SecondaryCategoryOverrideTags;
    public FGameplayTagContainer TertiaryCategoryOverrideTags;
    public FScalableFloat MaxStackSize;
    public FScalableFloat PurchaseItemLimit;
    public Float FrontendPreviewScale;
    public FSoftObjectPath /*SoftClassPath*/ TooltipClass;
    public FSoftObjectPath StatList;
    public FCurveTableRowHandle RatingLookup;
    public FSoftObjectPath WidePreviewImage;
    public FSoftObjectPath SmallPreviewImage;
    public FSoftObjectPath LargePreviewImage;
    public FSoftObjectPath DisplayAssetPath;
    public Lazy<FortItemSeriesDefinition> Series;
    public FVector FrontendPreviewPivotOffset;
    public FRotator FrontendPreviewInitialRotation;
    public FSoftObjectPath FrontendPreviewMeshOverride;
    public FSoftObjectPath FrontendPreviewSkeletalMeshOverride;
    public List<FInstancedStruct> DataList;

    public FName getSet() {
        return GameplayTags != null ? GameplayTags.getValue("Cosmetics.Set") : null;
    }

    public FName getSource() {
        return GameplayTags != null ? GameplayTags.getValue("Cosmetics.Source") : null;
    }

    public FName getUserFacingFlags() {
        return GameplayTags != null ? GameplayTags.getValue("Cosmetics.UserFacingFlags") : null;
    }
}
