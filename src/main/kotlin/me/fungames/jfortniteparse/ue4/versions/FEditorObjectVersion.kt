package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.reader.FArchive

object FEditorObjectVersion {
    const val BeforeCustomVersionWasAdded = 0
    const val RefactorMeshEditorMaterials = 8
    const val UPropertryForMeshSection = 10
    const val AddedMorphTargetSectionIndices = 23
    const val StaticMeshDeprecatedRawMesh = 28
    const val LatestVersion = StaticMeshDeprecatedRawMesh

    @ExperimentalUnsignedTypes
    fun get(Ar : FArchive) = when {
        Ar.game < GAME_UE4(12) -> BeforeCustomVersionWasAdded
        Ar.game < GAME_UE4(13) -> 2
        Ar.game < GAME_UE4(14) -> 6
        Ar.game < GAME_UE4(15) -> RefactorMeshEditorMaterials
        Ar.game < GAME_UE4(16) -> 14
        Ar.game < GAME_UE4(17) -> 17
        Ar.game < GAME_UE4(19) -> 20
        Ar.game < GAME_UE4(20) -> AddedMorphTargetSectionIndices
        Ar.game < GAME_UE4(21) -> 24
        Ar.game < GAME_UE4(22) -> 26
        Ar.game < GAME_UE4(23) -> 30
        Ar.game < GAME_UE4(24) -> 34
        Ar.game < GAME_UE4(25) -> 37
        Ar.game < GAME_UE4(26) -> 38
        else -> LatestVersion
    }
}