package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for changes made in Dev-Anim stream
object FAnimObjectVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0

    // Reworked how anim blueprint root nodes are recovered
    const val LinkTimeAnimBlueprintRootDiscovery = 1

    // Cached marker sync names on skeleton for editor
    const val StoreMarkerNamesOnSkeleton = 2

    // Serialized register array state for RigVM
    const val SerializeRigVMRegisterArrayState = 3

    // Increase number of bones per chunk from uint8 to uint16
    const val IncreaseBoneIndexLimitPerChunk = 4

    const val UnlimitedBoneInfluences = 5

    // Anim sequences have colors for their curves
    const val AnimSequenceCurveColors = 6

    // Notifies and sync markers now have Guids
    const val NotifyAndSyncMarkerGuids = 7

    // Serialized register dynamic state for RigVM
    const val SerializeRigVMRegisterDynamicState = 8

    // Groom cards serialization
    const val SerializeGroomCards = 9

    // Serialized rigvm entry names
    const val SerializeRigVMEntries = 10

    // Serialized rigvm entry names
    const val SerializeHairBindingAsset = 11

    // Serialized rigvm entry names
    const val SerializeHairClusterCullingData = 12

    // Groom cards and meshes serialization
    const val SerializeGroomCardsAndMeshes = 13

    // Stripping LOD data from groom
    const val GroomLODStripping = 14

    // Stripping LOD data from groom
    const val GroomBindingSerialization = 15

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = GroomBindingSerialization

    @JvmField val GUID = FGuid(0xAF43A65Du, 0x7FD34947u, 0x98733E8Eu, 0xD9C1BB05u)

    @JvmStatic
    fun get(Ar: FArchive): Int {
        val ver = Ar.customVer(GUID)
        if (ver >= 0) {
            return ver
        }
        val game = Ar.game
        return when {
            game < GAME_UE4(21) -> BeforeCustomVersionWasAdded
            game < GAME_UE4(25) -> StoreMarkerNamesOnSkeleton
            game < GAME_UE4(26) -> NotifyAndSyncMarkerGuids
            else -> LatestVersion
        }
    }
}