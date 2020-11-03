package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.util.isAligned

class FSerializedNameHeader {
    val data: ByteArray

    constructor() {
        data = ByteArray(2)
    }

    /*constructor(len: UInt, bIsUtf16: Boolean) : this() {
        static_assert(NAME_SIZE < 0x8000u, "")
        check(len <= NAME_SIZE)

        data[0] = (((if (bIsUtf16) 1 else 0).toUInt() shl 7) or (len shr 8)).toUByte()
        data[1] = len.toUByte()
    }*/

    constructor(Ar: FArchive) {
        data = Ar.read(2)
    }

    fun isUtf16() = (data[0].toUByte() and 0x80u) != 0u.toUByte()

    fun len() = ((data[0].toUByte() and 0x7Fu).toUInt() shl 8) + data[1].toUByte()
}

fun loadNameHeader(inOutAr: FArchive): FNameEntrySerialized {
    val header = FSerializedNameHeader(inOutAr)
    val len = header.len().toInt()

    return FNameEntrySerialized(if (header.isUtf16()) {
        String(inOutAr.read(len * 2), Charsets.UTF_16)
    } else {
        String(inOutAr.read(len), Charsets.UTF_8)
    })
}

fun batchLoadNameWithHash(str: String, len: Int, inHash: Long): String {
    /*val name = FNameStringView(str, len)
    val hash = FNameHash(str, len, inHash)
    check(hash == hashName(name, true)) { "Precalculated hash was wrong" }
    return getNamePoolPostInit().batchStore(FNameComparisonValue(name, hash))*/
    return str.intern()
}

fun batchLoadNameWithHash(name: FNameEntrySerialized, hash: Long) =
    batchLoadNameWithHash(name.name, name.name.length, hash)

inline fun loadNameBatch(outNames: MutableList<String>, nameData: ByteArray, hashData: ByteArray) {
    loadNameBatch(outNames, FByteArchive(nameData), FByteArchive(hashData))
}

fun loadNameBatch(outNames: MutableList<String>, nameDataAr: FArchive, hashDataAr: FArchive) {
    val hashDataSize = hashDataAr.size() - hashDataAr.pos()
    check(isAligned(hashDataSize, 8 /*sizeof(uint64)*/))

    val hashVersion = hashDataAr.readUInt64()
    val hashes = hashDataAr.readTArray(hashDataSize / 8 - 1) { hashDataAr.readInt64() }

    if (hashVersion == 0xC1640000uL /*FNameHash.ALGORITHM_ID*/) {
        for (hash in hashes) {
            val name = loadNameHeader(nameDataAr)
            outNames.add(batchLoadNameWithHash(name, hash))
        }
    } else {
        throw ParserException("hashVersion (0x%08X) != FNameHash.ALGORITHM_ID (0xC1640000), this is unsupported".format(hashVersion.toLong()))
    }
}