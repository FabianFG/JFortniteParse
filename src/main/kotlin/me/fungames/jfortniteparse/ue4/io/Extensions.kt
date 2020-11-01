package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.exceptions.UnknownCompressionMethodException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.oodle.Oodle
import java.io.ByteArrayInputStream
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.zip.GZIPInputStream
import java.util.zip.Inflater

fun <T> Future<T>.await(): T {
    try {
        return get()
    } catch (e: ExecutionException) {
        throw e.cause!!
    }
}

fun <T> CompletableFuture<T>.complete(result: Result<T>) = result.fold(::complete, ::completeExceptionally)

fun String.pathAppend(str: String, strLength: Int = str.length): String {
    val data = StringBuilder(this)
    val dataNum = data.length
    if (dataNum > 0 && data[dataNum - 1] != '/' && data[dataNum - 1] != '\\') {
        data.append('/')
    }
    if (strLength > 0) {
        data.append(str, 0, kotlin.math.min(str.length, strLength))
    }
    return data.toString()
}

inline operator fun String.div(other: String) = pathAppend(other)
inline fun align(value: ULong, alignment: ULong) = value + alignment - 1u and (alignment - 1u).inv()
inline fun align(value: UInt, alignment: ULong) = value + alignment - 1u and (alignment - 1u).inv()
inline fun align(value: UInt, alignment: UInt) = value + alignment - 1u and (alignment - 1u).inv()
inline fun isAligned(value: Int, alignment: Int) = value and (alignment - 1) <= 0

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