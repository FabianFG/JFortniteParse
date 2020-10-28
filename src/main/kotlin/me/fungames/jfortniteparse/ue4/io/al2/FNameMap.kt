package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@ExperimentalUnsignedTypes
class FNameMap {
    private val nameEntries = mutableListOf<FNameEntryId>()
    private var nameMapType = FMappedName.EType.Global

    fun loadGlobal(ioDispatcher: FIoDispatcher) {
        check(nameEntries.isEmpty())

        val namesId = createIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNames)
        val hashesId = createIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNameHashes)

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

    fun getName(mappedName: FMappedName): FName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        val nameEntry = nameEntries[mappedName.getIndex().toInt()]
        // return FName.createFromDisplayId(nameEntry, mappedName.getNumber())
        return FName.dummy("TODO")
    }

    fun tryGetName(mappedName: FMappedName): FName? {
        check(mappedName.getType() == nameMapType)
        val index = mappedName.getIndex()
        if (index < nameEntries.size.toUInt()) {
            val nameEntry = nameEntries[mappedName.getIndex().toInt()]
            // return FName.createFromDisplayId(nameEntry, mappedName.getNumber())
            return FName.dummy("TODO")
        }
        return null
    }

    fun getMinimalName(mappedName: FMappedName): FMinimalName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        val nameEntry = nameEntries[mappedName.getIndex().toInt()]
        return FMinimalName(nameEntry, mappedName.number.toInt())
    }
}