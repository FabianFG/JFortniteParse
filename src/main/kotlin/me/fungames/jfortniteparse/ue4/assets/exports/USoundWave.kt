package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.enums.ESoundWaveLoadingBehavior
import me.fungames.jfortniteparse.ue4.assets.objects.FByteBulkData
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@OnlyAnnotated
class USoundWave : USoundBase() {
    @JvmField @UProperty var CompressionQuality = 0
    @JvmField @UProperty var StreamingPriority = 0
    @JvmField @UProperty var SampleRateQuality: ESoundwaveSampleRateSettings? = null
    @JvmField @UProperty var SoundGroup: ESoundGroup? = null
    @JvmField @UProperty var bLooping = false
    @JvmField @UProperty var bStreaming = false
    @JvmField @UProperty var bSeekableStreaming = false
    @JvmField @UProperty var LoadingBehavior: ESoundWaveLoadingBehavior? = null
    @JvmField @UProperty var bMature = false
    @JvmField @UProperty var bManualWordWrap = false
    @JvmField @UProperty var bSingleLine = false
    @JvmField @UProperty var bIsAmbisonics = false
    @JvmField @UProperty var FrequenciesToAnalyze: List<Float>? = null
    @JvmField @UProperty var CookedSpectralTimeData: List<FSoundWaveSpectralTimeData>? = null
    @JvmField @UProperty var CookedEnvelopeTimeData: List<FSoundWaveEnvelopeTimeData>? = null
    @JvmField @UProperty var InitialChunkSize = 0
    @JvmField @UProperty var SpokenText: String? = null
    @JvmField @UProperty var SubtitlePriority = 0.0f
    @JvmField @UProperty var Volume = 0.0f
    @JvmField @UProperty var Pitch = 0.0f
    @JvmField @UProperty var NumChannels = 0
    @JvmField @UProperty var SampleRate = 0
    @JvmField @UProperty var Subtitles: List<FSubtitleCue>? = null
    @JvmField @UProperty var Curves: FPackageIndex /*UCurveTable*/? = null
    @JvmField @UProperty var InternalCurves: FPackageIndex /*UCurveTable*/? = null

    var bCooked: Boolean = false
    var compressedFormatData: Array<FSoundFormatData>? = null
    var rawData: FByteBulkData? = null
    lateinit var compressedDataGuid: FGuid
    var format: FName? = null
    var streamedAudioChunks: Array<FStreamedAudioChunk>? = null

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        bCooked = Ar.readBoolean()
        //bStreaming = bStreaming ?: (Ar.game >= GAME_UE4(25)) // TODO check whether default is really true
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
            streamedAudioChunks = Ar.readTArray(numChunks) { FStreamedAudioChunk(Ar) }
        }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        Ar.writeBoolean(bCooked)
        if (!bStreaming) {
            if (bCooked) {
                val compressedFormatData = this.compressedFormatData ?: throw ParserException("A non-streamed cooked audio needs compressed format data", Ar)
                Ar.writeTArray(compressedFormatData) { it.serialize(Ar) }
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
            Ar.writeTArrayWithoutSize(streamedAudioChunks) { it.serialize(Ar) }
        }
        super.completeWrite(Ar)
    }
}

enum class ESoundwaveSampleRateSettings {
    Max,
    High,
    Medium,
    Low,
    Min,
    MatchDevice
}

enum class ESoundGroup {
    SOUNDGROUP_Default,
    SOUNDGROUP_Effects,
    SOUNDGROUP_UI,
    SOUNDGROUP_Music,
    SOUNDGROUP_Voice,
    SOUNDGROUP_GameSoundGroup1,
    SOUNDGROUP_GameSoundGroup2,
    SOUNDGROUP_GameSoundGroup3,
    SOUNDGROUP_GameSoundGroup4,
    SOUNDGROUP_GameSoundGroup5,
    SOUNDGROUP_GameSoundGroup6,
    SOUNDGROUP_GameSoundGroup7,
    SOUNDGROUP_GameSoundGroup8,
    SOUNDGROUP_GameSoundGroup9,
    SOUNDGROUP_GameSoundGroup10,
    SOUNDGROUP_GameSoundGroup11,
    SOUNDGROUP_GameSoundGroup12,
    SOUNDGROUP_GameSoundGroup13,
    SOUNDGROUP_GameSoundGroup14,
    SOUNDGROUP_GameSoundGroup15,
    SOUNDGROUP_GameSoundGroup16,
    SOUNDGROUP_GameSoundGroup17,
    SOUNDGROUP_GameSoundGroup18,
    SOUNDGROUP_GameSoundGroup19,
    SOUNDGROUP_GameSoundGroup20
}

@UStruct
class FSoundWaveSpectralTimeData {
    @JvmField @UProperty var Data: List<FSoundWaveSpectralDataEntry>? = null
    @JvmField @UProperty var TimeSec = 0.0f
}

@UStruct
class FSoundWaveSpectralDataEntry {
    @JvmField @UProperty var Magnitude = 0.0f
    @JvmField @UProperty var NormalizedMagnitude = 0.0f
}

@UStruct
class FSoundWaveEnvelopeTimeData {
    @JvmField @UProperty var Amplitude = 0.0f
    @JvmField @UProperty var TimeSec = 0.0f
}

@UStruct
class FSubtitleCue {
    @JvmField @UProperty var Text: FText? = null
    @JvmField @UProperty var Time = 0.0f
}

class FSoundFormatData : UClass {
    var formatName: FName
    var data: FByteBulkData

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

    constructor(formatName: FName, data: FByteBulkData) {
        this.formatName = formatName
        this.data = data
    }
}

class FStreamedAudioChunk : UClass {
    var bCooked: Boolean
    var data: FByteBulkData
    var dataSize: Int
    var audioDataSize: Int

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

    constructor(bCooked: Boolean, data: FByteBulkData, dataSize: Int, audioDataSize: Int) {
        this.bCooked = bCooked
        this.data = data
        this.dataSize = dataSize
        this.audioDataSize = audioDataSize
    }
}