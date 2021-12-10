package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for changes made in //UE5/Main stream
object FUE5MainStreamObjectVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0

    // Nanite data added to Chaos geometry collections
    const val GeometryCollectionNaniteData = 1

    // Nanite Geometry Collection data moved to DDC
    const val GeometryCollectionNaniteDDC = 2

    // Removing SourceAnimationData, animation layering is now applied during compression
    const val RemovingSourceAnimationData = 3

    // New MeshDescription format.
    // This is the correct versioning for MeshDescription changes which were added to ReleaseObjectVersion.
    const val MeshDescriptionNewFormat = 4

    // Serialize GridGuid in PartitionActorDesc
    const val PartitionActorDescSerializeGridGuid = 5

    // Set PKG_ContainsMapData on external actor packages
    const val ExternalActorsMapDataPackageFlag = 6

    // Added a new configurable BlendProfileMode that the user can setup to control the behavior of blend profiles.
    const val AnimationAddedBlendProfileModes = 7

    // Serialize DataLayers in WorldPartitionActorDesc
    const val WorldPartitionActorDescSerializeDataLayers = 8

    // Renaming UAnimSequence::NumFrames to NumberOfKeys, as that what is actually contains.
    const val RenamingAnimationNumFrames = 9

    // Serialize HLODLayer in WorldPartition HLODActorDesc
    const val WorldPartitionHLODActorDescSerializeHLODLayer = 10

    // Fixed Nanite Geometry Collection cooked data
    const val GeometryCollectionNaniteCooked = 11

    // Added bCooked to UFontFace assets
    const val AddedCookedBoolFontFaceAssets = 12

    // Serialize CellHash in WorldPartition HLODActorDesc
    const val WorldPartitionHLODActorDescSerializeCellHash = 13

    // Nanite data is now transient in Geometry Collection similar to how RenderData is transient in StaticMesh.
    const val GeometryCollectionNaniteTransient = 14

    // Added FLandscapeSplineActorDesc
    const val AddedLandscapeSplineActorDesc = 15

    // Added support for per-object collision constraint flag. [Chaos]
    const val AddCollisionConstraintFlag = 16

    // Initial Mantle Serialize Version
    const val MantleDbSerialize = 17

    // Animation sync groups explicitly specify sync method
    const val AnimSyncGroupsExplicitSyncMethod = 18

    // Fixup FLandscapeActorDesc Grid indices
    const val FLandscapeActorDescFixupGridIndices = 19

    // FoliageType with HLOD support
    const val FoliageTypeIncludeInHLOD = 20

    // Introducing UAnimDataModel sub-object for UAnimSequenceBase containing all animation source data
    const val IntroducingAnimationDataModel = 21

    // Serialize ActorLabel in WorldPartitionActorDesc
    const val WorldPartitionActorDescSerializeActorLabel = 22

    // Fix WorldPartitionActorDesc serialization archive not persistent
    const val WorldPartitionActorDescSerializeArchivePersistent = 23

    // Fix potentially duplicated actors when using ForceExternalActorLevelReference
    const val FixForceExternalActorLevelReferenceDuplicates = 24

    // Make UMeshDescriptionBase serializable
    const val SerializeMeshDescriptionBase = 25

    // Chaos FConvex uses array of FVec3s for vertices instead of particles
    const val ConvexUsesVerticesArray = 26

    // Serialize HLOD info in WorldPartitionActorDesc
    const val WorldPartitionActorDescSerializeHLODInfo = 27

    // Expose particle Disabled flag to the game thread
    const val AddDisabledFlag = 28

    // Moving animation custom attributes from AnimationSequence to UAnimDataModel
    const val MoveCustomAttributesToDataModel = 29

    // Use of triangulation at runtime in BlendSpace
    const val BlendSpaceRuntimeTriangulation = 30

    // Fix to the Cubic smoothing, plus introduction of new smoothing types
    const val BlendSpaceSmoothingImprovements = 31

    // Removing Tessellation parameters from Materials
    const val RemovingTessellationParameters = 32

    // Sparse class data serializes its associated structure to allow for BP types to be used
    const val SparseClassDataStructSerialization = 33

    // PackedLevelInstance bounds fix
    const val PackedLevelInstanceBoundsFix = 34

    // Initial set of anim nodes converted to use constants held in sparse class data
    const val AnimNodeConstantDataRefactorPhase0 = 35

    // Explicitly serialized bSavedCachedExpressionData for Material(Instance)
    const val MaterialSavedCachedData = 36

    // Remove explicit decal blend mode
    const val RemoveDecalBlendMode = 37

    // Made directional lights be atmosphere lights by default
    const val DirLightsAreAtmosphereLightsByDefault = 38

    // Changed how world partition streaming cells are named
    const val WorldPartitionStreamingCellsNamingShortened = 39

    // Changed how actor descriptors compute their bounds
    const val WorldPartitionActorDescGetStreamingBounds = 40

    // Switch FMeshDescriptionBulkData to use virtualized bulkdata
    const val MeshDescriptionVirtualization = 41

    // Switch FTextureSource to use virtualized bulkdata
    const val TextureSourceVirtualization = 42

    // RigVM to store more information alongside the Copy Operator
    const val RigVMCopyOpStoreNumBytes = 43

    // Expanded separate translucency into multiple passes
    const val MaterialTranslucencyPass = 44

    // Chaos FGeometryCollectionObject user defined collision shapes support
    const val GeometryCollectionUserDefinedCollisionShapes = 45

    // Removed the AtmosphericFog component with conversion to SkyAtmosphere component
    const val RemovedAtmosphericFog = 46

    // The SkyAtmosphere now light up the heightfog by default, and by default the height fog has a black color.
    const val SkyAtmosphereAffectsHeightFogWithBetterDefault = 47

    // Ordering of samples in BlendSpace
    const val BlendSpaceSampleOrdering = 48

    // No longer bake MassToLocal transform into recorded transform data in GeometryCollection caching
    const val GeometryCollectionCacheRemovesMassToLocal = 49

    // UEdGraphPin serializes SourceIndex
    const val EdGraphPinSourceIndex = 50

    // Change texture bulkdatas to have unique guids
    const val VirtualizedBulkDataHaveUniqueGuids = 51

    // Introduce RigVM Memory Class Object
    const val RigVMMemoryStorageObject = 52

    // Ray tracing shadows have three states now (Disabled, Use Project Settings, Enabled)
    const val RayTracedShadowsType = 53

    // Add bVisibleInRayTracing flag to Skeletal Mesh Sections
    const val SkelMeshSectionVisibleInRayTracingFlagAdded = 54

    // Add generic tagging of all anim graph nodes in anim blueprints
    const val AnimGraphNodeTaggingAdded = 55

    // Add custom version to FDynamicMesh3
    const val DynamicMeshCompactedSerialization = 56

    // Remove the inline reduction bulkdata and replace it by a simple vertex and triangle count cache
    const val ConvertReductionBaseSkeletalMeshBulkDataToInlineReductionCacheData = 57

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = ConvertReductionBaseSkeletalMeshBulkDataToInlineReductionCacheData

    @JvmField val GUID = FGuid(0xD89B5E42u, 0x24BD4D46u, 0x8412ACA8u, 0xDF641779u)

    @JvmStatic
    fun get(Ar: FArchive): Int {
        val ver = Ar.customVer(GUID)
        if (ver >= 0) {
            return ver
        }
        val game = Ar.game
        return when {
            game < GAME_UE5(0) -> BeforeCustomVersionWasAdded
            else -> LatestVersion // TODO change this after they released UE5.0
        }
    }
}