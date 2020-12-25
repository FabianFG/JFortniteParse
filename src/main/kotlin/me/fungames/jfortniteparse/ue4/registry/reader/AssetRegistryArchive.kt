package me.fungames.jfortniteparse.ue4.registry.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.NAME_NO_NUMBER_INTERNAL
import me.fungames.jfortniteparse.ue4.objects.uobject.loadNameBatch
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FArchiveProxy
import me.fungames.jfortniteparse.ue4.registry.objects.*
import me.fungames.jfortniteparse.util.get

val ASSET_REGISTRY_NUMBERED_NAME_BIT = 0x80000000u

abstract class FAssetRegistryArchive(wrappedAr: FArchive) : FArchiveProxy(wrappedAr) {
    abstract fun serializeTagsAndBundles(out: FAssetData)
}

class FAssetRegistryReader : FAssetRegistryArchive {
    val names: List<String>
    val tags: FStore

    constructor(inner: FArchive) : super(inner) {
        names = loadNameBatch(inner)
        tags = FStore(this)
    }

    private constructor(inner: FArchive, names: List<String>, tags: FStore) : super(inner) {
        this.names = names
        this.tags = tags
    }

    override fun clone() = FAssetRegistryReader(wrappedAr, names, tags)

    override fun readFName(): FName {
        var index = readUInt32()
        var number = NAME_NO_NUMBER_INTERNAL

        if ((index and ASSET_REGISTRY_NUMBERED_NAME_BIT) > 0u) {
            index -= ASSET_REGISTRY_NUMBERED_NAME_BIT
            number = readInt32()
        }

        if (index >= 0u && index < names.size.toUInt()) {
            return FName.dummy(names[index], number)
        } else {
            throw ParserException("FName could not be read, requested index $index, name map size ${names.size}", this)
        }
    }

    override fun serializeTagsAndBundles(out: FAssetData) {
        out.tagsAndValues = loadTags()
        out.taggedAssetBundles = FAssetBundleData(this)
    }

    private fun loadTags(): Map<FName, String> {
        val mapHandle = readUInt64()
        val out = mutableMapOf<FName, String>()
        FPartialMapHandle(mapHandle).makeFullHandle(tags).forEachPair {
            out[it.key] = FValueHandle(tags, it.value).asString()
        }
        return out
    }
}