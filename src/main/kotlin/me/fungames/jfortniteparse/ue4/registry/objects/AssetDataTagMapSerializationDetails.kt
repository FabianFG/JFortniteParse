package me.fungames.jfortniteparse.ue4.registry.objects

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FNameEntryId
import me.fungames.jfortniteparse.ue4.registry.reader.FAssetRegistryReader
import me.fungames.jfortniteparse.util.get

/** Stores a fixed set of values and all the key-values maps used for lookup */
class FStore(Ar: FAssetRegistryReader) {
    companion object {
        val BEGIN_MAGIC = 0x12345678u
        val END_MAGIC = 0x87654321u
    }

    // Pairs for all unsorted maps that uses this store
    var pairs: Array<FNumberedPair>
    var numberlessPairs: Array<FNumberlessPair>

    // Values for all maps in this store
    var ansiStringOffsets: Array<UInt>
    var ansiStrings: ByteArray
    var wideStringOffsets: Array<UInt>
    var wideStrings: ByteArray
    var numberlessNames: Array<FNameEntryId>
    var names: Array<FName>
    var numberlessExportPaths: Array<FNumberlessExportPath>
    var exportPaths: Array<FAssetRegistryExportPath>
    var texts: Array<String> // FText objects serialized in NSLOCTEXT() strings

    val nameMap: List<String> = Ar.names

    init {
        check(Ar.readUInt32() == BEGIN_MAGIC)
        val nums = Array(11) { Ar.readInt32() }.iterator()

        numberlessNames = Array(nums.next()) { FNameEntryId(Ar) }
        names = Array(nums.next()) { Ar.readFName() }
        numberlessExportPaths = Array(nums.next()) { FNumberlessExportPath(Ar, nameMap) }
        exportPaths = Array(nums.next()) { FAssetRegistryExportPath(Ar) }
        texts = Array(nums.next()) { Ar.readString() /*FText(Ar)*/ }

        ansiStringOffsets = Array(nums.next()) { Ar.readUInt32() }
        wideStringOffsets = Array(nums.next()) { Ar.readUInt32() }
        ansiStrings = Ar.read(nums.next())
        wideStrings = Ar.read(nums.next() * 2)

        numberlessPairs = Array(nums.next()) { FNumberlessPair(Ar) }
        pairs = Array(nums.next()) { FNumberedPair(Ar) }
        check(Ar.readUInt32() == END_MAGIC)
    }

    fun getAnsiString(idx: UInt): String {
        val offset = ansiStringOffsets[idx]
        var length = 0u
        while (ansiStrings[offset + length] != 0.toByte()) ++length
        return String(ansiStrings, offset.toInt(), length.toInt(), Charsets.UTF_8)
    }

    fun getWideString(idx: UInt): String {
        val offset = wideStringOffsets[idx]
        var length = 0u
        while (wideStrings[offset + length] != 0.toByte() && wideStrings[offset + length + 1u] != 0.toByte()) length += 2u
        return String(wideStrings, offset.toInt(), length.toInt(), Charsets.UTF_16)
    }
}

/**
 * Incomplete handle to a map in an unspecified FStore.
 * Used for serialization where the store index is implicit.
 */
class FPartialMapHandle(int: ULong) {
    val bHasNumberlessKeys = int shr 63 > 0u
    val num = (int shr 32).toUShort()
    val pairBegin = int.toUInt()

    inline fun makeFullHandle(store: FStore) = FMapHandle(this, store)

    fun toInt() = ((if (bHasNumberlessKeys) 1uL else 0uL) shl 63) or (num.toULong() shl 32) or pairBegin.toULong()
}