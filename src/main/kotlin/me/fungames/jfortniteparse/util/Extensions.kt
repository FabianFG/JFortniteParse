package me.fungames.jfortniteparse.util

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import java.awt.Graphics2D
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import javax.imageio.ImageIO
import kotlin.jvm.internal.Ref.ObjectRef
import kotlin.math.min

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

inline operator fun <T> List<T>.get(index: UInt) = get(index.toInt())
inline operator fun <T> Array<T>.get(index: UInt) = get(index.toInt())
inline operator fun ByteArray.get(index: UInt) = get(index.toInt())

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
        val start = if (str[0] == '/' || str[0] == '\\') 1 else 0
        data.append(str, start, min(str.length, strLength))
        //data.append(str, 0, min(str.length, strLength))
    }
    return data.toString()
}

inline operator fun String.div(other: String) = pathAppend(other)
inline fun align(value: Int, alignment: Int) = value + alignment - 1 and (alignment - 1).inv()
inline fun align(value: ULong, alignment: ULong) = value + alignment - 1u and (alignment - 1u).inv()
inline fun align(value: UInt, alignment: UInt) = value + alignment - 1u and (alignment - 1u).inv()
inline fun isAligned(value: Int, alignment: Int) = value and (alignment - 1) <= 0

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

inline fun <T> T.ref() = ObjectRef<T>().also { it.element = this }

inline fun FName?.isNone() = this == null || this.isNone()