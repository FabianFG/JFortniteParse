@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.textures

import com.tomgibara.bits.BitReader
import com.tomgibara.bits.Bits
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


private fun getPixelLoc(width: Int, x: Int, y: Int, off: Int): Int {
    return (y * width + x) * 3 + off
}

private fun getZNormal(x: UByte, y: UByte): UByte {
    val xf = (x.toFloat() / 127.5) - 1.0f
    val yf = (y.toFloat() / 127.5) - 1.0f
    val zval = min(
        sqrt(max(1.0f - xf * xf - yf * yf, 0.0)),
        1.0
    ).toFloat()
    val f = zval * 127.0 + 128.0
    return f.toByte().toUByte()
}


private class UByteArrayInputStream(buf: ByteArray, offset: Int = 0, length: Int = buf.size) :
    ByteArrayInputStream(buf, offset, length) {
    fun read(b: UByteArray): Int {
        return super.read(b.asByteArray())
    }
}

internal fun readBC5(data : ByteArray, width : Int, height : Int) : ByteArray {
    //val start = System.currentTimeMillis()
    val res = UByteArray(width * height * 3)

    val bin = UByteArrayInputStream(data)

    for (yBlock in 0 until height / 4) {
        for (xBlock in 0 until width / 4) {
            val rBytes = decodeBC3Block(bin)
            val gBytes = decodeBC3Block(bin)
            for (r in 0..15) {
                val xOff = r % 4
                val yOff = r / 4
                res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 0)] = rBytes[r]
            }
            for (g in 0..15) {
                val xOff = g % 4
                val yOff = g / 4
                res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 1)] = gBytes[g]
            }
            for (b in 0..15) {
                val xOff = b % 4
                val yOff = b / 4
                val bVal = getZNormal(rBytes[b], gBytes[b])
                res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 2)] = bVal
            }
        }
    }
    bin.close()
    //System.err.println("Decoding BC5 $width * $height took ${System.currentTimeMillis() - start}ms")
    return res.asByteArray()
}

@Throws(IOException::class)
private fun decodeBC3Block(bin: UByteArrayInputStream): UByteArray {
    val ref0 = bin.read().toFloat()
    val ref1 = bin.read().toFloat()
    val refSl = FloatArray(8)
    refSl[0] = ref0
    refSl[1] = ref1
    if (ref0 > ref1) {
        refSl[2] = (6.0f * ref0 + 1.0f * ref1) / 7.0f
        refSl[3] = (5.0f * ref0 + 2.0f * ref1) / 7.0f
        refSl[4] = (4.0f * ref0 + 3.0f * ref1) / 7.0f
        refSl[5] = (3.0f * ref0 + 4.0f * ref1) / 7.0f
        refSl[6] = (2.0f * ref0 + 5.0f * ref1) / 7.0f
        refSl[7] = (1.0f * ref0 + 6.0f * ref1) / 7.0f
    } else {
        refSl[2] = (4.0f * ref0 + 1.0f * ref1) / 5.0f
        refSl[3] = (3.0f * ref0 + 2.0f * ref1) / 5.0f
        refSl[4] = (2.0f * ref0 + 3.0f * ref1) / 5.0f
        refSl[5] = (1.0f * ref0 + 4.0f * ref1) / 5.0f
        refSl[6] = 0.0f
        refSl[7] = 255.0f
    }
    var indexBlock1 = UByteArray(3)
    bin.read(indexBlock1)
    indexBlock1 = getBC3Indices(indexBlock1)
    var indexBlock2 = UByteArray(3)
    bin.read(indexBlock2)
    indexBlock2 = getBC3Indices(indexBlock2)
    val bytes = UByteArray(16)
    for (i in 0..7) {
        val blockValue = indexBlock1[i]
        val c = refSl[blockValue.toInt()]
        bytes[7 - i] = c.toByte().toUByte()
    }
    for (i in 0..7) {
        val blockValue = indexBlock2[i]
        val c = refSl[blockValue.toInt()]
        bytes[15 - i] = c.toByte().toUByte()
    }
    return bytes
}

@Throws(IOException::class)
fun getBC3Indices(bufBlock: UByteArray): UByteArray {
    val bufTest = UByteArray(3)
    bufTest[0] = bufBlock[2]
    bufTest[1] = bufBlock[1]
    bufTest[2] = bufBlock[0]
    val indices = UByteArray(8)
    val reader: BitReader = Bits.readerFrom(bufTest.asByteArray())
    for (i in 0..7) {
        indices[i] = reader.read(3).toUByte()
    }
    return indices
}