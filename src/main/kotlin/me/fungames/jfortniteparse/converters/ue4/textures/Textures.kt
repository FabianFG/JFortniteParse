@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.textures

import com.github.memo33.jsquish.Squish
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.kotlinASTC.ASTCCodecImage
import me.fungames.kotlinASTC.Bitness
import me.fungames.kotlinPointers.asPointer
import java.awt.image.BufferedImage
import kotlin.math.floor
import kotlin.math.sqrt

enum class PixelFormatInfo(val blockSizeX : Int, val blockSizeY : Int, val bytesPerBlock : Int, val x360AlignX : Int, val x360AlignY: Int, val float : Boolean) {
    PF_G8          (1, 1, 1, 64, 64, false),
    PF_RGB8        (1, 1, 3, 0, 0, false),
    PF_RGBA8       (1, 1, 4, 32, 32, false),
    PF_R8G8B8A8       (1, 1, 4, 32, 32, false),
    PF_BGRA8       (1, 1, 4, 32, 32, false),
    PF_B8G8R8A8       (1, 1, 4, 32, 32, false),
    PF_DXT1        (4, 4, 8, 128, 128, false),
    PF_DXT3        (4, 4, 16, 128, 128, false),
    PF_DXT5        (4, 4, 16, 128, 128, false),
    PF_DXT5N       (4, 4, 16, 128, 128, false),
    PF_V8U8        (1, 1, 2, 64, 32, false),
    PF_V8U8_2      (1, 1, 2, 64, 32, false),
    PF_BC5         (4, 4, 16, 0, 0, false),
    PF_RGBA4       (1, 1, 2, 0, 0, false),
    PF_ASTC_4x4    (4, 4, 16, 0, 0, false),
    PF_ASTC_6x6    (6, 6, 16, 0, 0, false),
    PF_ASTC_8x8    (8, 8, 16, 0, 0, false),
    PF_ASTC_10x10  (10, 10, 16, 0, 0, false),
    PF_ASTC_12x12  (12, 12, 16, 0, 0, false),

}

private fun rgbaBufferToImage(rgba: ByteArray, width: Int, height: Int): BufferedImage {
    val img = BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR)
    img.raster.setDataElements(0, 0, width, height, rgba)
    return img
}

private fun rgbBufferToImage(rgb: ByteArray, width: Int, height: Int): BufferedImage {
    val img = BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR)
    img.raster.setDataElements(0, 0, width, height, rgb)
    return img
}

@Throws(IllegalArgumentException::class)
@Synchronized
fun UTexture.toBufferedImage(): BufferedImage {
    if (this !is UTexture2D) throw ParserException("Can only convert UTexture2D so far")
    val texture = getFirstTexture()
    val mip = getFirstMip()
    val data = mip.data.data
    val width = mip.sizeX
    val height = mip.sizeY
    val format = try {
        PixelFormatInfo.valueOf(texture.pixelFormat)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Unknown pixel format: ${texture.pixelFormat}")
    }

    val pixelSize = if (format.float) 16 else 4
    val size = width * height * pixelSize
    val dst = ByteArray(size)

    when (format) {
        PixelFormatInfo.PF_RGB8 -> {
            var s = data.asPointer()
            var d = dst.asPointer()
            for (i in 0 until width * height) {
                // BGRA -> RGBA
                d[0] = s[2]
                d[1] = s[1]
                d[2] = s[0]
                d[3] = 255.toByte()
                d += 4
                s += 3
            }
        }
        PixelFormatInfo.PF_RGBA8, PixelFormatInfo.PF_R8G8B8A8 -> {
            data.copyInto(dst, 0, 0, width * height * 4)
        }
        PixelFormatInfo.PF_BGRA8, PixelFormatInfo.PF_B8G8R8A8 -> {
            var s = data.asPointer()
            var d = dst.asPointer()
            for (i in 0 until width * height) {
                // BGRA -> RGBA
                d[0] = s[2]
                d[1] = s[1]
                d[2] = s[0]
                d[3] = s[3]
                d += 4
                s += 4
            }
        }
        PixelFormatInfo.PF_RGBA4 -> {
            var s = data.asPointer()
            var d = dst.asPointer()
            for (i in 0 until width * height) {
                val b1 = s[0].toInt()
                val b2 = s[1].toInt()
                // BGRA -> RGBA
                d[0] = (b2 and 0xF0).toByte()
                d[1] = ((b2 and 0xF) shl 4).toByte()
                d[2] = (b1 and 0xF0).toByte()
                d[3] = ((b1 and 0xF) shl 4).toByte()
                d += 4
                s += 2
            }
        }
        PixelFormatInfo.PF_G8 -> {
            var s = data.asPointer()
            var d = dst.asPointer()
            for (i in 0 until width * height) {
                val b = s[0]
                d[0] = b
                d[1] = b
                d[2] = b
                d[3] = 255.toByte()
                s += 1
                d += 4
            }
        }
        PixelFormatInfo.PF_V8U8, PixelFormatInfo.PF_V8U8_2 -> {
            var s = data.asPointer()
            var d = dst.asPointer()
            val offset = if (format == PixelFormatInfo.PF_V8U8) 128.toByte() else 0
            for (i in 0 until width * height) {
                val u = (s[0] + offset).toByte()
                val v = (s[1] + offset).toByte()
                d[0] = u
                d[1] = v
                val uf = (u - offset) / 255.0f * 2 - 1
                val vf = (v - offset) / 255.0f * 2 - 1
                val t = 1.0f - uf * uf - vf * vf
                if (t >= 0)
                    d[2] = (255 - 255 * floor(sqrt(t))).toByte()
                else
                    d[2] = 255.toByte()
                d[3] = 255.toByte()
                s += 2
                d += 4
            }
        }
        PixelFormatInfo.PF_ASTC_4x4, PixelFormatInfo.PF_ASTC_6x6, PixelFormatInfo.PF_ASTC_8x8, PixelFormatInfo.PF_ASTC_10x10, PixelFormatInfo.PF_ASTC_12x12 -> {
            val img = ASTCCodecImage(Bitness.BITNESS_8, width, height, 1, 0, format.blockSizeX, format.blockSizeY)
            img.initializeImage()
            img.decode(data)
            img.toBuffer().copyInto(dst, 0, 0, width * height * 4)
        }
        //All DXT formats
        PixelFormatInfo.PF_DXT1, PixelFormatInfo.PF_DXT3, PixelFormatInfo.PF_DXT5, PixelFormatInfo.PF_DXT5N -> {
            val type: Squish.CompressionType = if (format == PixelFormatInfo.PF_DXT5 || format == PixelFormatInfo.PF_DXT5N)
                Squish.CompressionType.DXT5
            else if (format == PixelFormatInfo.PF_DXT3)
                Squish.CompressionType.DXT3
            else
                Squish.CompressionType.DXT1
            val decompressed = Squish.decompressImage(null, width, height, data, type)
            decompressed.copyInto(dst, 0, 0, width * height * 4)
        }
        PixelFormatInfo.PF_BC5 -> {
            val rgb = readBC5(data, width, height)
            return rgbBufferToImage(rgb, width, height)
        }
    }

    return rgbaBufferToImage(dst, width, height)
}
