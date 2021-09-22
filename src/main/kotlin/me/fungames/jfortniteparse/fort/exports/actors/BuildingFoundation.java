package me.fungames.jfortniteparse.fort.exports.actors;

import kotlin.Lazy;
import kotlin.UByte;
import me.fungames.jfortniteparse.ue4.assets.UStruct;
import me.fungames.jfortniteparse.ue4.assets.exports.actors.ALODActor;
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText;
import me.fungames.jfortniteparse.ue4.objects.core.math.*;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer;
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagQuery;
import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateBrush;
import me.fungames.jfortniteparse.ue4.objects.uobject.FName;
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex;
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath;

import java.util.List;

public class BuildingFoundation extends BuildingSMActor {
    public List<FPackageIndex /*BuildingGroup*/> BuildingGroups;
    public List<FSoftObjectPath> AdditionalWorlds;
    public FVector BuildingDeconstructorOrigin;
    public FVector BuildingDeconstructorExtent;
    public Boolean bConditionalFoundation;
    public Boolean bServerStreamedInLevel;
    public Boolean bShowHLODWhenDisabled;
    public Boolean bOverrideNavigationGraphCells;
    public Boolean bHasExcludedZone;
    public Boolean bForceDitheringTransition;
    public Boolean bStreamingDataBasedBounds;
    public EDynamicFoundationEnabledState FoundationEnabledState;
    public EDynamicFoundationType DynamicFoundationType;
    public EBuildingFoundationType FoundationType;
    public Integer NavExclusionMinX;
    public Integer NavExclusionMaxX;
    public Integer NavExclusionMinY;
    public Integer NavExclusionMaxY;
    public FBox StreamingBoundingBox;
    public FName LevelToStream;
    public FGameplayTagQuery BuildingGroupFilter;
    public FGameplayTag MapLocationTag;
    public FSlateBrush LandIcon;
    public FVector LandIconWorldOffset;
    public FText MapLocationText;
    public FVector MapLocationTextOffset;
    public BuildingFoundationStreamingData StreamingData;
    public List<Lazy<FortHLODSMActor>> SpawnedLODActors;
    public List<Float> SpawnedLODActorsMinDrawDistances;
    public List<Lazy<FortHLODSMActor>> ChildSpawnedLODActors;
    public List<Lazy<ALODActor>> HLODActors;
    public Lazy<BuildingFoundation> ParentFoundation;
    public Float ProxyMeshMaxDrawDistanceMultiplier;
    public FPackageIndex /*LevelStreaming*/ LevelStreamInfo;
    public DynamicBuildingFoundationRepData DynamicFoundationRepData;
    public FTransform DynamicFoundationTransform;

    public enum EDynamicFoundationEnabledState {
        Unknown,
        Enabled,
        Disabled
    }

    public enum EDynamicFoundationType {
        Static,
        StartEnabled_Stationary,
        StartEnabled_Dynamic,
        StartDisabled
    }

    public enum EBuildingFoundationType {
        BFT_3x3,
        BFT_5x5,
        BFT_5x10,
        BFT_None
    }

    @UStruct
    public static class BuildingFoundationStreamingData {
        public FName FoundationName;
        public FVector FoundationLocation;
        public FBox BoundingBox;
        public FGameplayTagContainer GameplayTags;
        public FIntPoint GridCoordinates;
        public List<Lazy<FortHLODSMActor> /*LazyObjectProperty*/> ProxyInfo;
        public List<Integer> ChildStreamingDataIndices;
        public UByte PersistentHLODLevelIndex;
    }

    @UStruct
    public static class DynamicBuildingFoundationRepData {
        public FRotator Rotation;
        public FVector Translation;
        public EDynamicFoundationEnabledState EnabledState;
    }
}
