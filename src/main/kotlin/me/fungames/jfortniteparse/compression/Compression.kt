package me.fungames.jfortniteparse.compression

import me.fungames.oodle.Oodle
import java.io.ByteArrayInputStream
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater

object Compression {
    val handlers = mutableMapOf<String, CompressionHandler>()

    init {
        handlers["None"] = CompressionHandler { dst, dstOff, dstLen, src, srcOff, srcLen ->
            assert(srcLen == dstLen)
            System.arraycopy(src, srcOff, dst, dstOff, srcLen)
        }
        handlers["Zlib"] = CompressionHandler { dst, dstOff, dstLen, src, srcOff, srcLen ->
            Inflater().apply {
                setInput(src, srcOff, srcLen)
                inflate(dst, dstOff, dstLen)
                end()
            }
        }
        handlers["Gzip"] = CompressionHandler { dst, dstOff, dstLen, src, srcOff, srcLen ->
            GZIPInputStream(ByteArrayInputStream(src, srcOff, srcLen)).use {
                it.read(dst, dstOff, dstLen)
            }
        }
        handlers["Oodle"] = CompressionHandler { dst, dstOff, dstLen, src, srcOff, srcLen ->
            Oodle.decompress(src, srcOff, srcLen, dst, dstOff, dstLen)
        }
    }

    inline fun uncompressMemory(formatName: String, compressedBuffer: ByteArray, uncompressedSize: Int) =
        ByteArray(uncompressedSize).also { uncompressMemory(formatName, it, compressedBuffer) }

    inline fun uncompressMemory(formatName: String, uncompressedBuffer: ByteArray, compressedBuffer: ByteArray) {
        uncompressMemory(formatName, uncompressedBuffer, 0, uncompressedBuffer.size, compressedBuffer, 0, compressedBuffer.size)
    }

    fun uncompressMemory(formatName: String,
                         uncompressedBuffer: ByteArray, uncompressedBufferOff: Int, uncompressedSize: Int,
                         compressedBuffer: ByteArray, compressedBufferOff: Int, compressedSize: Int) {
        val handler = handlers[formatName]
            ?: throw UnknownCompressionMethodException("Unknown compression method $formatName")
        handler.decompress(uncompressedBuffer, uncompressedBufferOff, uncompressedSize, compressedBuffer, compressedBufferOff, compressedSize)
    }
}

fun interface CompressionHandler {
    fun decompress(dst: ByteArray, dstOff: Int, dstLen: Int, src: ByteArray, srcOff: Int, srcLen: Int)
}

class UnknownCompressionMethodException(message: String?, cause: Throwable? = null) : RuntimeException(message, cause)