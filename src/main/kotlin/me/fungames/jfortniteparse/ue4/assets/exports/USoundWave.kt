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
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FFormatContainer

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

    var bCooked = false
    /** Uncompressed wav data 16 bit in mono or stereo - stereo not allowed for multichannel data */
    var rawData: FByteBulkData? = null
    /** GUID used to uniquely identify this node so it can be found in the DDC */
    var compressedDataGuid = FGuid()
    var compressedFormatData: FFormatContainer? = null
    /** The streaming derived data for this sound on this platform. */
    var runningPlatformData: FStreamedAudioPlatformData? = null

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        bCooked = Ar.readBoolean()
        val bShouldStreamSound = isStreaming()

        if (bCooked) {
            if (!bShouldStreamSound) {
                compressedFormatData = FFormatContainer(Ar)
            }
        } else {
            rawData = FByteBulkData(Ar)
        }

        compressedDataGuid = FGuid(Ar)

        if (bShouldStreamSound) {
            if (bCooked) {
                runningPlatformData = FStreamedAudioPlatformData(Ar)
            }
        }

        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        Ar.writeBoolean(bCooked)
        val bShouldStreamSound = isStreaming()

        if (bCooked) {
            if (!bShouldStreamSound) {
                compressedFormatData!!.serialize(Ar)
            }
        } else {
            rawData!!.serialize(Ar)
        }

        compressedDataGuid.serialize(Ar)

        if (bShouldStreamSound) {
            if (bCooked) {
                runningPlatformData!!.serialize(Ar)
            }
        }

        super.completeWrite(Ar)
    }

    fun isStreaming() = bStreaming || LoadingBehavior != ESoundWaveLoadingBehavior.ForceInline
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

/**
 * A chunk of streamed audio.
 */
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

/**
 * Platform-specific data used streaming audio at runtime.
 */
class FStreamedAudioPlatformData {
    var numChunks: Int
    var audioFormat: FName
    var chunks: MutableList<FStreamedAudioChunk>

    constructor(Ar: FAssetArchive) {
        numChunks = Ar.readInt32()
        audioFormat = Ar.readFName()
        chunks = MutableList(numChunks) { FStreamedAudioChunk(Ar) }
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        Ar.writeInt32(numChunks)
        Ar.writeFName(audioFormat)
        chunks.forEach { it.serialize(Ar) }
    }
}