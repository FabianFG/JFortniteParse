package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.io.isAligned
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive

fun getRequiredUtf16Padding(ptr: FArchive) {

}

class FSerializedNameHeader {
    val data: UByteArray

    constructor() {
        data = UByteArray(2)
    }

    /*constructor(len: UInt, bIsUtf16: Boolean) : this() {
        static_assert(NAME_SIZE < 0x8000u, "")
        check(len <= NAME_SIZE)

        data[0] = (((if (bIsUtf16) 1 else 0).toUInt() shl 7) or (len shr 8)).toUByte()
        data[1] = len.toUByte()
    }*/

    constructor(Ar: FArchive) {
        data = Ar.read(2).toUByteArray()
    }

    fun isUtf16() = (data[0] and 0x80u) > 0u

    fun len() = ((data[0] and 0x7Fu).toUInt() shl 8) + data[1]
}

fun loadNameHeader(inOutAr: FArchive) {
    val header = FSerializedNameHeader(inOutAr)
    val len = header.len()

    if (header.isUtf16()) {

    } else {

    }
}

fun loadNameBatch(outNames: MutableList<FNameEntryId>, nameData: ByteArray, hashData: ByteArray) {
//    check(isAligned(nameData.size, 8 /*sizeof(uint64)*/)) TODO failing here
    check(isAligned(hashData.size, 8 /*sizeof(uint64)*/))
    check(hashData.isNotEmpty())

    val nameDataAr = FByteArchive(nameData)
    val hashDataAr = FByteArchive(hashData)

    val hashVersion = hashDataAr.readUInt64()
    val hashes = hashDataAr.readTArray(hashData.size / 8 - 1) { hashDataAr.readUInt64() }

    if (hashVersion == 0xC1640000uL /*FNameHash.ALGORITHM_ID*/) {
        for (hash in hashes) {
            val name = loadNameHeader(nameDataAr)
        }
    } else {
        throw ParserException("hashVersion (0x%08X) != FNameHash.ALGORITHM_ID (0xC1640000), this is unsupported".format(hashVersion.toLong()))
    }

    /*const uint8* NameIt = NameData.GetData()
    const uint8* NameEnd = NameData.GetData() + NameData.Num()

    const uint64* HashDataIt = reinterpret_cast<const uint64*>(HashData.GetData())
    uint64 HashVersion = INTEL_ORDER64(HashDataIt[0])
    TArrayView<const uint64> Hashes = MakeArrayView(HashDataIt + 1, HashData.Num() / sizeof(uint64) - 1)

    OutNames.Empty(Hashes.Num())

//    GetNamePoolPostInit().BatchLock()

    if (HashVersion == FNameHash::AlgorithmId)
    {
        for (uint64 Hash : Hashes)
        {
            check(NameIt < NameEnd)
            FNameSerializedView Name = LoadNameHeader(*//* in-out *//* NameIt)
            OutNames.Add(BatchLoadNameWithHash(Name, INTEL_ORDER64(Hash)))
        }
    }
    else
    {
        while (NameIt < NameEnd)
        {
            FNameSerializedView Name = LoadNameHeader(*//* in-out *//* NameIt)
            OutNames.Add(BatchLoadNameWithoutHash(Name))
        }

    }

//    GetNamePoolPostInit().BatchUnlock()

    check(NameIt == NameEnd)*/
}