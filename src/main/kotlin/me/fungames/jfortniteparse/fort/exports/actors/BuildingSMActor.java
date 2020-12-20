package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.Lazy;
import kotlin.UByte;
import kotlin.UShort;
import me.fungames.jfortniteparse.fort.enums.EFortResourceType;
import me.fungames.jfortniteparse.fort.enums.ELootQuotaLevel;
import me.fungames.jfortniteparse.fort.enums.EPlacementType;
import me.fungames.jfortniteparse.fort.exports.BuildingTextureData;
import me.fungames.jfortniteparse.ue4.assets.UProperty;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.enums.EDetailMode;
import me.fungames.jfortniteparse.ue4.assets.exports.FCurveTableRowHandle;
import me.fungames.jfortniteparse.ue4.assets.exports.UStaticMesh;
import me.fungames.jfortniteparse.ue4.assets.exports.components.UStaticMeshComponent;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInstanceConstant;
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface;
import me.fungames.jfortniteparse.ue4.objects.core.math.FBox;
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector;
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector2D;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

import static me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInstance.*;

public class BuildingSMActor extends BuildingActor {
    @UProperty(arrayDim = 4)
    public Lazy<BuildingTextureData>[] TextureData;
    public Lazy<UStaticMesh> StaticMesh;
    //public List<FTierMeshSets> AlternateMeshes;
    @UProperty(skipPrevious = 1)
    public Boolean bForceReplicateSubObjects;
    public Boolean bNoPhysicsCollision;
    public Boolean bNoCameraCollision;
    public Boolean bNoPawnCollision;
    public Boolean bNoAIPawnCollision;
    public Boolean bBlocksCeilingPlacement;
    public Boolean bBlocksAttachmentPlacement;
    public Boolean bUsePhysicalSurfaceForFootstep;
    public Boolean bRandomYawOnPlacement;
    public Boolean bRandomScaleOnPlacement;
    public Boolean bClearMIDWhenReturningToUndamagedState;
    public UByte NumFrameSubObjects;
    public EFortResourceType ResourceType;
    public FVector2D RandomScaleRange;
    public FName DestructionLootTierGroup;
    public FSoftObjectPath WindSpeedCurve;
    public FSoftObjectPath WindPannerSpeedCurve;
    public FSoftObjectPath WindAudio;
    public Float ShieldBuffMaterialParamValue1;
    public Float ShieldBuffMaterialParamValue2;
    public Float AnimatingDistanceFieldSelfShadowBias;
    public Float AnimatingSubObjects;
    public Float PlayerGridSnapSize;
    public Integer AltMeshIdx;
    public Boolean bAllowBuildingCheat;
    public Boolean bMirrored;
    public Boolean bNoCollision;
    public Boolean bSupportsRepairing;
    public Boolean bHiddenDueToTrapPlacement;
    public Boolean bAttachmentPlacementBlockedFront;
    public Boolean bAttachmentPlacementBlockedBack;
    public Boolean bIsForPreviewing;
    public Boolean bUnderConstruction;
    public Boolean bUnderRepair;
    public Boolean bIsInitiallyBuilding;
    public Boolean bCameraOnlyCollision;
    public Boolean bNoWeaponCollision;
    public Boolean bNoRangedWeaponCollision;
    public Boolean bNoProjectileCollision;
    public Boolean bDoNotBlockInteract;
    public Boolean bNeedsMIDsForCreative;
    public Boolean bAllowResourceDrop;
    public Boolean bHideOnDeath;
    public Boolean bPlayDestructionEffects;
    public Boolean bSkipConstructionSounds;
    public Boolean bSupportedDirectly;
    public Boolean bForciblyStructurallySupported;
    public Boolean bRegisterWithStructuralGrid;
    public Boolean bCurrentlyBeingEdited;
    public Boolean bAllowWeakSpots;
    public Boolean bUseComplexForWeakSpots;
    public Boolean bCanSpawnAtLowerQuotaLevels;
    public Boolean bNeedsWindMaterialParameters;
    public Boolean bPlayBounce;
    public Boolean bPropagateBounce;
    public Boolean bPropagatesBounceEffects;
    public Boolean bNeedsDamageOverlay;
    public Boolean bDeriveCurieIdentifierFromResourceType;
    public Boolean bAllowCustomMaterial;
    public Boolean bUseSingleMeshCullDistance;
    public ESavedSupportStatus SavedDirectlySupportedStatus;
    public ELootQuotaLevel MaximumQuotaLevelBound;
    public EBuildingAnim BuildingAnimation;
    public UByte CurAnimSubObjectNum;
    public UByte CurAnimSubObjectTargetNum;
    public UShort ActorIndexInFoundation;
    public FortBounceData BounceData;
    public Float DestroyedTime;
    public Float InfluenceMapWeight;
    public Lazy<UStaticMeshComponent> BASEEffectMeshComponent;
    public List<BuildingNavObstacle> NavObstacles;
    public FVector BuildingPlacementDistance;
    public FVector /*FVector_NetQuantize100*/ ReplicatedDrawScale3D;
    public EditorOnlyBuildingInstanceMaterialParameters EditorOnlyInstanceMaterialParameters;
    public Lazy<UStaticMeshComponent> StaticMeshComponent;
    public FPackageIndex /*MaterialInterface*/ BaseMaterial;
    //public FScriptMulticastDelegate OnConstructionComplete;
    @UProperty(skipPrevious = 1)
    public BuildingActorMinimalReplicationProxy MinimalReplicationProxy;
    public ChosenQuotaInfo DestructionLootTierChosenQuotaInfo;
    public FName DestructionLootTierKey;
    public FCurveTableRowHandle BuildingResourceAmountOverride;
    public Integer MaxResourcesToSpawn;
    public List<FSoftObjectPath> IntenseWindMaterials;
    public FPackageIndex /*ParticleSystem*/ BreakEffect;
    public FSoftObjectPath DeathParticles;
    public FPackageIndex /*ParticleSystem*/ DeathParticlesInst;
    public FName DeathParticleSocketName;
    public FPackageIndex /*SoundBase*/ DeathSound;
    public FPackageIndex /*ParticleSystem*/ ConstructedEffect;
    public List<RandomDayphaseFX> RandomDayphaseFXList;
    public FPackageIndex /*AudioComponent*/ ConstructionAudioComponent;
    public FPackageIndex /*FortPawn*/ CachedDestructionInstigator;
    public Lazy<UStaticMeshComponent> DamageOverlayComponent;
    public Float DamageAmountStart;
    public Float LastDamageAmount;
    public FVector LastDamageHitImpulseDir;
    public List<Lazy<UStaticMeshComponent>> CachedAnimatingStaticMeshes;
    /*public FScriptMulticastDelegate OnRepairBuildingStarted;
    public FScriptMulticastDelegate OnRepairBuildingFinished;*/
    @UProperty(skipPrevious = 2)
    public FPackageIndex /*BuildingEditModeMetadata*/ EditModePatternData;
    public Integer UndermineGroup;
    public Integer LogicalBuildingIdx;
    public List<AnimatingMaterialPair> AnimatingMaterialMappings;
    public List<AnimatingMaterialPair> DamagedButNotAnimatingMaterialMappings;
    public FPackageIndex /*Class*/ EditModeSupportClass;
    public FPackageIndex /*BuildingEditModeSupport*/ EditModeSupport;
    public Float HealthToAutoBuild;
    public Float AccumulatedAutoBuildTime;
    public EBuildingReplacementType BuildingReplacementType;
    public EBuildingReplacementType ReplacementDestructionReason;
    public EBuildingAnim CurBuildingAnimType;
    public EFortDamageVisualsState DamageVisualsState;
    public Float CurBuildProgress;
    public Float OutwardMotionMagnitude;
    public Float CurBuildingAnimStartTime;
    public List<FPackageIndex /*MaterialInstanceDynamic*/> BlueprintMIDs;
    public List<Lazy<UMaterialInstanceConstant>> BlueprintMICs;
    public Lazy<UStaticMeshComponent> BlueprintMeshComp;
    public FPackageIndex /*FortPlayerStateZone*/ EditingPlayer;
    public FVector BuildingAttachmentPointOffset;
    public Float BuildingAttachmentRadius;
    public EBuildingAttachmentSlot BuildingAttachmentSlot;
    public EBuildingAttachmentType BuildingAttachmentType;
    public EPlacementType BuildingPlacementType;
    public EStructuralSupportCheck LastStructuralCheck;
    public Lazy<BuildingSMActor> ParentActorToAttachTo;
    public List<Lazy<BuildingActor>> AttachedBuildingActors;
    public List<Lazy<BuildingActor>> BuildingActorsAttachedTo;
    /*public FScriptMulticastDelegate OnTrapPlacementChanged;
    public FScriptMulticastDelegate OnReplacementDestruction;*/
    @UProperty(arrayDim = 2, skipPrevious = 2, skipNext = 1)
    public Lazy<BuildingActor>[] AttachmentPlacementBlockingActors;
    //public TWeakObjectPtr<FPackageIndex /*BuildingFoundation*/> Foundation;
    public Lazy<BuildingSMActor> DamagerOwner;
    public FPackageIndex /*FortConstructorBASE*/ RelevantBASE;
    @UProperty(skipNext = 1)
    public FPackageIndex /*FortConstructorBASE*/ LastRelevantBASE;
    //public ProxyGameplayCueDamage ProxyGameplayCueDamage;

    public enum ESavedSupportStatus {
        UnknownState,
        Supported,
        UnSupported
    }

    public enum EBuildingAnim {
        EBA_None,
        EBA_Building,
        EBA_Breaking,
        EBA_Destruction,
        EBA_Placement,
        EBA_DynamicLOD,
        EBA_DynamicShrink
    }

    @UStruct
    public static class FortBounceData {
        public Float StartTime;
        public Float BounceValue;
        public Float Radius;
        public FLinearColor DeformationVector;
        public FLinearColor DeformationCenter;
        public EFortBounceType BounceType;
        public Boolean bLocalInstigator;
        public Boolean bIsPlaying;
    }

    public enum EFortBounceType {
        Hit,
        Interact,
        EditPlaced
    }

    @UStruct
    public static class BuildingNavObstacle {
        public FBox LocalBounds;
        public EBuildingNavObstacleType ObstacleType;
    }

    public enum EBuildingNavObstacleType {
        UnwalkableAll,
        UnwalkableHuskOnly,
        SmashWhenLowHeight,
        SmashOnlyLowHeight,
        SmashSmasherOnly,
        SmashAll
    }

    @UStruct
    public static class EditorOnlyBuildingInstanceMaterialParameters {
        public List<FScalarParameterValue> ScalarParams;
        public List<FVectorParameterValue> VectorParams;
        public List<FTextureParameterValue> TextureParams;
    }

    @UStruct
    public static class BuildingActorMinimalReplicationProxy {
        public Short Health;
        public Short MaxHealth;
    }

    @UStruct
    public static class ChosenQuotaInfo {
        public Integer LootTier;
        public FName LootTierKey;
    }

    @UStruct
    public static class RandomDayphaseFX {
        public FPackageIndex /*ParticleSystem*/ ParticleSystem;
        public List<FPackageIndex /*UParticleSystem*/> AltParticleSystems;
        public List<EFortDayPhase> RequiredDayphases;
        public Float ChanceToSpawnFX;
        public EDetailMode DetailMode;
        public Float MaxDrawDistance;
        public Boolean bRandomSelectionAlreadyHappened;
        public FPackageIndex /*ParticleSystemComponent*/ SpawnedComponent;
    }

    public enum EFortDayPhase {
        Morning,
        Day,
        Evening,
        Night,
        NumPhases
    }

    @UStruct
    public static class AnimatingMaterialPair {
        public Lazy<UMaterialInterface> Original;
        public Lazy<UMaterialInterface> Override;
    }

    public enum EBuildingReplacementType {
        BRT_None,
        BRT_Edited,
        BRT_Upgrade
    }

    public enum EFortDamageVisualsState {
        UnDamaged,
        DamagedAndAnimating,
        DamagedAndStatic
    }

    public enum EBuildingAttachmentSlot {
        SLOT_Floor,
        SLOT_Wall,
        SLOT_Ceiling,
        SLOT_None
    }

    public enum EBuildingAttachmentType {
        ATTACH_Floor,
        ATTACH_Wall,
        ATTACH_Ceiling,
        ATTACH_Corner,
        ATTACH_All,
        ATTACH_WallThenFloor,
        ATTACH_FloorAndStairs,
        ATTACH_CeilingAndStairs,
        ATTACH_None
    }

    public enum EStructuralSupportCheck {
        Stable,
        Unstable,
        Max_None
    }
}
