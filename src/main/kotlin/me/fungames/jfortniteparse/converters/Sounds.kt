package me.fungames.jfortniteparse.converters

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.assets.exports.USoundWave

data class SoundWave(var data : ByteArray, var format : String) {
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
fun USoundWave.convert() : SoundWave {
    UEClass.logger.debug("Starting to convert USoundWave")
    return if (!bStreaming) {
        if (bCooked) {
            UEClass.logger.debug("Found cooked sound data, exporting...")
            val compressedFormatData = this.compressedFormatData ?: throw ParserException("Cooked sounds need compressed format data")
            require(!compressedFormatData.isNullOrEmpty())
            UEClass.logger.debug("Done")
            SoundWave(compressedFormatData[0].data.data, compressedFormatData[0].formatName.text)
        } else {
            UEClass.logger.debug("Found non-cooked sound data, exporting...")
            val rawData = this.rawData ?: throw ParserException("Non-cooked sounds need raw data")
            UEClass.logger.debug("Done")
            SoundWave(rawData.data, "ogg")
        }
    } else {
        val streamedChunks = this.streamedAudioChunks ?: throw ParserException("Streamed sounds need streamed audio chunks")
        UEClass.logger.debug("Found streamed sound data, exporting...")
        var data = ByteArray(0)
        streamedChunks.iterator().forEach { data +=  it.data.data.copyOfRange(0, it.audioDataSize)}
        val format = this.format?.text ?: throw IllegalArgumentException("Streamed sounds need format")
        UEClass.logger.debug("Done")
        SoundWave(data, format)
    }
}