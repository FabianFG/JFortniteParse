package me.fungames.jfortniteparse.ue4.versions

enum class Ue4Version(val game: Int) {
    GAME_UE4_0(GAME_UE4(0)),
    GAME_UE4_1(GAME_UE4(1)),
    GAME_UE4_2(GAME_UE4(2)),
    GAME_UE4_3(GAME_UE4(3)),
    GAME_UE4_4(GAME_UE4(4)),
    GAME_UE4_5(GAME_UE4(5)),
    GAME_UE4_6(GAME_UE4(6)),
    GAME_UE4_7(GAME_UE4(7)),
    GAME_UE4_8(GAME_UE4(8)),
    GAME_UE4_9(GAME_UE4(9)),
    GAME_UE4_10(GAME_UE4(10)),
    GAME_UE4_11(GAME_UE4(11)),
    GAME_UE4_12(GAME_UE4(12)),
    GAME_UE4_13(GAME_UE4(13)),
    GAME_UE4_14(GAME_UE4(14)),
    GAME_UE4_15(GAME_UE4(15)),
    GAME_UE4_16(GAME_UE4(16)),
    GAME_UE4_17(GAME_UE4(17)),
    GAME_UE4_18(GAME_UE4(18)),
    GAME_UE4_19(GAME_UE4(19)),
    GAME_UE4_20(GAME_UE4(20)),
    GAME_UE4_21(GAME_UE4(21)),
    GAME_UE4_22(GAME_UE4(22)),
    GAME_UE4_23(GAME_UE4(23)),
    GAME_UE4_24(GAME_UE4(24)),
    GAME_UE4_25(GAME_UE4(25)),
        GAME_VALORANT(me.fungames.jfortniteparse.ue4.versions.GAME_VALORANT),
    GAME_UE4_26(GAME_UE4(26)),
    GAME_UE4_27(GAME_UE4(27)),

    GAME_UE4_LATEST(GAME_UE4(LATEST_SUPPORTED_UE4_VERSION)),

    GAME_UE5_0(GAME_UE5(0)),
    GAME_UE5_1(GAME_UE5(1)),

    GAME_UE5_LATEST(GAME_UE5(LATEST_SUPPORTED_UE5_VERSION));

    val version get() = getArVer(game)
}

// region UE4 Versioning
const val GAME_UE4_BASE = 0x1000000

// bytes: 01.00.0N.NX : 01=UE4, 00=masked by GAME_ENGINE, NN=UE4 subversion, X=game (4 bits, 0=base engine)
// const val GAME_Borderlands3 = GAME_UE4(20) + 2
val GAME_VALORANT = GAME_UE4(25) + 1

fun GAME_UE4(x: Int) = GAME_UE4_BASE + (x shl 4)
fun GAME_UE4_GET_MINOR(x: Int) = (x - GAME_UE4_BASE) shr 4

const val LATEST_SUPPORTED_UE4_VERSION = 27
// endregion

// region UE5 Versioning
const val GAME_UE5_BASE = 0x2000000

fun GAME_UE5(x: Int) = GAME_UE5_BASE + (x shl 4)
fun GAME_UE5_GET_MINOR(x: Int) = (x - GAME_UE5_BASE) shr 4

const val LATEST_SUPPORTED_UE5_VERSION = 1
// endregion

fun getArVer(game: Int): Int {
    // Custom UE Games
    // If a game needs a even more specific custom version than the major release version you can add it below
    // if (game == GAME_VALORANT)
    //     return VER_UE4_24
    return when {
        game < GAME_UE4(1) -> VER_UE4_0
        game < GAME_UE4(2) -> VER_UE4_1
        game < GAME_UE4(3) -> VER_UE4_2
        game < GAME_UE4(4) -> VER_UE4_3
        game < GAME_UE4(5) -> VER_UE4_4
        game < GAME_UE4(6) -> VER_UE4_5
        game < GAME_UE4(7) -> VER_UE4_6
        game < GAME_UE4(8) -> VER_UE4_7
        game < GAME_UE4(9) -> VER_UE4_8
        game < GAME_UE4(10) -> VER_UE4_9
        game < GAME_UE4(11) -> VER_UE4_10
        game < GAME_UE4(12) -> VER_UE4_11
        game < GAME_UE4(13) -> VER_UE4_12
        game < GAME_UE4(14) -> VER_UE4_13
        game < GAME_UE4(15) -> VER_UE4_14
        game < GAME_UE4(16) -> VER_UE4_15
        game < GAME_UE4(17) -> VER_UE4_16
        game < GAME_UE4(18) -> VER_UE4_17
        game < GAME_UE4(19) -> VER_UE4_18
        game < GAME_UE4(20) -> VER_UE4_19
        game < GAME_UE4(21) -> VER_UE4_20
        game < GAME_UE4(22) -> VER_UE4_21
        game < GAME_UE4(23) -> VER_UE4_22
        game < GAME_UE4(24) -> VER_UE4_23
        game < GAME_UE4(25) -> VER_UE4_24
        game < GAME_UE4(26) -> VER_UE4_25
        game < GAME_UE4(27) -> VER_UE4_26
        game < GAME_UE5(0) -> VER_UE4_27
        else -> VER_UE5_0
    }
}