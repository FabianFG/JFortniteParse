package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.Lazy;
import kotlin.UInt;
import me.fungames.jfortniteparse.fort.enums.EFortRarity;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.FCurveTableRowHandle;
import me.fungames.jfortniteparse.ue4.objects.FScalableFloat;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FRotator;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.engine.curves.UCurveFloat;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagQuery;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;

import java.util.List;

public class BuildingContainer extends BuildingTimeOfDayLights {
    public FPackageIndex /*SoundCue*/ SearchingSoundCueLoop;
    public FPackageIndex /*SoundCue*/ LootRepeatSoundCue;
    public FPackageIndex /*SoundCue*/ OnDamageSoundCue;
    public FPackageIndex /*SoundCue*/ OnDeathSoundCue;
    public FPackageIndex /*StaticMesh*/ SearchedMesh;
    public List<FPackageIndex /*MaterialInterface*/> SearchedMaterialOverrides;
    public List<LootTierGroupTagOverride> LootTierGroupTagOverrideData;
    public FName SearchLootTierGroup;
    public List<RandomUpgradeData> PotentialRandomUpgrades;
    public ChosenQuotaInfo SearchLootTierChosenQuotaInfo;
    public FName ContainerLootTierKey;
    public Integer ReplicatedLootTier;
    public Integer ChosenRandomUpgrade;
    public Boolean bSpawnedActor;
    public Boolean bBlockMarking;
    public Boolean bCanBeMarked;
    public MarkedActorDisplayInfo MarkerDisplay;
    public FVector MarkerPositionOffset;
    public FVector2D NumItemsToDropRange;
    public Float SearchBounceRadiusOverride;
    public FPackageIndex /*FortWorldItemDefinition*/ LootTestingData;
    public Float LootNoiseRange;
    public FVector LootSpawnLocation;
    public FVector LootFinalLocation;
    public FVector InstancedLoot_TossDirection;
    public Float InstancedLoot_TossSpeed;
    public Float InstancedLoot_TossConeHalfAngle;
    public FVector LootSpawnLocation_Athena;
    public FRotator LootTossDirection_Athena;
    public Float LootTossSpeed_Athena;
    public Float LootTossConeHalfAngle_Athena;
    public EFortRarity HighestRarity;
    public Boolean bUseLootProperties_Athena;
    public Boolean bAlwaysShowContainer;
    public Boolean bAlwaysMaintainLoot;
    public Boolean bDestroyContainerOnSearch;
    public Boolean bForceHidePickupMinimapIndicator;
    public Boolean bForceSpawnLootOnDestruction;
    public Boolean bForceTossLootOnSpawn;
    public Boolean bAlreadySearched;
    public Boolean bDoNotDropLootOnDestruction;
    public Boolean bBuriedTreasure;
    public Boolean bHasRaisedTreasure;
    public Boolean bStartAlreadySearched_Athena;
    public Boolean bRegenerateLoot;
    public Boolean bUseLocationForDrop;
    public Float LootedWeaponsDurabilityModifier;
    public FortSearchBounceData SearchBounceData;
    public FCurveTableRowHandle SearchSpeed;
    public FText SearchText;
    public FPackageIndex /*AudioComponent*/ AudioIndicator_Component;
    public Lazy<UCurveFloat> CurrentInteractBounceCurve;
    public Lazy<UCurveFloat> CurrentInteractBounceNormalCurve;
    public Float SavedReservedRandomValueResult;
    public Float TimeUntilLootRegenerates;

    @UStruct
    public static class LootTierGroupTagOverride {
        public FScalableFloat IsEnabled;
        public FName OverrideLootTierGroup;
        public FGameplayTagQuery PlayerTagQuery;
    }

    @UStruct
    public static class RandomUpgradeData {
        public FName LootTierGroupIfApplied;
        public FScalableFloat ChanceToApplyPerContainer;
        public FScalableFloat Enabled;
        public List<RandomUpgradeCalendarData> CalendarDrivenEnableState;
        public List<MarkedActorDisplayInfo> MarkerDisplayInfoOverride;
    }

    @UStruct
    public static class RandomUpgradeCalendarData {
        public ECalendarDrivenState ReactionWhenEventIsPresent;
        public String EventName;
    }

    public enum ECalendarDrivenState {
        ForceEnable,
        ForceDisable
    }

    @UStruct
    public static class FortSearchBounceData {
        public FVector BounceNormal;
        public UInt SearchAnimationCount;
        public FPackageIndex /*FortPlayerPawn*/ SearchingPawn;
    }
}
