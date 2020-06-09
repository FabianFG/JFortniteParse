package me.fungames.jfortniteparse.ue4.versions

enum class Ue4Version(val game : Int) {
    GAME_UE4_22(me.fungames.jfortniteparse.ue4.versions.GAME_UE4_22),
    GAME_UE4_23(me.fungames.jfortniteparse.ue4.versions.GAME_UE4_23),
    GAME_UE4_24(me.fungames.jfortniteparse.ue4.versions.GAME_UE4_24),
    GAME_UE4_25(me.fungames.jfortniteparse.ue4.versions.GAME_UE4_25),

    //CUSTOM GAMES
    GAME_VALORANT(me.fungames.jfortniteparse.ue4.versions.GAME_VALORANT),

    GAME_UE4_LATEST(GAME_UE4(LATEST_SUPPORTED_UE4_VERSION));

    val version = ue4Versions[GAME_UE4_GET_MINOR(game)]
}

const val GAME_UE4_BASE = 0x1000000
const val GAME_UE4_22 = GAME_UE4_BASE + (22 shl 4)
const val GAME_UE4_23 = GAME_UE4_BASE + (23 shl 4)
const val GAME_UE4_24 = GAME_UE4_BASE + (24 shl 4)
const val GAME_UE4_25 = GAME_UE4_BASE + (25 shl 4)

    // bytes: 01.00.0N.NX : 01=UE4, 00=masked by GAME_ENGINE, NN=UE4 subversion, X=game (4 bits, 0=base engine)
    //val GAME_Borderlands3 = GAME_UE4(20) + 2
const val GAME_VALORANT = GAME_UE4_22 + 1

fun GAME_UE4(x : Int) = GAME_UE4_BASE + (x shl 4)
fun GAME_UE4_GET_MINOR(x : Int) = (x - GAME_UE4_BASE) shr 4
fun GAME_UE4_GET_AR_VER(game : Int) = ue4Versions[GAME_UE4_GET_MINOR(game)]

const val LATEST_SUPPORTED_UE4_VERSION = 25

internal val ue4Versions = arrayOf(VER_UE4_0, VER_UE4_1, VER_UE4_2, VER_UE4_3, VER_UE4_4,
    VER_UE4_5, VER_UE4_6, VER_UE4_7, VER_UE4_8, VER_UE4_9,
    VER_UE4_10, VER_UE4_11, VER_UE4_12, VER_UE4_13, VER_UE4_14,
    VER_UE4_15, VER_UE4_16, VER_UE4_17, VER_UE4_18, VER_UE4_19,
    VER_UE4_20, VER_UE4_21, VER_UE4_22, VER_UE4_23, VER_UE4_24,
    VER_UE4_25)