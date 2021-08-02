package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for changes made in Dev-Core stream
object FFrameworkObjectVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0

    // BodySetup's default instance collision profile is used by default when creating a new instance.
    const val UseBodySetupCollisionProfile = 1

    // Regenerate subgraph arrays correctly in animation blueprints to remove duplicates and add
    // missing graphs that appear read only when edited
    const val AnimBlueprintSubgraphFix = 2

    // Static and skeletal mesh sockets now use the specified scale
    const val MeshSocketScaleUtilization = 3

    // Attachment rules are now explicit in how they affect location, rotation and scale
    const val ExplicitAttachmentRules = 4

    // Moved compressed anim data from uasset to the DDC
    const val MoveCompressedAnimDataToTheDDC = 5

    // Some graph pins created using legacy code seem to have lost the RF_Transactional flag,
    // which causes issues with undo. Restore the flag at this version
    const val FixNonTransactionalPins = 6

    // Create new struct for SmartName, and use that for CurveName
    const val SmartNameRefactor = 7

    // Add Reference Skeleton to Rig
    const val AddSourceReferenceSkeletonToRig = 8

    // Refactor ConstraintInstance so that we have an easy way to swap behavior paramters
    const val ConstraintInstanceBehaviorParameters = 9

    // Pose Asset support mask per bone
    const val PoseAssetSupportPerBoneMask = 10

    // Physics Assets now use SkeletalBodySetup instead of BodySetup
    const val PhysAssetUseSkeletalBodySetup = 11

    // Remove SoundWave CompressionName
    const val RemoveSoundWaveCompressionName = 12

    // Switched render data for clothing over to unreal data, reskinned to the simulation mesh
    const val AddInternalClothingGraphicalSkinning = 13

    // Wheel force offset is now applied at the wheel instead of vehicle COM
    const val WheelOffsetIsFromWheel = 14

    // Move curve metadata to be saved in skeleton
    // Individual asset still saves some flag - i.e. disabled curve and editable or not, but
    // major flag - i.e. material types - moves to skeleton and handle in one place
    const val MoveCurveTypesToSkeleton = 15

    // Cache destructible overlaps on save
    const val CacheDestructibleOverlaps = 16

    // Added serialization of materials applied to geometry cache objects
    const val GeometryCacheMissingMaterials = 17

    // Switch static & skeletal meshes to calculate LODs based on resolution-independent screen size
    const val LODsUseResolutionIndependentScreenSize = 18

    // Blend space post load verification
    const val BlendSpacePostLoadSnapToGrid = 19

    // Addition of rate scales to blend space samples
    const val SupportBlendSpaceRateScale = 20

    // LOD hysteresis also needs conversion from the LODsUseResolutionIndependentScreenSize version
    const val LODHysteresisUseResolutionIndependentScreenSize = 21

    // AudioComponent override subtitle priority default change
    const val ChangeAudioComponentOverrideSubtitlePriorityDefault = 22

    // Serialize hard references to sound files when possible
    const val HardSoundReferences = 23

    // Enforce const correctness in Animation Blueprint function graphs
    const val EnforceConstInAnimBlueprintFunctionGraphs = 24

    // Upgrade the InputKeySelector to use a text style
    const val InputKeySelectorTextStyle = 25

    // Represent a pins container type as an enum not 3 independent booleans
    const val EdGraphPinContainerType = 26

    // Switch asset pins to store as string instead of hard object reference
    const val ChangeAssetPinsToString = 27

    // Fix Local Variables so that the properties are correctly flagged as blueprint visible
    const val LocalVariablesBlueprintVisible = 28

    // Stopped serializing UField_Next so that UFunctions could be serialized in dependently of a UClass
    // in order to allow us to do all UFunction loading in a single pass (after classes and CDOs are created):
    const val RemoveUField_Next = 29

    // Fix User Defined structs so that all members are correct flagged blueprint visible
    const val UserDefinedStructsBlueprintVisible = 30

    // FMaterialInput and FEdGraphPin store their name as FName instead of FString
    const val PinsStoreFName = 31

    // User defined structs store their default instance, which is used for initializing instances
    const val UserDefinedStructsStoreDefaultInstance = 32

    // Function terminator nodes serialize an FMemberReference rather than a name/class pair
    const val FunctionTerminatorNodesUseMemberReference = 33

    // Custom event and non-native interface event implementations add 'const' to reference parameters
    const val EditableEventsUseConstRefParameters = 34

    // No longer serialize the legacy flag that indicates this state, as it is now implied since we don't serialize the skeleton CDO
    const val BlueprintGeneratedClassIsAlwaysAuthoritative = 35

    // Enforce visibility of blueprint functions - e.g. raise an error if calling a private function from another blueprint:
    const val EnforceBlueprintFunctionVisibility = 36

    // ActorComponents now store their serialization index
    const val StoringUCSSerializationIndex = 37

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = StoringUCSSerializationIndex

    fun get(Ar: FArchive): Int {
        val game = Ar.game
        return when {
            game < GAME_UE4(12) -> BeforeCustomVersionWasAdded
            game < GAME_UE4(13) -> FixNonTransactionalPins
            game < GAME_UE4(14) -> RemoveSoundWaveCompressionName
            game < GAME_UE4(15) -> GeometryCacheMissingMaterials
            game < GAME_UE4(16) -> ChangeAudioComponentOverrideSubtitlePriorityDefault
            game < GAME_UE4(17) -> HardSoundReferences
            game < GAME_UE4(18) -> LocalVariablesBlueprintVisible
            game < GAME_UE4(19) -> UserDefinedStructsBlueprintVisible
            game < GAME_UE4(20) -> FunctionTerminatorNodesUseMemberReference
            game < GAME_UE4(22) -> EditableEventsUseConstRefParameters
            game < GAME_UE4(24) -> BlueprintGeneratedClassIsAlwaysAuthoritative
            game < GAME_UE4(25) -> EnforceBlueprintFunctionVisibility
            game < GAME_UE4(26) -> StoringUCSSerializationIndex
            else -> LatestVersion
        }
    }
}