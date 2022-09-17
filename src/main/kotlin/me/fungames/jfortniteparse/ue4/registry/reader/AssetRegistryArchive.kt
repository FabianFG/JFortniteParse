package me.fungames.jfortniteparse.ue4.registry.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.NAME_NO_NUMBER_INTERNAL
import me.fungames.jfortniteparse.ue4.objects.uobject.loadNameBatch
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FArchiveProxy
import me.fungames.jfortniteparse.ue4.registry.objects.*
import me.fungames.jfortniteparse.ue4.versions.EUnrealEngineObjectUE5Version
import kotlin.collections.set

val ASSET_REGISTRY_NUMBERED_NAME_BIT = 0x80000000u

/** Header containing versioning & data stripping information for a serialized asset registry state. */
class FAssetRegistryHeader {
    var version: FAssetRegistryVersion
    var filterEditorOnlyData: Boolean

    constructor(Ar: FArchive) {
        version = FAssetRegistryVersion(Ar)
        if (version >= FAssetRegistryVersion.Type.AddedHeader) {
            filterEditorOnlyData = Ar.readBoolean()
        } else {
            filterEditorOnlyData = false
        }
    }

    constructor(version: FAssetRegistryVersion, filterEditorOnlyData: Boolean) {
        this.version = version
        this.filterEditorOnlyData = filterEditorOnlyData
    }
}

abstract class FAssetRegistryArchive(wrappedAr: FArchive, val header: FAssetRegistryHeader) : FArchiveProxy(wrappedAr) {
    init {
        if (header.version >= FAssetRegistryVersion.Type.RemoveAssetPathFNames) {
            ver = EUnrealEngineObjectUE5Version.FSOFTOBJECTPATH_REMOVE_ASSET_PATH_FNAMES
        }
        isFilterEditorOnly = header.filterEditorOnlyData
    }

    val version get() = header.version

    abstract fun serializeTagsAndBundles(out: FAssetData)
}

class FAssetRegistryReader : FAssetRegistryArchive {
    val names: List<String>
    val tags: FStore

    constructor(inner: FArchive, header: FAssetRegistryHeader) : super(inner, header) {
        names = loadNameBatch(inner)
        tags = FStore(this)
    }

    private constructor(inner: FArchive, header: FAssetRegistryHeader, names: List<String>, tags: FStore) : super(inner, header) {
        this.names = names
        this.tags = tags
    }

    override fun clone() = FAssetRegistryReader(wrappedAr, header, names, tags)

    override fun readFName(): FName {
        var index = readUInt32()
        var number = NAME_NO_NUMBER_INTERNAL

        if ((index and ASSET_REGISTRY_NUMBERED_NAME_BIT) > 0u) {
            index -= ASSET_REGISTRY_NUMBERED_NAME_BIT
            number = readInt32()
        }

        if (index >= 0u && index < names.size.toUInt()) {
            return FName(names, index.toInt(), number)
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
        FPartialMapHandle(mapHandle).makeFullHandle(tags).forEachPair { (key, value) ->
            out[key] = FValueHandle(tags, value).asString()
        }
        return out
    }
}