package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.reader.FArchive

// Custom serialization version for changes made in Dev-Core stream
object FCoreObjectVersion {
    // Before any version changes were made
    const val BeforeCustomVersionWasAdded = 0
    const val MaterialInputNativeSerialize = 1
    const val EnumProperties = 2
    const val SkeletalMaterialEditorDataStripping = 3
    const val FProperties = 4

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = FProperties

    fun get(Ar: FArchive): Int {
        val game = Ar.game
        return when {
            game < GAME_UE4(12) -> BeforeCustomVersionWasAdded
            game < GAME_UE4(15) -> MaterialInputNativeSerialize
            game < GAME_UE4(22) -> EnumProperties
            game < GAME_UE4(25) -> SkeletalMaterialEditorDataStripping
            else -> LatestVersion
        }
    }
}