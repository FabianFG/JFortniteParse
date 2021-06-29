package me.fungames.jfortniteparse.ue4.registry.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.registry.objects.FAssetBundleData
import me.fungames.jfortniteparse.ue4.registry.objects.FAssetData
import kotlin.math.min

class FNameTableArchiveReader : FAssetRegistryArchive {
    val nameMap: List<String>

    constructor(wrappedArchive: FArchive) : super(wrappedArchive) {
        this.nameMap = serializeNameMap()
    }

    private constructor(wrappedArchive: FArchive, nameMap: List<String>) : super(wrappedArchive) {
        this.nameMap = nameMap
    }

    override fun clone() = FNameTableArchiveReader(wrappedAr, nameMap)

    private fun serializeNameMap(): List<String> {
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
            val nameMap = MutableList(min(nameCount, maxReservation)) {
                val name = wrappedAr.readString()
                //uassetAr.skip(4) // skip nonCasePreservingHash (uint16) and casePreservingHash (uint16)
                name
            }
            wrappedAr.seek(originalOffset)
            return nameMap
        }
        return emptyList()
    }

    // This is kinda duplicate of FAssetArchive
    override fun readFName(): FName {
        val nameIndex = this.readInt32()
        val extraIndex = this.readInt32()
        if (nameIndex in nameMap.indices)
            return FName(nameMap, nameIndex, extraIndex)
        else
            throw ParserException("FName could not be read, requested index $nameIndex, name map size ${nameMap.size}", this)
    }

    override fun serializeTagsAndBundles(out: FAssetData) {
        // This is actually a FAssetDataTagMapSharedView which just contains a FAssetDataTagMap
        // which is just a TSortedMap<FName, FString>
        out.tagsAndValues = readTMap { readFName() to readString() }
        out.taggedAssetBundles = FAssetBundleData(emptyArray())
    }
}