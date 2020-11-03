package me.fungames.jfortniteparse.compression

import me.fungames.jfortniteparse.exceptions.UnknownCompressionMethodException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.pak.CompressionMethod
import me.fungames.oodle.Oodle
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater

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
                compressed.copyInto(decompressed, 0, 0, compressed.size)
            }
            CompressionMethod.Zlib -> {
                val inflater = Inflater()
                inflater.setInput(compressed, 0, compressed.size)
                inflater.inflate(decompressed)
                inflater.end()
            }
            CompressionMethod.Gzip -> {
                val gzipIn = GZIPInputStream(ByteArrayInputStream(compressed))
                gzipIn.read(decompressed)
                gzipIn.close()
            }
            CompressionMethod.Oodle -> {
                Oodle.decompress(compressed, decompressed)
            }
            CompressionMethod.Unknown -> throw UnknownCompressionMethodException("Compression method is unknown")

        }
    }
    fun uncompressMemory(formatName: FName, uncompressedBuffer: ByteArray, uncompressedBufferOff: Int, uncompressedSize: Int, compressedBuffer: ByteArray, compressedBufferOff: Int, compressedSize: Int) {
        when (formatName.text) {
            "None" -> {
                assert(compressedSize == uncompressedSize)
                System.arraycopy(compressedBuffer, compressedBufferOff, uncompressedBuffer, uncompressedBufferOff, compressedSize)
            }
            "Zlib" -> {
                Inflater().apply {
                    setInput(compressedBuffer, compressedBufferOff, compressedSize)
                    inflate(uncompressedBuffer, uncompressedBufferOff, uncompressedSize)
                    end()
                }
            }
            "Gzip" -> {
                GZIPInputStream(ByteArrayInputStream(compressedBuffer, compressedBufferOff, compressedSize)).use {
                    it.read(uncompressedBuffer, uncompressedBufferOff, uncompressedSize)
                }
            }
            "Oodle" -> {
                Oodle.decompress(compressedBuffer, uncompressedBuffer) // TODO pos
            }
            else -> throw UnknownCompressionMethodException("Compression method is unknown")
        }
    }
}