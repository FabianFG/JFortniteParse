package me.fungames.jfortniteparse.ue4.converters

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.exceptions.ParserException
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
    LOG_JFP.debug("Starting to convert USoundWave")
    return if (!isStreaming()) {
        if (bCooked) {
            LOG_JFP.debug("Found cooked sound data, exporting...")
            val compressedFormatData = this.compressedFormatData?.formats ?: throw ParserException("Cooked sounds need compressed format data")
            require(!compressedFormatData.isNullOrEmpty())
            LOG_JFP.debug("Done")
            val firstData = compressedFormatData.entries.first()
            var exportFormat = firstData.key.text
            if (exportFormat.startsWith("OGG1")) {
                exportFormat = "OGG"
            }
            SoundWave(firstData.value.data, exportFormat)
        } else {
            LOG_JFP.debug("Found non-cooked sound data, exporting...")
            val rawData = this.rawData ?: throw ParserException("Non-cooked sounds need raw data")
            LOG_JFP.debug("Done")
            SoundWave(rawData.data, "ogg")
        }
    } else {
        val runningPlatformData = runningPlatformData ?: throw ParserException("Streamed sounds need streamed audio chunks")
        val streamedChunks = runningPlatformData.chunks
        LOG_JFP.debug("Found streamed sound data, exporting...")
        val data = ByteArray(streamedChunks.sumOf { it.audioDataSize })
        var dataOff = 0
        var exportFormat = runningPlatformData.audioFormat.text
        if (exportFormat.startsWith("OGG1")) {
            exportFormat = "OGG"
        }
        streamedChunks.forEach {
            System.arraycopy(it.data.data, 0, data, dataOff, it.audioDataSize)
            dataOff += it.audioDataSize
        }
        LOG_JFP.debug("Done")
        SoundWave(data, exportFormat)
    }
}