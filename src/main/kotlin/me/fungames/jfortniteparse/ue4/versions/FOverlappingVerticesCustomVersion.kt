package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

// custom version for overlapping vertices code
object FOverlappingVerticesCustomVersion {
    // Before any version changes were made in the plugin
    const val BeforeCustomVersionWasAdded = 0
    // UE4.19
    // Converted to use HierarchicalInstancedStaticMeshComponent
    const val DetectOVerlappingVertices = 1
    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = DetectOVerlappingVertices

    @JvmField val GUID = FGuid(0x612FBE52u, 0xDA53400Bu, 0x910D4F91u, 0x9FB1857Cu)

    @JvmStatic
    fun get(Ar: FArchive): Int {
        val ver = Ar.customVer(GUID)
        if (ver >= 0) {
            return ver
        }
        val game = Ar.game
        return when {
            game < GAME_UE4(19) -> BeforeCustomVersionWasAdded
            else -> LatestVersion
        }
    }
}