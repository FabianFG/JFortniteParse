package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.Lazy;
import kotlin.UByte;
import me.fungames.jfortniteparse.fort.enums.EFortTeam;
import me.fungames.jfortniteparse.fort.enums.EPhysicalSurface;
import me.fungames.jfortniteparse.fort.objects.FortAttributeInitializationKey;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.FCurveTableRowHandle;
import me.fungames.jfortniteparse.ue4.assets.exports.UClass;
import me.fungames.jfortniteparse.ue4.assets.exports.USoundBase;
import me.fungames.jfortniteparse.ue4.assets.exports.actors.AActor;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UPrimitiveComponent;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagQuery;
import me.fungames.jfortniteparse.ue4.objects.uobject.FMulticastScriptDelegate;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class BuildingActor extends AActor {
    public FGuid MyGuid;
    public Float SavedHealthPct;
    public Short OwnerPersistentID;
    public Boolean bUseMinLifeSpan;
    public FSoftObjectPath /*SoftClassPath*/ AreaClass;
    public Lazy<UClass> NavigationLinksClass;
    public List<FPackageIndex /*FortAthenaVehicle*/> InitialOverlappingVehicles;
    public Integer CurrentBuildingLevel;
    public Integer MaximumBuildingLevel;
    public Lazy<UClass> BuildingAttributeSetClass;
    public FPackageIndex /*FortBuildingActorSet*/ BuildingAttributeSet;
    public FPackageIndex /*FortBuildingActorSet*/ ReplicatedBuildingAttributeSet;
    public Float MaxHealthInitializationValue;
    @UProperty(arrayDim = 2)
    public FortAttributeInitializationKey AttributeInitKeys;
    public EAttributeInitLevelSource AttributeInitLevelSource;
    public EAbilitySystemComponentCreationPolicy AbilitySystemComponentCreationPolicy;
    public EPhysicalSurface PrimarySurfaceType;
    public EFortBaseWeaponDamage WeaponResponseType;
    public FPackageIndex /*FortAbilitySystemComponent*/ AbilitySystemComponent;
    public FPackageIndex /*FortAbilitySystemComponent*/ ReplicatedAbilitySystemComponent;
    //public List<FGameplayCueParameters> PendingDamageImpactCues;
    @UProperty(skipPrevious = 1)
    public Float HealthBarIndicatorWidth;
    public Float HealthBarIndicatorVerticalOffset;
    public FName HealthBarIndicatorSocketName;
    public FPackageIndex /*FortHealthBarIndicator*/ HealthBarIndicator;
    public Integer HealthBarIndicatorDifficultyRating;
    public UByte ForceMetadataRelevant;
    public UByte LastMetadataRelevant;
    public EDynamicBuildingPlacementType DynamicBuildingPlacementType;
    public ENavigationObstacleOverride NavigationObstacleOverride;
    public FGameplayTagQuery IncomingDamageFilterQuery;
    public Boolean bIsInvulnerable;
    public Boolean bPreviewBuildingActor;
    public Boolean bPlayedDying;
    public Boolean bHasRegisteredActorStateAtLeastOnce;
    public Boolean bDirtyForLevelRecordSave;
    public Boolean bSavedMetaPropertiesProcessed;
    public Boolean bUpgradeUsesSameClass;
    public Boolean bDisplayLevelInInfoWidget;
    public Boolean bAllowUpgradeRegardlessOfPlayerBuildLevel;
    public Boolean bDisplayDamageNumbersInAthena;
    public Boolean bUseFortHealthBarIndicator;
    public Boolean bSurpressHealthBar;
    public Boolean bCreateVerboseHealthLogs;
    public Boolean bIsIndestructibleForTargetSelection;
    public Boolean bDestroyed;
    public Boolean bPersistToWorld;
    public Boolean bRefreshFullSaveDataBeforeZoneSave;
    public Boolean bBeingDragged;
    public Boolean bRotateInPlaceGame;
    public Boolean bBeingOneHitDisassembled;
    public Boolean bBoundsAreInvalidForMelee;
    public Boolean bIsNavigationModifier;
    public Boolean bBlockNavigationLinks;
    public Boolean bCanExportNavigationCollisions;
    public Boolean bCanExportNavigationObstacle;
    public Boolean bMirrorNavLinksX;
    public Boolean bMirrorNavLinksY;
    public Boolean bIgnoreMoveGoalCollisionRadius;
    public Boolean bForceDisableRootNavigationRelevance;
    public Boolean bForceAutomationPass;
    public Boolean bForceAutomationPass_NavmeshOnTop;
    public Boolean bForceAutomationPass_SmashableFlat;
    public Boolean bCanBeSavedInCreativeVolume;
    public Boolean bIsNavigationRelevant;
    public Boolean bIsNavigationIndestructible;
    public Boolean bBlockNavLinksInCell;
    public Boolean bUseHotSpotAsMoveGoalReplacement;
    public Boolean bHasCustomAttackLocation;
    public Boolean bWorldReadyCalled;
    public Boolean bBeingRotatedOrScaled;
    public Boolean bBeingTranslated;
    public Boolean bRotateInPlaceEditor;
    public Boolean bEditorPlaced;
    public Boolean bPlayerPlaced;
    public Boolean bShouldTick;
    public Boolean bUsesDayPhaseChange;
    public Boolean bIsDynamic;
    public Boolean bIsDynamicOnDedicatedServer;
    public Boolean bIsDedicatedServer;
    public Boolean bUseTickManager;
    public Boolean bIsMovable;
    public Boolean bRegisteredForDayPhaseChange;
    public Boolean bForceDamagePing;
    public Boolean bDestroyFoliageWhenPlaced;
    public Boolean bObstructTrapTargeting;
    public Boolean bInstantDeath;
    public Boolean bDoNotBlockBuildings;
    public Boolean bForceBlockBuildings;
    public Boolean bDestroyOnPlayerBuildingPlacement;
    public Boolean bUseCentroidForBlockBuildingsCheck;
    public Boolean bPredictedBuildingActor;
    public Boolean bIgnoreCollisionWithCriticalActors;
    public Boolean bIsPlayerBuildable;
    public Boolean bFireBuiltAndDestroyedEvents;
    public Boolean bStructurallySupportOverlappingActors;
    public Boolean bAllowInteract;
    public Boolean bShowFirstInteractPrompt;
    public Boolean bShowSecondInteractPrompt;
    public Boolean bAllowHostileBlueprintInteraction;
    public Boolean bEndAbilitiesOnDeath;
    public Boolean bAlwaysUseNetCullDistanceSquaredForRelevancy;
    public Boolean bHighlightDirty;
    public Boolean bCollisionBlockedByPawns;
    public Boolean bAllowTeamDamage;
    public Boolean bIgnoreAffiliationInteractHighlight;
    public Boolean bSuppressInteractionWidget;
    public EFortBuildingType BuildingType;
    public EFortTeam Team;
    public UByte TeamIndex;
    public FGameplayTagContainer ConstTags;
    public FGameplayTagContainer StaticGameplayTags;
    public FText InteractionText;
    public FGameplayTag CanInteractPerformNativeActionTag;
    public FMulticastScriptDelegate OnDied;
    public FMulticastScriptDelegate OnDamaged;
    public FPackageIndex /*FortMission*/ AssociatedMissionParam;
    public FPackageIndex /*FortPlacementActor*/ OriginatingPlacementActor;
    public Float BRMinDrawDistance;
    public Float BRMaxDrawDistance;
    public Float StWMinDrawDistance;
    public Float StWMaxDrawDistance;
    public FMulticastScriptDelegate OnInteract;
    public FCurveTableRowHandle InteractionSpeed;
    public Integer DataVersion;
    public Float LastTakeHitTimeTimeout;
    public Lazy<USoundBase> PlayHitSound;
    public Float CullDistance;
    public Float SnapGridSize;
    public Float VertSnapGridSize;
    public FVector SnapOffset;
    public FVector CentroidOffset;
    public FVector BaseLocToPivotOffset;
    public String CustomState;
    public List<Lazy<UClass>> ComponentTypesWhitelistedForReplication;
    public List<Lazy<UPrimitiveComponent>> OverridePrimitivesToExcludeFoliage;
    public FPackageIndex /*BuildingActorHotSpotConfig*/ HotSpotConfig;
    public FMulticastScriptDelegate OnBuildingHealthChanged;
    public FMulticastScriptDelegate OnActorHealthChanged;
    public FGuid SavedActorGuid;
    public Float BaselineScale;
    public Float AccumulatedDeltaSinceLastVisualsTick;
    public FPackageIndex /*ProjectileMovementComponent*/ ProjectileMovementComponent;
    public Float LifespanAfterDeath;

    public enum EAttributeInitLevelSource {
        WorldDifficulty,
        PlayerBuildingSkill,
        AthenaPlaylist
    }

    public enum EAbilitySystemComponentCreationPolicy {
        Never,
        Lazy,
        Always
    }

    public enum EFortBaseWeaponDamage {
        Combat,
        Environmental
    }

    public enum EDynamicBuildingPlacementType {
        CountsTowardsBounds,
        DestroyIfColliding,
        DestroyAnythingThatCollides
    }

    public enum ENavigationObstacleOverride {
        UseMeshSettings,
        ForceEnabled,
        ForceDisabled
    }

    public enum EFortBuildingType {
        Wall,
        Floor,
        Corner,
        Deco,
        Prop,
        Stairs,
        Roof,
        Pillar,
        SpawnedItem,
        Container,
        Trap,
        GenericCenterCellActor,
        None
    }

    @UStruct
    public static class MarkedActorDisplayInfo {
        public FText DisplayName;
        public FSoftObjectPath Icon;
        public Lazy<UClass> CustomIndicatorClass;
        public FLinearColor PrimaryColor;
        public Lazy<USoundBase> Sound;
        public EFortMarkedActorScreenClamping ScreenClamping;
    }

    public enum EFortMarkedActorScreenClamping {
        Default,
        Clamp,
        ClampWhileNew,
        DontClamp
    }
}
