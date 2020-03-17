package me.fungames.jfortniteparse.ue4.versions

enum class Ue4Version(val versionInt : Int) {
    GAME_UE4_22(me.fungames.jfortniteparse.ue4.versions.GAME_UE4_22),
    GAME_UE4_23(me.fungames.jfortniteparse.ue4.versions.GAME_UE4_23),
    GAME_UE4_24(me.fungames.jfortniteparse.ue4.versions.GAME_UE4_24),
    GAME_UE4_25(me.fungames.jfortniteparse.ue4.versions.GAME_UE4_25),

    GAME_UE4_LATEST(GAME_UE4(LATEST_SUPPORTED_UE4_VERSION))
}

const val GAME_UE4_BASE = 0x1000000
const val GAME_UE4_22 = GAME_UE4_BASE + (22 shl 4)
const val GAME_UE4_23 = GAME_UE4_BASE + (23 shl 4)
const val GAME_UE4_24 = GAME_UE4_BASE + (24 shl 4)
const val GAME_UE4_25 = GAME_UE4_BASE + (25 shl 4)

    // bytes: 01.00.0N.NX : 01=UE4, 00=masked by GAME_ENGINE, NN=UE4 subversion, X=game (4 bits, 0=base engine)
    //val GAME_Borderlands3 = GAME_UE4(20) + 2

fun GAME_UE4(x : Int) = GAME_UE4_BASE + (x shl 4)

const val LATEST_SUPPORTED_UE4_VERSION = 25