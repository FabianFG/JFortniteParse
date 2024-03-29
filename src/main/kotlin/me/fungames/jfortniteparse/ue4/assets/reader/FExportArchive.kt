package me.fungames.jfortniteparse.ue4.assets.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.IoPackage
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.io.EIoChunkType
import me.fungames.jfortniteparse.ue4.io.EIoChunkType5
import me.fungames.jfortniteparse.ue4.io.FIoChunkId
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FMappedName
import me.fungames.jfortniteparse.ue4.versions.GAME_UE5_BASE
import java.nio.ByteBuffer

class FExportArchive(data: ByteBuffer, val obj: UObject, val pkg: IoPackage) : FAssetArchive(data, pkg.provider, pkg.fileName) {
    init {
        versions = pkg.versions
        owner = pkg
    }

    override fun getPayload(type: PayloadType) = payloads.getOrPut(type) {
        if (provider == null)
            throw ParserException("Lazy loading a $type requires a file provider")
        val chunkType = if (game >= GAME_UE5_BASE) when (type) {
            PayloadType.UBULK -> EIoChunkType5.BulkData
            PayloadType.M_UBULK -> EIoChunkType5.MemoryMappedBulkData
            PayloadType.UPTNL -> EIoChunkType5.OptionalBulkData
        } else when (type) {
            PayloadType.UBULK -> EIoChunkType.BulkData
            PayloadType.M_UBULK -> EIoChunkType.MemoryMappedBulkData
            PayloadType.UPTNL -> EIoChunkType.OptionalBulkData
        }
        val payloadChunkId = FIoChunkId(pkg.packageId.value(), 0u, chunkType)
        val ioBuffer = runCatching { provider!!.saveChunk(payloadChunkId) }.getOrElse { ByteArray(0) }
        FAssetArchive(ioBuffer, provider, pkgName)
    }

    override fun clone(): FExportArchive {
        val c = FExportArchive(data.duplicate(), obj, pkg)
        c.versions = versions
        c.useUnversionedPropertySerialization = useUnversionedPropertySerialization
        c.isFilterEditorOnly = isFilterEditorOnly
        c.littleEndian = littleEndian
        c.pos = pos
        payloads.forEach { c.payloads[it.key] = it.value }
        c.uassetSize = uassetSize
        c.uexpSize = uexpSize
        c.bulkDataStartOffset = bulkDataStartOffset
        return c
    }

    override fun handleBadNameIndex(nameIndex: Int) {
        throw ParserException("FName could not be read, requested index $nameIndex, name map size ${pkg.nameMap.nameEntries.size}", this)
    }

    override fun readFName(): FName {
        val nameIndex = readUInt32()
        val number = readUInt32()

        if (nameIndex > Int.MAX_VALUE.toUInt()) {
            throw ParserException("FName could not be read, bad FMappedName index", this)
        }
        val mappedName = FMappedName.create(nameIndex, number, FMappedName.EType.Package)
        var name = pkg.nameMap.getNameOrNull(mappedName)
        if (name == null) {
            handleBadNameIndex(nameIndex.toInt())
            name = FName()
        }
        return name
    }

    override fun printError() = "FExportArchive Info: pos $pos, stopper $size, object ${obj.getPathName()}"
}