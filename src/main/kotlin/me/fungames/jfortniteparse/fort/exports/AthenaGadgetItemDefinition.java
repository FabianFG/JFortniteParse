package me.fungames.jfortniteparse.fort.exports;

import me.fungames.jfortniteparse.fort.objects.FortCreativeTagsHelper;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateBrush;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class AthenaGadgetItemDefinition extends FortGadgetItemDefinition {
    public Boolean bCanBeDroppedWhenEquipmentChangeIsBlocked;
    public Boolean bAllowedFuelGadgetUI;
    public Boolean bShowCooldownUI;
    public Boolean bShowShortDescriptionInPickupDisplay;
    public Boolean bDisplayPlayerNameForInventoryActor;
    public Boolean bDisplayHealthForInventoryActor;
    public Boolean bDisplayShieldForInventoryActor;
    public FGameplayTag SpecialActorInventoryTag;
    public FName InventorySpecialActorUniqueID;
    public FSlateBrush InventoryMinimapIconBrush;
    public FVector2D InventoryMinimapIconScale;
    public FSlateBrush InventoryCompassIconBrush;
    public FVector2D InventoryCompassIconScale;
    public FText InventoryActorDisplayName;
    public List<SpecialActorSingleStatData> SpecialActorInventoryStatList;
    public FPackageIndex /*FortInteractContextInfoWidget*/ ContextOverrideWidget;
    public FortCreativeTagsHelper CreativeTagsHelper;

    public static class SpecialActorSingleStatData {
        public ESpecialActorStatType StatType;
        public Float Value;
        public Float StatLogicValue;
    }

    public enum ESpecialActorStatType {
        NumEliminationsNearby,
        TimeInWorld,
        PickupNumSpawns,
        PickupNumDespawns,
        PickupNumDropped,
        PickupNumTaken,
        PlayerWon,
        PlayerNumEliminations,
        PlayerNum,
        TotalStats
    }
}
