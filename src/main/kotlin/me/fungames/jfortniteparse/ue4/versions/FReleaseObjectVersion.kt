package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for changes made in Release streams.
object FReleaseObjectVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0

    // Static Mesh extended bounds radius fix
    const val StaticMeshExtendedBoundsFix = 1

    //Physics asset bodies are either in the sync scene or the async scene, but not both
    const val NoSyncAsyncPhysAsset = 2

    // ULevel was using TTransArray incorrectly (serializing the entire array in addition to individual mutations).
    // converted to a TArray:
    const val LevelTransArrayConvertedToTArray = 3

    // Add Component node templates now use their own unique naming scheme to ensure more reliable archetype lookups.
    const val AddComponentNodeTemplateUniqueNames = 4

    // Fix a serialization issue with static mesh FMeshSectionInfoMap FProperty
    const val UPropertryForMeshSectionSerialize = 5

    // Existing HLOD settings screen size to screen area conversion
    const val ConvertHLODScreenSize = 6

    // Adding mesh section info data for existing billboard LOD models
    const val SpeedTreeBillboardSectionInfoFixup = 7

    // Change FMovieSceneEventParameters::StructType to be a string asset reference from a TWeakObjectPtr<UScriptStruct>
    const val EventSectionParameterStringAssetRef = 8

    // Remove serialized irradiance map data from skylight.
    const val SkyLightRemoveMobileIrradianceMap = 9

    // rename bNoTwist to bAllowTwist
    const val RenameNoTwistToAllowTwistInTwoBoneIK = 10

    // Material layers serialization refactor
    const val MaterialLayersParameterSerializationRefactor = 11

    // Added disable flag to skeletal mesh data
    const val AddSkeletalMeshSectionDisable = 12

    // Removed objects that were serialized as part of this material feature
    const val RemovedMaterialSharedInputCollection = 13

    // HISMC Cluster Tree migration to add new data
    const val HISMCClusterTreeMigration = 14

    // Default values on pins in blueprints could be saved incoherently
    const val PinDefaultValuesVerified = 15

    // During copy and paste transition getters could end up with broken state machine references
    const val FixBrokenStateMachineReferencesInTransitionGetters = 16

    // Change to MeshDescription serialization
    const val MeshDescriptionNewSerialization = 17

    // Change to not clamp RGB values > 1 on linear color curves
    const val UnclampRGBColorCurves = 18

    // Bugfix for FAnimObjectVersion::LinkTimeAnimBlueprintRootDiscovery.
    const val LinkTimeAnimBlueprintRootDiscoveryBugFix = 19

    // Change trail anim node variable deprecation
    const val TrailNodeBlendVariableNameChange = 20

    // Make sure the Blueprint Replicated Property Conditions are actually serialized properly.
    const val PropertiesSerializeRepCondition = 21

    // DepthOfFieldFocalDistance at 0 now disables DOF instead of DepthOfFieldFstop at 0.
    const val FocalDistanceDisablesDOF = 22

    // Removed versioning, but version entry must still exist to keep assets saved with this version loadable
    const val Unused_SoundClass2DReverbSend = 23

    // Groom asset version
    const val GroomAssetVersion1 = 24
    const val GroomAssetVersion2 = 25

    // Store applied version of Animation Modifier to use when reverting
    const val SerializeAnimModifierState = 26

    // Groom asset version
    const val GroomAssetVersion3 = 27

    // Upgrade filmback
    const val DeprecateFilmbackSettings = 28

    // custom collision type
    const val CustomImplicitCollisionType = 29

    // FFieldPath will serialize the owner struct reference and only a short path to its property
    const val FFieldPathOwnerSerialization = 30

    // New MeshDescription format
    // This was inadvertently added in UE5. The proper version for it is in in UE5MainStreamObjectVersion
    const val MeshDescriptionNewFormat = 31

    // Pin types include a flag that propagates the 'CPF_UObjectWrapper' flag to generated properties
    const val PinTypeIncludesUObjectWrapperFlag = 32

    // Added Weight member to FMeshToMeshVertData
    const val WeightFMeshToMeshVertData = 33

    // Animation graph node bindings displayed as pins
    const val AnimationGraphNodeBindingsDisplayedAsPins = 34

    // Serialized rigvm offset segment paths
    const val SerializeRigVMOffsetSegmentPaths = 35

    // Upgrade AbcGeomCacheImportSettings for velocities
    const val AbcVelocitiesSupport = 36

    // Add margin support to Chaos Convex
    const val MarginAddedToConvexAndBox = 37

    // Add structure data to Chaos Convex
    const val StructureDataAddedToConvex = 38

    // Changed axis UI for LiveLink AxisSwitch Pre Processor
    const val AddedFrontRightUpAxesToLiveLinkPreProcessor = 39

    // Some sequencer event sections that were copy-pasted left broken links to the director BP
    const val FixupCopiedEventSections = 40

    // Serialize the number of bytes written when serializing function arguments
    const val RemoteControlSerializeFunctionArgumentsSize = 41

    // Add loop counters to sequencer's compiled sub-sequence data
    const val AddedSubSequenceEntryWarpCounter = 42

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = AddedSubSequenceEntryWarpCounter

    @JvmField val GUID = FGuid(0x9C54D522u, 0xA8264FBEu, 0x94210746u, 0x61B482D0u)

    @JvmStatic
    fun get(Ar: FArchive): Int {
        val ver = Ar.customVer(GUID)
        if (ver >= 0) {
            return ver
        }
        val game = Ar.game
        return when {
            game < GAME_UE4(11) -> BeforeCustomVersionWasAdded
            game < GAME_UE4(13) -> StaticMeshExtendedBoundsFix
            game < GAME_UE4(14) -> LevelTransArrayConvertedToTArray
            game < GAME_UE4(15) -> AddComponentNodeTemplateUniqueNames
            game < GAME_UE4(16) -> SpeedTreeBillboardSectionInfoFixup
            game < GAME_UE4(17) -> SkyLightRemoveMobileIrradianceMap
            game < GAME_UE4(19) -> RenameNoTwistToAllowTwistInTwoBoneIK
            game < GAME_UE4(20) -> AddSkeletalMeshSectionDisable
            game < GAME_UE4(21) -> MeshDescriptionNewSerialization
            game < GAME_UE4(23) -> TrailNodeBlendVariableNameChange
            game < GAME_UE4(24) -> Unused_SoundClass2DReverbSend
            game < GAME_UE4(25) -> DeprecateFilmbackSettings
            game < GAME_UE4(26) -> FFieldPathOwnerSerialization
            game < GAME_UE4(27) -> StructureDataAddedToConvex
            game < GAME_UE5(0) -> AddedSubSequenceEntryWarpCounter
            game == GAME_UE5(0) -> FixupCopiedEventSections // ue5-main, please check again after release
            else -> LatestVersion
        }
    }
}