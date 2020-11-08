package me.fungames.jfortniteparse.util

import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.kotlinPointers.BytePointer
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.atomic.AtomicReferenceArray
import javax.imageio.ImageIO
import kotlin.jvm.internal.Ref.IntRef
import kotlin.jvm.internal.Ref.ObjectRef

fun BufferedImage.scale(newWidth: Int, newHeight: Int, flags: Int = Image.SCALE_SMOOTH): BufferedImage {
    val scaled = this.getScaledInstance(newWidth, newHeight, flags)
    val scaledImg = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB)
    val g2 = scaledImg.createGraphics()
    g2.drawImage(scaled, 0, 0, null)
    g2.dispose()
    return scaledImg
}

fun BufferedImage.cut(newWidth: Int): BufferedImage {
    val startX = (this.width / 2) - (newWidth / 2)
    return this.getSubimage(startX, 0, newWidth, this.height)
}

fun Graphics2D.drawCenteredString(s: String, x: Int, y: Int) {
    drawString(s, x - fontMetrics.stringWidth(s) / 2, y)
}

fun BufferedImage.toPngArray(): ByteArray {
    val out = ByteArrayOutputStream()
    ImageIO.write(this, "png", out)
    return out.toByteArray()
}

fun BytePointer.toUInt32() = FByteArchive(byteArrayOf(this[0],
    this[1],
    this[2],
    this[3]))
    .readUInt32()

fun BytePointer.toInt64() = FByteArchive(byteArrayOf(this[0],
    this[1],
    this[2],
    this[3],
    this[4],
    this[5],
    this[6],
    this[7]))
    .readInt64()

inline operator fun <T> List<T>.get(index: UInt) = get(index.toInt())
inline operator fun <T> Array<T>.get(index: UInt) = get(index.toInt())

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
inline fun align(value: UInt, alignment: UInt) = value + alignment - 1u and (alignment - 1u).inv()
inline fun isAligned(value: Int, alignment: Int) = value and (alignment - 1) <= 0

fun AtomicInteger.compareExchange(expected: IntRef, value: Int): Boolean {
    val prevValue = get()
    val bResult = compareAndSet(expected.element, value)
    expected.element = prevValue
    return bResult
}

fun <T> AtomicReference<T>.compareExchange(expected: ObjectRef<T>, value: T): Boolean {
    val prevValue = get()
    val bResult = compareAndSet(expected.element, value)
    expected.element = prevValue
    return bResult
}

fun <E> AtomicReferenceArray<E>.compareExchange(i: Int, expected: ObjectRef<E>, value: E): Boolean {
    val prevValue = get(i)
    val bResult = compareAndSet(i, expected.element, value)
    expected.element = prevValue
    return bResult
}

fun BitSet.indexOfFirst(value: Boolean): Int {
    for (i in 0 until size()) {
        if (get(i) == value) {
            return i
        }
    }
    return INDEX_NONE
}

/** Divides two integers and rounds up */
fun UInt.divideAndRoundUp(divisor: UInt) = (this + divisor - 1u) / divisor