package me.fungames.jfortniteparse.ue4.assets.reader

import me.fungames.jfortniteparse.GExportArchiveCheckDummyName
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.IoPackage
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.asyncloading2.FMappedName
import me.fungames.jfortniteparse.ue4.io.EIoChunkType
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_High
import me.fungames.jfortniteparse.ue4.io.FIoChunkId
import me.fungames.jfortniteparse.ue4.io.FIoReadOptions
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.util.await
import java.nio.ByteBuffer
import java.util.concurrent.CompletableFuture

class FExportArchive(data: ByteBuffer, val pkg: IoPackage) : FAssetArchive(data, null, pkg.fileName) {
    init {
        owner = pkg
    }

    override fun getPayload(type: PayloadType) = payloads.getOrPut(type) {
        val batch = pkg.globalPackageStore.ioDispatcher.newBatch()
        val request = batch.read(
            FIoChunkId(pkg.packageId.value(), 0u, if (type == PayloadType.UBULK) EIoChunkType.BulkData else EIoChunkType.OptionalBulkData),
            FIoReadOptions(),
            IoDispatcherPriority_High.value
        )

        val batchCompletedEvent = CompletableFuture<Void>()
        batch.issueAndTriggerEvent(batchCompletedEvent)
        batchCompletedEvent.await()
        return FAssetArchive(request.result.getOrThrow(), provider, pkgName)
    }

    override fun handleBadNameIndex(nameIndex: Int) {
        throw ParserException("FName could not be read, requested index $nameIndex, name map size ${pkg.nameMap.nameEntries.size}", this)
    }

    override fun readFName(): FName {
        val nameIndex = readUInt32()
        val number = readUInt32()

        val mappedName = FMappedName.create(nameIndex, number, FMappedName.EType.Package)
        var name = pkg.nameMap.tryGetName(mappedName)
        if (name == null) {
            handleBadNameIndex(nameIndex.toInt())
            name = FName()
        }
        return name
    }

    fun checkDummyName(dummyName: String) {
        if (GExportArchiveCheckDummyName && dummyName !in pkg.nameMap.nameEntries) {
            UClass.logger.warn("$dummyName is not in the package name map. There must be something wrong.")
        }
    }
}