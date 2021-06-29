package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.io.EIoChunkType
import me.fungames.jfortniteparse.ue4.io.FIoChunkId
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FNameEntryId
import me.fungames.jfortniteparse.ue4.objects.uobject.loadNameBatch
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive

class FNameMap {
    internal var nameEntries = emptyList<String>()
    private var nameMapType = FMappedName.EType.Global

    fun loadGlobal(provider: FileProvider) {
        check(nameEntries.isEmpty())

        val namesId = FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNames)
        val hashesId = FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNameHashes)

        val nameBuffer = provider.saveChunk(namesId)
        val hashBuffer = provider.saveChunk(hashesId)

        load(nameBuffer, hashBuffer, FMappedName.EType.Global)
    }

    fun size() = nameEntries.size

    fun load(nameBuffer: ByteArray, hashBuffer: ByteArray, nameMapType: FMappedName.EType) {
        nameEntries = loadNameBatch(FByteArchive(nameBuffer), FByteArchive(hashBuffer))
        this.nameMapType = nameMapType
    }

    fun load(nameBuffer: FArchive, hashBuffer: FArchive, nameMapType: FMappedName.EType) {
        nameEntries = loadNameBatch(nameBuffer, hashBuffer)
        this.nameMapType = nameMapType
    }

    fun getName(mappedName: FMappedName): FName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        return FName(nameEntries, mappedName.getIndex().toInt(), mappedName.number.toInt())
    }

    fun getNameOrNull(mappedName: FMappedName): FName? {
        check(mappedName.getType() == nameMapType)
        val index = mappedName.getIndex().toInt()
        if (index < nameEntries.size) {
            return FName(nameEntries, index, mappedName.number.toInt())
        }
        return null
    }

    fun getMinimalName(mappedName: FMappedName): FMinimalName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        return FMinimalName(FNameEntryId(mappedName.getIndex()), mappedName.number.toInt(), nameEntries)
    }
}