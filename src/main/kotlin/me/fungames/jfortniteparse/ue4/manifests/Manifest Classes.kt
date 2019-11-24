package me.fungames.jfortniteparse.ue4.manifests

import me.fungames.jfortniteparse.compression.Compression
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.pak.CompressionMethod
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.io.File

@ExperimentalUnsignedTypes
class FManifestData : UEClass {

    var header : FManifestHeader
    var meta : FManifestMeta
    var chunkDataList : FChunkDataList
    var fileManifestList : FFileManifestList
    var customFields : FCustomFields


    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        header = FManifestHeader(Ar)
        var manifestRawData = Ar.read(header.dataSizeCompressed.toInt())
        if (header.storedAs.toInt() and 1 != 0) {
            //Compressed, only compression format is ZLib
            manifestRawData = Compression.decompress(manifestRawData, header.dataSizeUncompressed.toInt(), CompressionMethod.Zlib)
        }
        val rawAr = FByteArchive(manifestRawData)
        meta = FManifestMeta(rawAr)
        chunkDataList = FChunkDataList(rawAr)
        fileManifestList = FFileManifestList(rawAr)
        customFields = FCustomFields(rawAr)
        Ar.seek(startPos + header.headerSize.toInt() + header.dataSizeCompressed.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchive) {

    }
}

@ExperimentalUnsignedTypes
class FChunkDataList : UEClass {

    var chunkList : Array<FChunkInfo>

    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        val elementCount = Ar.readInt32()
        chunkList = Array(elementCount) { FChunkInfo() }
        for (chunkInfo in chunkList) chunkInfo.guid = FGuid(Ar)
        for (chunkInfo in chunkList) chunkInfo.hash = Ar.readUInt64()
        for (chunkInfo in chunkList) chunkInfo.shaHash = Ar.read(20)
        for (chunkInfo in chunkList) chunkInfo.groupNumber = Ar.readUInt8()
        for (chunkInfo in chunkList) chunkInfo.windowSize = Ar.readUInt32()
        for (chunkInfo in chunkList) chunkInfo.fileSize = Ar.readInt64()
        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchive) {

    }
}

@ExperimentalUnsignedTypes
class FFileManifestList : UEClass {

    var fileList : Array<FFileManifest>

    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        val elementCount = Ar.readInt32()
        fileList = Array(elementCount) { FFileManifest() }
        for (fileManifest in fileList) fileManifest.fileName = Ar.readString()
        for (fileManifest in fileList) fileManifest.symlinkTarget = Ar.readString()
        for (fileManifest in fileList) fileManifest.fileHash = Ar.read(20)
        for (fileManifest in fileList) fileManifest.fileMetaFlags = Ar.readUInt8()
        for (fileManifest in fileList) fileManifest.installTags = Ar.readTArray { it.readString() }
        for (fileManifest in fileList) fileManifest.chunkParts = Ar.readTArray { FChunkPart(Ar) }
        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchive) {

    }
}

@ExperimentalUnsignedTypes
data class FFileManifest(
    var fileName : String = "",
    var symlinkTarget : String = "",
    var fileHash : ByteArray = ByteArray(0),
    var fileMetaFlags : UByte = 0.toUByte(),
    var installTags : Array<String> = emptyArray(),
    var chunkParts : Array<FChunkPart> = emptyArray(),
    var fileSize : ULong = 0.toULong()
)

@ExperimentalUnsignedTypes
class FChunkPart : UEClass {
    var guid : FGuid
    var offset : UInt
    var size : UInt

    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        guid = FGuid(Ar)
        offset = Ar.readUInt32()
        size = Ar.readUInt32()
        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        guid.serialize(Ar)
        Ar.writeUInt32(offset)
        Ar.writeUInt32(size)
        super.completeWrite(Ar)
    }
}

@ExperimentalUnsignedTypes
class FCustomFields : UEClass {

    var fields : Map<String, String>

    constructor(Ar: FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        val elementCount = Ar.readInt32()

        data class MutablePair<A, B>(var first: A, var second: B)

        val arrayFields = Array(elementCount) {MutablePair("", "")}
        for (field in arrayFields) field.first = Ar.readString()
        for (field in arrayFields) field.second = Ar.readString()
        val fields = mutableMapOf<String, String>()
        for ((first, second) in arrayFields)
            fields[first] = second
        this.fields = fields
        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }


    fun serialize(Ar : FArchive) {

    }
}

@ExperimentalUnsignedTypes
data class FChunkInfo(
    var guid : FGuid = FGuid(0u, 0u, 0u,0u),
    var hash : ULong = 0.toULong(),
    var shaHash : ByteArray = ByteArray(0),
    var groupNumber : UByte = 0.toUByte(),
    var windowSize : UInt = 0u,
    var fileSize : Long = 0L
)

@ExperimentalUnsignedTypes
class FManifestMeta : UEClass {

    var isFileDataInt : Boolean
    var appId : UInt
    var appName : String
    var buildVersion : String
    var launchExe : String
    var launchCommand : String
    var prereqIds : Array<String>
    var prereqName : String
    var prereqPath : String
    var prereqArgs : String


    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        /*val featureLevelInt = */Ar.readInt32()
        isFileDataInt = Ar.readFlag()
        appId = Ar.readUInt32()
        appName = Ar.readString()
        buildVersion = Ar.readString()
        launchExe = Ar.readString()
        launchCommand = Ar.readString()
        prereqIds = Ar.readTArray { it.readString() }
        prereqName = Ar.readString()
        prereqPath = Ar.readString()
        prereqArgs = Ar.readString()

        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchive) {

    }
}

@ExperimentalUnsignedTypes
class FManifestHeader : UEClass {

    var magic : UInt
    var headerSize : UInt
    var dataSizeUncompressed : UInt
    var dataSizeCompressed : UInt
    var shaHash : ByteArray
    var storedAs : UByte
    var version : Int

    companion object {
        const val MANIFEST_HEADER_MAGIC = 0x44BEC00Cu
    }

    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        magic = Ar.readUInt32()
        headerSize = Ar.readUInt32()
        dataSizeUncompressed = Ar.readUInt32()
        dataSizeCompressed = Ar.readUInt32()
        shaHash = Ar.read(20)
        storedAs = Ar.readUInt8()
        version = Ar.readInt32()
        Ar.seek(startPos + headerSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchive) {

    }
}