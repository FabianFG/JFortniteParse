package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.assets.*
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class USoundWave : UEExport {

    override var baseObject: UObject
    var bCooked : Boolean
    var bStreaming : Boolean

    var compressedFormatData : Array<FSoundFormatData>? = null
    var rawData : FByteBulkData? = null
    var compressedDataGuid : FGuid
    var format : FName? = null
    var streamedAudioChunks : Array<FStreamedAudioChunk>? = null

    constructor(Ar : FAssetArchive, exportObject : FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        bCooked = Ar.readBoolean()
        bStreaming = baseObject.getOrDefault("bStreaming", false)
        if (!bStreaming) {
            if (bCooked) {
                compressedFormatData = Ar.readTArray { FSoundFormatData(Ar) }
                format = compressedFormatData?.firstOrNull()?.formatName
            } else {
                rawData = FByteBulkData(Ar)
            }
            compressedDataGuid = FGuid(Ar)
        } else {
            compressedDataGuid = FGuid(Ar)
            val numChunks = Ar.readInt32()
            format = Ar.readFName()
            streamedAudioChunks = Ar.readTArray(numChunks) {FStreamedAudioChunk(Ar)}
        }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        Ar.writeBoolean(bCooked)
        if (!bStreaming) {
            if (bCooked) {
                val compressedFormatData = this.compressedFormatData ?: throw ParserException("A non-streamed cooked audio needs compressed format data", Ar)
                Ar.writeTArray(compressedFormatData) {it.serialize(Ar)}
            } else {
                val rawData = this.rawData ?: throw ParserException("A non-streamed non-cooked audio needs raw data", Ar)
                rawData.serialize(Ar)
            }
            compressedDataGuid.serialize(Ar)
        } else {
            compressedDataGuid.serialize(Ar)
            val streamedAudioChunks = this.streamedAudioChunks ?: throw ParserException("A streamed audio needs streamed audio chunks", Ar)
            Ar.writeInt32(streamedAudioChunks.size)
            val format = this.format ?: throw ParserException("A streamed audio needs a format", Ar)
            Ar.writeFName(format)
            Ar.writeTArrayWithoutSize(streamedAudioChunks) {it.serialize(Ar)}
        }
        super.completeWrite(Ar)
    }

    constructor(baseObject : UObject, bCooked : Boolean, bStreaming : Boolean,
                compressedFormatData : Array<FSoundFormatData>? = null, rawData : FByteBulkData? = null,
                compressedDataGuid : FGuid, format : FName? = null, streamedAudioChunks : Array<FStreamedAudioChunk>? = null, exportType: String) : super(exportType) {
        this.baseObject = baseObject
        this.bCooked = bCooked
        this.bStreaming = bStreaming
        this.compressedFormatData = compressedFormatData
        this.rawData = rawData
        this.compressedDataGuid = compressedDataGuid
        this.format = format
        this.streamedAudioChunks = streamedAudioChunks
    }
}

@ExperimentalUnsignedTypes
class FSoundFormatData : UEClass {
    var formatName : FName
    var data : FByteBulkData

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        formatName = Ar.readFName()
        data = FByteBulkData(Ar)
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(formatName)
        data.serialize(Ar)
        super.completeWrite(Ar)
    }

    constructor(formatName : FName, data : FByteBulkData) {
        this.formatName = formatName
        this.data = data
    }
}

@ExperimentalUnsignedTypes
class FStreamedAudioChunk : UEClass {
    var bCooked : Boolean
    var data : FByteBulkData
    var dataSize : Int
    var audioDataSize : Int

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        bCooked = Ar.readBoolean()
        if (bCooked) {
            data = FByteBulkData(Ar)
            dataSize = Ar.readInt32()
            audioDataSize = Ar.readInt32()
        } else
            throw ParserException("StreamedAudioChunks must be cooked", Ar)
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeBoolean(bCooked)
        if (bCooked) {
            data.serialize(Ar)
            Ar.writeInt32(dataSize)
            Ar.writeInt32(audioDataSize)
        } else
            throw ParserException("StreamedAudioChunks must be cooked", Ar)
        super.completeWrite(Ar)
    }

    constructor(bCooked: Boolean, data: FByteBulkData, dataSize : Int, audioDataSize : Int) {
        this.bCooked = bCooked
        this.data = data
        this.dataSize = dataSize
        this.audioDataSize = audioDataSize
    }
}