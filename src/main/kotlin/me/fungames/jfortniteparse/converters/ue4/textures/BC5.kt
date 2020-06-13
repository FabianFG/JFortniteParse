package me.fungames.jfortniteparse.converters.ue4.textures

import com.tomgibara.bits.BitReader
import com.tomgibara.bits.Bits
import glm_.and
import java.io.ByteArrayInputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt


private fun getPixelLoc(width: Int, x: Int, y: Int, off: Int): Int {
    return (y * width + x) * 3 + off
}

private fun getZNormal(x: Byte, y: Byte): Byte {
    val xf = ((x and 0xFF) / 127.5 - 1).toFloat()
    val yf = ((y and 0xFF) / 127.5 - 1).toFloat()
    val zval = min(
        sqrt(max(1.0f - xf * xf - yf * yf, 0.0f).toDouble()),
        1.0
    ).toFloat()
    val f = (zval * 127.0 + 128.0).toFloat()
    return f.toByte()
}


internal fun readBC5(data : ByteArray, width : Int, height : Int) : ByteArray {
    val res = ByteArray(width * height * 3)

    val bin = ByteArrayInputStream(data)

    for (yBlock in 0 until height / 4) {
        for (xBlock in 0 until width / 4) {
            val rBytes: ByteArray = decodeBC3Block(bin)
            val gBytes: ByteArray = decodeBC3Block(bin)
            for (r in 0..15) {
                val xOff = r % 4
                val yOff = r / 4
                res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 0)] = rBytes[r]
            }
            for (g in 0..15) {
                val xOff = g % 4
                val yOff = g / 4
                res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 1)] = rBytes[g]
            }
            for (b in 0..15) {
                val xOff = b % 4
                val yOff = b / 4
                val bVal: Byte = getZNormal(rBytes[b], gBytes[b])
                res[getPixelLoc(width, xBlock * 4 + xOff, yBlock * 4 + yOff, 2)] = bVal
            }
        }
    }
    return res
}

private fun readFloat8(buff : ByteArray, littleEndian : Boolean): Float {
    val bb: ByteBuffer = ByteBuffer.allocate(4).put(buff)
    bb.position(0)
    if (littleEndian) bb.order(ByteOrder.LITTLE_ENDIAN)
    return bb.float
}

private fun Float.toLittleEndian(): ByteArray {
    val bb = ByteBuffer.allocate(4)
    bb.order(ByteOrder.LITTLE_ENDIAN)
    bb.putFloat(this)
    return bb.array()
}

@Throws(IOException::class)
private fun decodeBC3Block(bin: ByteArrayInputStream): ByteArray {
    val ref0A = ByteArray(1)
    bin.read(ref0A)
    val ref0: Float = readFloat8(ref0A, true)
    val ref1A = ByteArray(1)
    bin.read(ref1A)
    val ref1: Float = readFloat8(ref1A, true)
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
    var indexBlock1 = ByteArray(3)
    bin.read(indexBlock1)
    indexBlock1 = getBC3Indices(indexBlock1)
    var indexBlock2 = ByteArray(3)
    bin.read(indexBlock2)
    indexBlock2 = getBC3Indices(indexBlock2)
    val bytes = ByteArray(16)
    for (i in 0..7) {
        val blockValue = indexBlock1[i]
        val c = refSl[blockValue.toInt()]
        bytes[7 - i] = c.toLittleEndian()[0]
    }
    for (i in 0..7) {
        val blockValue = indexBlock2[i]
        val c = refSl[blockValue.toInt()]
        bytes[15 - i] = c.toLittleEndian()[0]
    }
    return bytes
}

@Throws(IOException::class)
fun getBC3Indices(bufBlock: ByteArray): ByteArray {
    val bufTest = ByteArray(3)
    bufTest[0] = bufBlock[2]
    bufTest[1] = bufBlock[1]
    bufTest[2] = bufBlock[0]
    val indices = ByteArray(8)
    val reader: BitReader = Bits.readerFrom(bufTest)
    for (i in 0..7) {
        indices[i] = reader.read(3).toByte()
    }
    return indices
}