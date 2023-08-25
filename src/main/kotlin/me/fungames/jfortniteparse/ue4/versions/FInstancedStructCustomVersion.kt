package me.fungames.jfortniteparse.ue4.versions

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

object FInstancedStructCustomVersion {
    // Before any version changes were made
    const val CustomVersionAdded = 0

    // -----<new versions can be added above this line>-------------------------------------------------
    const val LatestVersion = CustomVersionAdded

    @JvmField val GUID = FGuid(0xE21E1CAAu, 0xAF47425Eu, 0x89BF6AD4u, 0x4C44A8BBu)

    @JvmStatic
    fun get(Ar: FArchive): Int {
        val ver = Ar.customVer(GUID)
        if (ver >= 0) {
            return ver
        }
        val game = Ar.game
        return when {
            game < GAME_UE5(3) -> -1
            else -> LatestVersion
        }
    }
}