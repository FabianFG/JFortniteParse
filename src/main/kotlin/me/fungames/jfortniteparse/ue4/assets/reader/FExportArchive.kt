package me.fungames.jfortniteparse.ue4.assets.reader

import me.fungames.jfortniteparse.GExportArchiveCheckDummyName
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.IoPackage
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.asyncloading2.FMappedName
import me.fungames.jfortniteparse.ue4.io.EIoChunkType
import me.fungames.jfortniteparse.ue4.io.FIoChunkId
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import java.nio.ByteBuffer

class FExportArchive(data: ByteBuffer, val obj: UObject, val pkg: IoPackage) : FAssetArchive(data, pkg.provider, pkg.fileName) {
    init {
        game = pkg.game.game
        ver = pkg.game.version
        owner = pkg
    }

    override fun getPayload(type: PayloadType) = payloads.getOrPut(type) {
        if (provider == null)
            throw ParserException("Lazy loading a $type requires a file provider")
        val payloadChunkId = FIoChunkId(pkg.packageId.value(), 0u, when (type) {
            PayloadType.UBULK -> EIoChunkType.BulkData
            PayloadType.M_UBULK -> EIoChunkType.MemoryMappedBulkData
            PayloadType.UPTNL -> EIoChunkType.OptionalBulkData
        })
        val ioBuffer = runCatching { provider!!.saveChunk(payloadChunkId) }.getOrElse { ByteArray(0) }
        FAssetArchive(ioBuffer, provider, pkgName)
    }

    override fun clone(): FExportArchive {
        val c = FExportArchive(data.duplicate(), obj, pkg)
        c.game = game
        c.ver = ver
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

    fun checkDummyName(dummyName: String) {
        if (GExportArchiveCheckDummyName && dummyName !in pkg.nameMap.nameEntries) {
            UClass.logger.warn("$dummyName is not in the package name map. There must be something wrong.")
        }
    }

    override fun printError() = "FExportArchive Info: pos $pos, stopper $size, object ${obj.getPathName()}"
}