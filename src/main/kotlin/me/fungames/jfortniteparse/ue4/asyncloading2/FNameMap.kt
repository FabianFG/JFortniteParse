package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FNameEntryId
import me.fungames.jfortniteparse.ue4.objects.uobject.loadNameBatch
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.get

class FNameMap {
    internal val nameEntries = mutableListOf<String>()
    private var nameMapType = FMappedName.EType.Global

    fun loadGlobal(ioDispatcher: FIoDispatcher) {
        check(nameEntries.isEmpty())

        val namesId = FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNames)
        val hashesId = FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNameHashes)

        val batch = ioDispatcher.newBatch()
        val nameRequest = batch.read(namesId, FIoReadOptions())
        val hashRequest = batch.read(hashesId, FIoReadOptions())
        batch.issue(EIoDispatcherPriority.IoDispatcherPriority_High)

        /*reserveNameBatch(
            ioDispatcher.getSizeForChunk(namesId),
            ioDispatcher.getSizeForChunk(hashesId))*/

        batch.waitRequests()

        val nameBuffer = nameRequest.getResultOrThrow()
        val hashBuffer = hashRequest.getResultOrThrow()

        load(nameBuffer, hashBuffer, FMappedName.EType.Global)

        ioDispatcher.freeBatch(batch)
    }

    fun size() = nameEntries.size

    fun load(nameBuffer: ByteArray, hashBuffer: ByteArray, nameMapType: FMappedName.EType) {
        loadNameBatch(nameEntries, nameBuffer, hashBuffer)
        this.nameMapType = nameMapType
    }

    fun load(nameBuffer: FArchive, hashBuffer: FArchive, nameMapType: FMappedName.EType) {
        loadNameBatch(nameEntries, nameBuffer, hashBuffer)
        this.nameMapType = nameMapType
    }

    fun getName(mappedName: FMappedName): FName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        val nameEntry = nameEntries[mappedName.getIndex()]
        return FName.createFromDisplayId(nameEntry, mappedName.number.toInt())
    }

    fun tryGetName(mappedName: FMappedName): FName? {
        check(mappedName.getType() == nameMapType)
        val index = mappedName.getIndex()
        if (index < nameEntries.size.toUInt()) {
            val nameEntry = nameEntries[mappedName.getIndex()]
            return FName.createFromDisplayId(nameEntry, mappedName.number.toInt())
        }
        return null
    }

    fun getMinimalName(mappedName: FMappedName): FMinimalName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        return FMinimalName(FNameEntryId(mappedName.getIndex()), mappedName.number.toInt(), nameEntries)
    }
}