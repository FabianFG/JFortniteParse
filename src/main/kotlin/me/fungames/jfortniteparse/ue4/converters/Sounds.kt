package me.fungames.jfortniteparse.ue4.converters

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.USoundWave

data class SoundWave(var data: ByteArray, var format: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as SoundWave
        if (!data.contentEquals(other.data)) return false
        if (format != other.format) return false
        return true
    }

    override fun hashCode(): Int {
        var result = data.contentHashCode()
        result = 31 * result + format.hashCode()
        return result
    }
}

@Throws(IllegalArgumentException::class)
fun USoundWave.convert(): SoundWave {
    UClass.logger.debug("Starting to convert USoundWave")
    return if (!bStreaming) {
        if (bCooked) {
            UClass.logger.debug("Found cooked sound data, exporting...")
            val compressedFormatData = this.compressedFormatData ?: throw ParserException("Cooked sounds need compressed format data")
            require(!compressedFormatData.isNullOrEmpty())
            UClass.logger.debug("Done")
            if (compressedFormatData[0].formatName.text.startsWith("OGG1")) {
                compressedFormatData[0].formatName.text = "OGG"
            }
            SoundWave(
                compressedFormatData[0].data.data,
                compressedFormatData[0].formatName.text
            )
        } else {
            UClass.logger.debug("Found non-cooked sound data, exporting...")
            val rawData = this.rawData ?: throw ParserException("Non-cooked sounds need raw data")
            UClass.logger.debug("Done")
            SoundWave(rawData.data, "ogg")
        }
    } else {
        val streamedChunks = this.streamedAudioChunks ?: throw ParserException("Streamed sounds need streamed audio chunks")
        UClass.logger.debug("Found streamed sound data, exporting...")
        val data = ByteArray(streamedChunks.sumBy { it.audioDataSize })
        var dataOff = 0
        if (this.format?.text?.startsWith("OGG1") == true) {
            this.format?.text = "OGG"
        }
        streamedChunks.forEach {
            System.arraycopy(it.data.data, 0, data, dataOff, it.audioDataSize)
            dataOff += it.audioDataSize
        }
        val format = this.format?.text ?: throw IllegalArgumentException("Streamed sounds need format")
        UClass.logger.debug("Done")
        SoundWave(data, format)
    }
}