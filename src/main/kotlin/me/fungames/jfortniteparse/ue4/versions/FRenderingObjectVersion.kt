package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.reader.FArchive

object FRenderingObjectVersion {
    const val BeforeCustomVersionWasAdded = 0
    const val TextureStreamingMeshUVChannelData = 10
    const val IncreaseNormalPrecision = 26
    const val StaticMeshSectionForceOpaqueField = 37
    const val LatestVersion = StaticMeshSectionForceOpaqueField

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun get(Ar : FArchive) = when {
        Ar.game < GAME_UE4(12) -> BeforeCustomVersionWasAdded
        Ar.game < GAME_UE4(13) -> 2
        Ar.game < GAME_UE4(14) -> 4
        Ar.game < GAME_UE4(16) -> 12 // 4.14 and 4.15
        Ar.game < GAME_UE4(17) -> 15
        Ar.game < GAME_UE4(18) -> 19
        Ar.game < GAME_UE4(19) -> 20
        Ar.game < GAME_UE4(20) -> 25
        Ar.game < GAME_UE4(21) -> IncreaseNormalPrecision
        Ar.game < GAME_UE4(22) -> 27
        Ar.game < GAME_UE4(23) -> 28
        Ar.game < GAME_UE4(24) -> 31
        Ar.game < GAME_UE4(25) -> 36
        Ar.game < GAME_UE4(26) -> 43
        else -> LatestVersion
    }
}