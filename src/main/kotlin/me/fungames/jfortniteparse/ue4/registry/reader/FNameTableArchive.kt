package me.fungames.jfortniteparse.ue4.registry.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FNameEntry
import me.fungames.jfortniteparse.ue4.reader.FArchive
import kotlin.math.min

@ExperimentalUnsignedTypes
class FNameTableArchive : FArchive {
    override var littleEndian: Boolean
        get() = wrappedAr.littleEndian
        set(value) { wrappedAr.littleEndian = value }

    private val wrappedAr : FArchive
    val nameMap : List<FNameEntry>

    constructor(wrappedArchive: FArchive) {
        this.wrappedAr = wrappedArchive
        this.nameMap = serializeNameMap()
    }

    private constructor(wrappedArchive: FArchive, nameMap : List<FNameEntry>) {
        this.wrappedAr = wrappedArchive
        this.nameMap = nameMap
    }

    override fun clone() = FNameTableArchive(wrappedAr, nameMap)

    override fun seek(pos: Int) = wrappedAr.seek(pos)
    override fun size() = wrappedAr.size()
    override fun pos() = wrappedAr.pos()

    override fun read(b: ByteArray, off: Int, len: Int) = wrappedAr.read(b, off, len)
    override fun readBuffer(size: Int) = wrappedAr.readBuffer(size)

    override fun skip(n: Long) = wrappedAr.skip(n)
    override fun printError() = wrappedAr.printError()

    private fun serializeNameMap() : List<FNameEntry> {
        val nameOffset = wrappedAr.readInt64()
        if (nameOffset > wrappedAr.size())
            throw ParserException("This Name Table was corrupted. Name Offset $nameOffset > Size ${size()}")
        if (nameOffset > 0) {
            val originalOffset = wrappedAr.pos()
            // We already verified that nameOffset isn't bigger than our archive so it's safe to cast to int
            wrappedAr.seek(nameOffset.toInt())

            val nameCount = wrappedAr.readInt32()
            if (nameCount < 0)
                throw ParserException("Negative name count offset in name table: $nameCount")

            val minFNameEntrySize = 4 // sizeof(int32)
            val maxReservation = size() - pos() / minFNameEntrySize
            val nameMap = ArrayList<FNameEntry>(min(nameCount, maxReservation))
            for (nameMapIdx in 0 until nameCount)
                nameMap.add(FNameEntry(wrappedAr))
            wrappedAr.seek(originalOffset)
            return nameMap
        }
        return emptyList()
    }

    // This is kinda duplicate of FAssetArchive
    fun readFName() : FName {
        val nameIndex = this.readInt32()
        val extraIndex = this.readInt32()
        if (nameIndex in nameMap.indices)
            return FName(nameMap, nameIndex, extraIndex)
        else
            throw ParserException("FName could not be read, requested index $nameIndex, name map size ${nameMap.size}", this)
    }

    //Only overriding these to keep optimal performance with FByteArchive
    override fun readDouble() = wrappedAr.readDouble()
    override fun readFloat32() = wrappedAr.readFloat32()
    override fun readInt8() = wrappedAr.readInt8()
    override fun readInt16() = wrappedAr.readInt16()
    override fun readInt32() = wrappedAr.readInt32()
    override fun readInt64() = wrappedAr.readInt64()
}