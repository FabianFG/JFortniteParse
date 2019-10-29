package me.fungames.jfortniteparse.compression

import me.fungames.jfortniteparse.exceptions.UnknownCompressionMethodException
import me.fungames.jfortniteparse.ue4.pak.CompressionMethod
import me.fungames.oodle.Oodle
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

object Compression {
    fun decompress(compressed : ByteArray, decompressedSize : Int, method : CompressionMethod) : ByteArray {
        val decompressed = ByteArray(decompressedSize)
        decompress(compressed, decompressed, method)
        return decompressed
    }
    fun decompress(compressed: ByteArray, decompressed : ByteArray, method: CompressionMethod) {
        when(method) {
            CompressionMethod.None -> {
                assert(compressed.size == decompressed.size)
                System.arraycopy(compressed, 0, decompressed, 0, compressed.size)
            }
            CompressionMethod.Zlib -> {
                val iis = InflaterInputStream(ByteArrayInputStream(compressed))
                iis.read(decompressed)
            }
            CompressionMethod.Gzip -> {
                val gzipIn = GZIPInputStream(ByteArrayInputStream(compressed))
                gzipIn.read(decompressed)
            }
            CompressionMethod.Oodle -> {
                Oodle.decompress(compressed, decompressed)
            }
            CompressionMethod.Unknown -> throw UnknownCompressionMethodException("Compression method is unknown")

        }
    }
}