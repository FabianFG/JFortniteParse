package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FSerializedNameHeader {
    val data = ByteArray(2)

    constructor()

    /*constructor(len: UInt, bIsUtf16: Boolean) : this() {
        static_assert(NAME_SIZE < 0x8000u, "")
        check(len <= NAME_SIZE)

        data[0] = (((if (bIsUtf16) 1 else 0).toUInt() shl 7) or (len shr 8)).toUByte()
        data[1] = len.toUByte()
    }*/

    constructor(Ar: FArchive) {
        Ar.read(data)
    }

    fun isUtf16() = (data[0].toUByte() and 0x80u) != 0u.toUByte()

    fun len() = ((data[0].toUByte() and 0x7Fu).toUInt() shl 8) + data[1].toUByte()
}

fun loadNameHeader(inOutAr: FArchive): String {
    val header = FSerializedNameHeader(inOutAr)
    val len = header.len().toInt()

    return if (header.isUtf16()) {
        String(inOutAr.read(len * 2), Charsets.UTF_16)
    } else {
        String(inOutAr.read(len), Charsets.UTF_8)
    }
}

fun canUseSavedHashes(hashVersion: ULong) = hashVersion == 0xC1640000uL /*FNameHash.ALGORITHM_ID*/