package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.GExportArchiveCheckDummyName
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.io.EIoChunkType
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_High
import me.fungames.jfortniteparse.ue4.io.FIoChunkId
import me.fungames.jfortniteparse.ue4.io.FIoReadOptions
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.util.await
import org.slf4j.event.Level
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture

class FExportArchive(
    data: ByteBuffer,
    val packageDesc: FAsyncPackageDesc2,
    val importStore: FPackageImportStore,
    val externalReadDependencies: List<FExternalReadCallback>,
    val nameMap: FNameMap,
    val exports: Array<FExportObject>,
    val exportMap: Array<FExportMapEntry>) : FAssetArchive(data, null, packageDesc.diskPackageName.text) {
    var cookedHeaderSize = 0u
    var cookedSerialOffset = 0uL
    var cookedSerialSize = 0uL
    var bufferSerialOffset = 0uL

    override fun getPayload(type: PayloadType) = payloads.getOrPut(type) {
        val batch = importStore.globalPackageStore.ioDispatcher.newBatch()
        val request = batch.read(
            FIoChunkId(packageDesc.diskPackageId.value(), 0u, if (type == PayloadType.UBULK) EIoChunkType.BulkData else EIoChunkType.OptionalBulkData),
            FIoReadOptions(),
            IoDispatcherPriority_High.value
        )

        val batchCompletedEvent = CompletableFuture<Void>()
        batch.issueAndTriggerEvent(batchCompletedEvent)
        batchCompletedEvent.await()
        return FAssetArchive(request.result.getOrThrow(), provider, pkgName)
    }

    override fun handleBadNameIndex(nameIndex: Int) {
        asyncPackageLog(Level.ERROR, packageDesc, "HandleBadNameIndex",
            "Index: %d/%d".format(nameIndex, nameMap.size()))
        throw ParserException("FName could not be read, requested index $nameIndex, name map size ${nameMap.nameEntries.size}", this)
    }

    override fun readFName(): FName {
        val nameIndex = readUInt32()
        val number = readUInt32()

        val mappedName = FMappedName.create(nameIndex, number, FMappedName.EType.Package)
        var name = nameMap.tryGetName(mappedName)
        if (name == null) {
            handleBadNameIndex(nameIndex.toInt())
            name = FName()
        }
        return name
    }

    fun checkDummyName(dummyName: String) {
        if (GExportArchiveCheckDummyName && dummyName !in nameMap.nameEntries) {
            asyncPackageLog(Level.WARN, packageDesc, "CheckDummyName", "$dummyName is not in the package name map. There must be something wrong.")
        }
    }
}