package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FManifestHeader {
    var magic: UInt
    var headerSize: UInt
    var dataSizeUncompressed: UInt
    var dataSizeCompressed: UInt
    var shaHash: ByteArray
    var storedAs: UByte
    var version: Int

    companion object {
        const val MANIFEST_HEADER_MAGIC = 0x44BEC00Cu
    }

    constructor(Ar: FArchive) {
        val startPos = Ar.pos()
        magic = Ar.readUInt32()
        headerSize = Ar.readUInt32()
        dataSizeUncompressed = Ar.readUInt32()
        dataSizeCompressed = Ar.readUInt32()
        shaHash = Ar.read(20)
        storedAs = Ar.readUInt8()
        version = Ar.readInt32()
        Ar.seek(startPos + headerSize.toInt())
    }

    fun serialize(Ar: FArchiveWriter) {}
}