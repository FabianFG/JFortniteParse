package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for RecomputeTangent
object FRecomputeTangentCustomVersion {
    // Before any version changes were made in the plugin
    const val BeforeCustomVersionWasAdded = 0
    // UE4.12
    // We serialize the RecomputeTangent Option
    const val RuntimeRecomputeTangent = 1
    // UE4.26
    // Choose which Vertex Color channel to use as mask to blend tangents
    const val RecomputeTangentVertexColorMask = 2
    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = RecomputeTangentVertexColorMask

    @JvmField val GUID = FGuid(0x5579F886u, 0x933A4C1Fu, 0x83BA087Bu, 0x6361B92Fu)

    @JvmStatic
    fun get(Ar: FArchive): Int {
        val ver = Ar.customVer(GUID)
        if (ver >= 0) {
            return ver
        }
        val game = Ar.game
        return when {
            game < GAME_UE4(12) -> BeforeCustomVersionWasAdded
            game < GAME_UE4(26) -> RuntimeRecomputeTangent
            else -> LatestVersion
        }
    }
}