package me.fungames.jfortniteparse.fort.exports;

import kotlin.UByte;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.objects.FDataTableCategoryHandle;
import me.fungames.jfortniteparse.ue4.objects.FDataTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FTransform;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateBrush;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftClassPath;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

public class FortWorldItemDefinition extends FortItemDefinition {
    public FGameplayTagContainer RequiredEquipTags;
    //public List<FFortPickupRestrictionLists> PickupRestrictionListEntry;
    @UProperty(skipPrevious = 1)
    public EWorldItemDropBehavior DropBehavior;
    public Boolean bIgnoreRespawningForDroppingAsPickup;
    public Boolean bCanAutoEquipByClass;
    public Boolean bPersistInInventoryWhenFinalStackEmpty;
    public Boolean bSupportsQuickbarFocus;
    public Boolean bSupportsQuickbarFocusForGamepadOnly;
    public Boolean bShouldActivateWhenFocused;
    public Boolean bForceFocusWhenAdded;
    public Boolean bForceIntoOverflow;
    public Boolean bForceStayInOverflow;
    public Boolean bDropCurrentItemOnOverflow;
    public Boolean bShouldShowItemToast;
    public Boolean bShowDirectionalArrowWhenFarOff;
    public Boolean bCanBeDropped;
    public Boolean bCanBeReplacedByPickup;
    public Boolean bItemCanBeStolen;
    public Boolean bCanBeDepositedInStorageVault;
    public Boolean bItemHasDurability;
    public Boolean bAllowedToBeLockedInInventory;
    public Boolean bOverridePickupMeshTransform;
    public Boolean bAlwaysCountForCollectionQuest;
    public Boolean bDropOnDeath;
    public Boolean bDropOnLogout;
    public Boolean bDropOnDBNO;
    public Boolean bDoesNotNeedSourceSchematic;
    public Boolean bUsesGoverningTags;
    public Integer DropCount;
    public Float MiniMapViewableDistance;
    public FSlateBrush MiniMapIconBrush;
    public FText OwnerPickupText;
    public FDataTableCategoryHandle LootLevelData;
    public FTransform PickupMeshTransform;
    public Boolean bIsPickupASpecialActor;
    public FGameplayTag SpecialActorPickupTag;
    //public List<FSpecialActorSingleStatData> SpecialActorPickupStatList;
    @UProperty(skipPrevious = 1)
    public FName PickupSpecialActorUniqueID;
    public FSlateBrush PickupMinimapIconBrush;
    public FVector2D PickupMinimapIconScale;
    public FSlateBrush PickupCompassIconBrush;
    public FVector2D PickupCompassIconScale;
    public FScalableFloat PickupDespawnTime;
    public FScalableFloat InStormPickupDespawnTime;
    public FScalableFloat NetworkCullDistanceOverride;
    public FSoftObjectPath PickupStaticMesh;
    public FSoftObjectPath PickupSkeletalMesh;
    public FSoftClassPath PickupEffectOverride;
    public FSoftObjectPath PickupSound;
    public FSoftObjectPath PickupByNearbyPawnSound;
    public FSoftObjectPath DropSound;
    public FSoftObjectPath DroppedLoopSound;
    public FSoftObjectPath LandedSound;
    public FDataTableRowHandle DisassembleRecipe;
    public Float DisassembleDurabilityDegradeMinLootPercent;
    public Float DisassembleDurabilityDegradeMaxLootPercent;
    public Integer PreferredQuickbarSlot;
    public Integer MinLevel;
    public Integer MaxLevel;
    public UByte NumberOfSlotsToTake;

    public enum EWorldItemDropBehavior {
        DropAsPickup,
        DestroyOnDrop,
        DropAsPickupDestroyOnEmpty,
        DropAsPickupEvenWhenEmpty
    }
}
