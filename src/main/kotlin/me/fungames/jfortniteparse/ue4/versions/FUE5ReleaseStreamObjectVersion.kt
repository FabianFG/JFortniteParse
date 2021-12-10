package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for changes made in //UE5/Release-* stream
object FUE5ReleaseStreamObjectVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0

    // Added Lumen reflections to new reflection enum, changed defaults
    const val ReflectionMethodEnum = 1

    // Serialize HLOD info in WorldPartitionActorDesc
    const val WorldPartitionActorDescSerializeHLODInfo = 2

    // Removing Tessellation from materials and meshes.
    const val RemovingTessellation = 3

    // LevelInstance serialize runtime behavior
    const val LevelInstanceSerializeRuntimeBehavior = 4

    // Refactoring Pose Asset runtime data structures
    const val PoseAssetRuntimeRefactor = 5

    // Serialize the folder path of actor descs
    const val WorldPartitionActorDescSerializeActorFolderPath = 6

    // Change hair strands vertex format
    const val HairStrandsVertexFormatChange = 7

    // Added max linear and angular speed to Chaos bodies
    const val AddChaosMaxLinearAngularSpeed = 8

    // PackedLevelInstance version
    const val PackedLevelInstanceVersion = 9

    // PackedLevelInstance bounds fix
    const val PackedLevelInstanceBoundsFix = 10

    // Custom property anim graph nodes (linked anim graphs, control rig etc.) now use optional pin manager
    const val CustomPropertyAnimGraphNodesUseOptionalPinManager = 11

    // Add native double and int64 support to FFormatArgumentData
    const val TextFormatArgumentData64bitSupport = 12

    // Material layer stacks are no longer considered 'static parameters'
    const val MaterialLayerStacksAreNotParameters = 13

    // CachedExpressionData is moved from UMaterial to UMaterialInterface
    const val MaterialInterfaceSavedCachedData = 14

    // Add support for multiple cloth deformer LODs to be able to raytrace cloth with a different LOD than the one it is rendered with
    const val AddClothMappingLODBias = 15

    // Add support for different external actor packaging schemes
    const val AddLevelActorPackagingScheme = 16

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = AddLevelActorPackagingScheme

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