@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.assets.exports.UObject

class FExportObject {
    var `object`: UObject? = null
    var bFiltered = false
    var bExportLoadFailed = false
}


class FNameMap {
    private val nameEntries = mutableListOf<Int>()
//    private val nameEntries = mutableListOf<FNameEntryId>()
//    private val nameMapType = FMappedName.EType.Global

    fun loadGlobal(ioDispatcher: FIoDispatcher) {
        check(nameEntries.isEmpty())

        val namesId = createIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNames)
        val hashesId = createIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNameHashes)

        val batch = ioDispatcher.newBatch()
        val nameRequest = batch.read(namesId, FIoReadOptions())
        val hashRequest = batch.read(hashesId, FIoReadOptions())
        batch.issue(EIoDispatcherPriority.IoDispatcherPriority_High)

        /*reserveNameBatch(
            ioDispatcher.getSizeForChunk(namesId).ValueOrDie(),
            ioDispatcher.getSizeForChunk(hashesId).ValueOrDie())

        batch.wait()

        val nameBuffer = nameRequest.getResultOrThrow()
        val hashBuffer = hashRequest.getResultOrThrow()

        load(MakeArrayView(nameBuffer.Data(), nameBuffer.DataSize()), MakeArrayView(hashBuffer.Data(), hashBuffer.DataSize()), FMappedName.EType.Global)

        ioDispatcher.freeBatch(batch)*/
    }

    fun size() = nameEntries.size
}
