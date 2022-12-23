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

    // Added some new MeshInfo to the FSkeletalMeshLODModel class.
    const val SkeletalMeshLODModelMeshInfo = 58

    // Add Texture DoScaleMipsForAlphaCoverage
    const val TextureDoScaleMipsForAlphaCoverage = 59

    // Fixed default value of volumetric cloud to be exact match with main view, more expenssive but we let user choosing how to lower the quality.
    const val VolumetricCloudReflectionSampleCountDefaultUpdate = 60

    // Use special BVH for TriangleMesh, instead of the AABBTree
    const val UseTriangleMeshBVH = 61

    // FDynamicMeshAttributeSet has Weight Maps. TDynamicAttributeBase serializes its name.
    const val DynamicMeshAttributesWeightMapsAndNames = 62

    // Switching FK control naming scheme to incorporate _CURVE for curve controls
    const val FKControlNamingScheme = 63

    // Fix-up for FRichCurveKey::TangentWeightMode, which were found to contain invalid value w.r.t the enum-type
    const val RichCurveKeyInvalidTangentMode = 64

    // Enforcing new automatic tangent behaviour, enforcing auto-tangents for Key0 and KeyN to be flat, for Animation Assets.
    const val ForceUpdateAnimationAssetCurveTangents = 65

    // SoundWave Update to use EditorBuildData for it's RawData
    const val SoundWaveVirtualizationUpdate = 66

    // Fix material feature level nodes to account for new SM6 input pin.
    const val MaterialFeatureLevelNodeFixForSM6 = 67

    // Fix material feature level nodes to account for new SM6 input pin.
    const val GeometryCollectionPerChildDamageThreshold = 68

    // Move some Chaos flags into a bitfield
    const val AddRigidParticleControlFlags = 69

    // Allow each LiveLink controller to specify its own component to control
    const val LiveLinkComponentPickerPerController = 70

    // Remove Faces in Triangle Mesh BVH
    const val RemoveTriangleMeshBVHFaces = 71

    // Moving all nodal offset handling to Lens Component
    const val LensComponentNodalOffset = 72

    // GPU none interpolated spawning no longer calls the update script
    const val FixGpuAlwaysRunningUpdateScriptNoneInterpolated = 73

    // World partition streaming policy serialization only for cooked builds
    const val WorldPartitionSerializeStreamingPolicyOnCook = 74

    // Remove serialization of bounds relevant from  WorldPartitionActorDesc
    const val WorldPartitionActorDescRemoveBoundsRelevantSerialization = 75

    // Added IAnimationDataModel interface and replace UObject based representation for Animation Assets
    // This version had to be undone. Animation assets saved between this and the subsequent backout version
    // will be unable to be loaded
    const val AnimationDataModelInterface_BackedOut = 76

    // Deprecate LandscapeSplineActorDesc
    const val LandscapeSplineActorDescDeprecation = 77

    // Revert the IAnimationDataModel changes. Animation assets
    const val BackoutAnimationDataModelInterface = 78

    // Made stationary local and skylights behave similar to SM5
    const val MobileStationaryLocalLights = 79

    // Made ManagedArrayCollection::FValueType::Value always serialize when FValueType is
    const val ManagedArrayCollectionAlwaysSerializeValue = 80

    // Moving all distortion handling to Lens Component
    const val LensComponentDistortion = 81

    // Updated image media source path resolution logic
    const val ImgMediaPathResolutionWithEngineOrProjectTokens = 82

    // Add low resolution data in Height Field
    const val AddLowResolutionHeightField = 83

    // Low resolution data in Height Field will store one height for (6x6) 36 cells
    const val DecreaseLowResolutionHeightField = 84

    // Add damage propagation settings to geometry collections
    const val GeometryCollectionDamagePropagationData = 85

    // Wheel friction forces are now applied at tire contact point
    const val VehicleFrictionForcePositionChange = 86

    // Add flag to override MeshDeformer on a SkinnedMeshComponent.
    const val AddSetMeshDeformerFlag = 87

    // Replace FNames for class/actor paths with FSoftObjectPath
    const val WorldPartitionActorDescActorAndClassPaths = 88

    // Reintroducing AnimationDataModelInterface_BackedOut changes
    const val ReintroduceAnimationDataModelInterface = 89

    // Support 16-bit skin weights on SkeletalMesh
    const val IncreasedSkinWeightPrecision = 90

    // bIsUsedWithVolumetricCloud flag auto conversion
    const val MaterialHasIsUsedWithVolumetricCloudFlag = 91

    // bIsUsedWithVolumetricCloud flag auto conversion
    const val UpdateHairDescriptionBulkData = 92

    // Added TransformScaleMethod pin to SpawnActorFromClass node
    const val SpawnActorFromClassTransformScaleMethod = 93

    // Added support for the RigVM to run branches lazily
    const val RigVMLazyEvaluation = 94

    // Adding additional object version to defer out-of-date pose asset warning until next resaves
    const val PoseAssetRawDataGUIDUpdate = 95

    // Store function information (and compilation data) in blueprint generated class
    const val RigVMSaveFunctionAccessInModel = 96

    // Store the RigVM execute context struct the VM uses in the archive
    const val RigVMSerializeExecuteContextStruct = 97

    // Store the Visual Logger timestamp as a double
    const val VisualLoggerTimeStampAsDouble = 98

    // Add ThinSurface instance override support
    const val MaterialInstanceBasePropertyOverridesThinSurface = 99

    // Add refraction mode None, converted from legacy when the refraction pin is not plugged.
    const val MaterialRefractionModeNone = 100

    // Store serialized graph function in the function data
    const val RigVMSaveSerializedGraphInGraphFunctionData = 101

    // Animation Sequence now stores its frame-rate on a per-platform basis
    const val PerPlatformAnimSequenceTargetFrameRate = 102

    // New default for number of attributes on 2d grids
    const val NiagaraGrid2DDefaultUnnamedAttributesZero = 103

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = NiagaraGrid2DDefaultUnnamedAttributesZero

    @JvmField val GUID = FGuid(0x697DD581u, 0xE64f41ABu, 0xAA4A51ECu, 0xBEB7B628u)

    @JvmStatic
    fun get(Ar: FArchive): Int {
        val ver = Ar.customVer(GUID)
        if (ver >= 0) {
            return ver
        }
        val game = Ar.game
        return when {
            game < GAME_UE5(0) -> BeforeCustomVersionWasAdded
            game < GAME_UE5(1) -> WorldPartitionActorDescActorAndClassPaths
            else -> LatestVersion
        }
    }
}