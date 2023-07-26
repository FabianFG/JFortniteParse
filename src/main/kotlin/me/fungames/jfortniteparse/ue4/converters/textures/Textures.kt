package me.fungames.jfortniteparse.ue4.converters.textures

import com.github.memo33.jsquish.Squish
import me.fungames.jfortniteparse.ue4.assets.exports.tex.FTexture2DMipMap
import me.fungames.jfortniteparse.ue4.assets.exports.tex.FTexturePlatformData
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.converters.textures.Detex.DetexPixelFormat
import me.fungames.jfortniteparse.ue4.converters.textures.Detex.DetexTextureFormat
import me.fungames.jfortniteparse.ue4.converters.textures.PixelFormatInfo.*
import me.fungames.jfortniteparse.ue4.converters.textures.dds.DDSCaps
import me.fungames.jfortniteparse.ue4.converters.textures.dds.DDSHeader
import me.fungames.jfortniteparse.ue4.converters.textures.dds.DDSHeader10
import me.fungames.jfortniteparse.ue4.converters.textures.dds.DDSPixelFormat
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import me.fungames.kotlinASTC.ASTCCodecImage
import me.fungames.kotlinASTC.Bitness
import me.fungames.kotlinPointers.asPointer
import java.awt.image.BufferedImage
import kotlin.math.floor
import kotlin.math.sqrt

enum class PixelFormatInfo(val blockSizeX: Int, val blockSizeY: Int, val bytesPerBlock: Int, val float: Boolean) {
    PF_G8               (1, 1, 1, false),
    PF_RGB8             (1, 1, 3, false),
    PF_RGBA8            (1, 1, 4, false),
    PF_R8G8B8A8         (1, 1, 4, false),
    PF_BGRA8            (1, 1, 4, false),
    PF_B8G8R8A8         (1, 1, 4, false),
    PF_DXT1             (4, 4, 8, false),
    PF_DXT3             (4, 4, 16, false),
    PF_DXT5             (4, 4, 16, false),
    PF_DXT5N            (4, 4, 16, false),
    PF_V8U8             (1, 1, 2, false),
    PF_V8U8_2           (1, 1, 2, false),
    PF_BC5              (4, 4, 16, false),
    PF_RGBA4            (1, 1, 2, false),
    PF_ATC_RGB          (4, 4, 8, false),
    PF_ATC_RGBA_E       (4, 4, 16, false),
    PF_ATC_RGBA_I       (4, 4, 16, false),
    PF_X24_G8           (1, 1, 1, false),
    PF_ETC1             (4, 4, 8, false),
    PF_ETC2_RGB         (4, 4, 8, false),
    PF_ETC2_RGBA        (4, 4, 16, false),
    PF_R32G32B32A32_UINT(1, 1, 16, false),
    PF_R16G16_UINT      (1, 1, 4, false),
    PF_ASTC_4x4         (4, 4, 16, false),
    PF_ASTC_6x6         (6, 6, 16, false),
    PF_ASTC_8x8         (8, 8, 16, false),
    PF_ASTC_10x10       (10, 10, 16, false),
    PF_ASTC_12x12       (12, 12, 16, false),
    PF_BC6H             (4, 4, 16, true),
    PF_BC7              (4, 4, 16, false),
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

fun UTexture2D.toBufferedImage() = toBufferedImage(getFirstTexture())

@Synchronized
fun UTexture2D.toBufferedImage(texture: FTexturePlatformData = getFirstTexture(), mip: FTexture2DMipMap = texture.getFirstLoadedMip()): BufferedImage {
    val data = mip.data.data
    val width = mip.sizeX
    val height = mip.sizeY
    val format = try {
        valueOf(texture.pixelFormat)
    } catch (e: IllegalArgumentException) {
        throw IllegalArgumentException("Unknown pixel format: ${texture.pixelFormat}")
    }

    val pixelSize = if (format.float) 16 else 4
    val size = width * height * pixelSize

    when (format) {
        PF_RGB8 -> {
            val dst = ByteArray(size)
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
            return rgbaBufferToImage(dst, width, height)
        }
        PF_RGBA8, PF_R8G8B8A8 -> {
            return rgbaBufferToImage(data, width, height)
        }
        PF_BGRA8, PF_B8G8R8A8 -> {
            val dst = ByteArray(size)
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
            return rgbaBufferToImage(dst, width, height)
        }
        PF_RGBA4 -> {
            val dst = ByteArray(size)
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
            return rgbaBufferToImage(dst, width, height)
        }
        PF_G8 -> {
            val dst = ByteArray(size)
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
            return rgbaBufferToImage(dst, width, height)
        }
        PF_V8U8, PF_V8U8_2 -> {
            val dst = ByteArray(size)
            var s = data.asPointer()
            var d = dst.asPointer()
            val offset = if (format == PF_V8U8) 128.toByte() else 0
            for (i in 0 until width * height) {
                val u = (s[0] + offset).toByte()
                val v = (s[1] + offset).toByte()
                d[0] = u
                d[1] = v
                val uf = (u - offset) / 255.0f * 2 - 1
                val vf = (v - offset) / 255.0f * 2 - 1
                val t = 1.0f - uf * uf - vf * vf
                if (t >= 0)
                    d[2] = (255 - 255 * floor(sqrt(t))).toInt().toByte()
                else
                    d[2] = 255.toByte()
                d[3] = 255.toByte()
                s += 2
                d += 4
            }
            return rgbaBufferToImage(dst, width, height)
        }
        PF_ASTC_4x4, PF_ASTC_6x6, PF_ASTC_8x8, PF_ASTC_10x10, PF_ASTC_12x12 -> {
            val img = ASTCCodecImage(Bitness.BITNESS_8, width, height, 1, 0, format.blockSizeX, format.blockSizeY)
            img.initializeImage()
            img.decode(data)
            return rgbaBufferToImage(img.toBuffer(), width, height)
        }
        //All DXT formats
        PF_DXT1, PF_DXT3, PF_DXT5, PF_DXT5N -> {
            val decompressed = Squish.decompressImage(null, width, height, data, when (format) {
                PF_DXT5, PF_DXT5N -> Squish.CompressionType.DXT5
                PF_DXT3 -> Squish.CompressionType.DXT3
                else -> Squish.CompressionType.DXT1
            })
            return rgbaBufferToImage(decompressed, width, height)
        }
        PF_BC5 -> {
            val rgb = readBC5(data, width, height)
            return rgbBufferToImage(rgb, width, height)
        }
        PF_BC7 -> {
            val dst = ByteArray(size)
            if (!Detex.decompressTextureLinear(data, dst, width, height, DetexTextureFormat.BPTC, DetexPixelFormat.RGBA8)) {
                throw IllegalArgumentException("Failed to decode BC7 texture")
            }
            return rgbaBufferToImage(dst, width, height)
        }
        PF_ETC1 -> {
            val dst = ByteArray(size)
            if (!Detex.decompressTextureLinear(data, dst, width, height, DetexTextureFormat.ETC1, DetexPixelFormat.RGBA8)) {
                throw IllegalArgumentException("Failed to decode ETC1 texture")
            }
            return rgbaBufferToImage(dst, width, height)
        }
        PF_ETC2_RGB -> {
            val dst = ByteArray(size)
            if (!Detex.decompressTextureLinear(data, dst, width, height, DetexTextureFormat.ETC2, DetexPixelFormat.RGBA8)) {
                throw IllegalArgumentException("Failed to decode ETC2_RGB texture")
            }
            return rgbaBufferToImage(dst, width, height)
        }
        PF_ETC2_RGBA -> {
            val dst = ByteArray(size)
            if (!Detex.decompressTextureLinear(data, dst, width, height, DetexTextureFormat.ETC2_EAC, DetexPixelFormat.RGBA8)) {
                throw IllegalArgumentException("Failed to decode ETC2_RGBA texture")
            }
            return rgbaBufferToImage(dst, width, height)
        }
    }
    throw IllegalArgumentException("Unsupported pixel format: $format")
}

fun FTexturePlatformData.getDdsFourCC() = when (pixelFormat) {
    "PF_DXT1" -> "DXT1"
    "PF_DXT3" -> "DXT3"
    "PF_DXT5", "PF_DXT5N" -> "DXT5"
    "PF_BC4" -> "ATI1"
    "PF_BC5" -> "ATI2"
    else -> null
}?.toCharArray()

fun UTexture2D.toDdsArray(texture: FTexturePlatformData = getFirstTexture(), mip: FTexture2DMipMap = texture.getFirstLoadedMip()): ByteArray {
    val fourCC = texture.getDdsFourCC()
        ?: throw IllegalArgumentException("Pixel format ${texture.pixelFormat} cannot be exported to DDS")
    val header = DDSHeader()
    header.setFourCC(fourCC[0], fourCC[1], fourCC[2], fourCC[3])
    header.setWidth(mip.sizeX)
    header.setHeight(mip.sizeY)
    header.setLinearSize(mip.data.data.size)
    val Ar = FByteArchiveWriter()
    header.serialize(Ar)
    Ar.write(mip.data.data)
    return Ar.toByteArray()
}

private fun DDSPixelFormat.serialize(Ar: FArchiveWriter) {
    Ar.writeInt32(size)
    Ar.writeInt32(flags)
    Ar.writeInt32(fourcc)
    Ar.writeInt32(bitcount)
    Ar.writeInt32(rmask)
    Ar.writeInt32(gmask)
    Ar.writeInt32(bmask)
    Ar.writeInt32(amask)
}

private fun DDSCaps.serialize(Ar: FArchiveWriter) {
    Ar.writeInt32(caps1)
    Ar.writeInt32(caps2)
    Ar.writeInt32(caps3)
    Ar.writeInt32(caps4)
}

private fun DDSHeader10.serialize(Ar: FArchiveWriter) {
    Ar.writeInt32(dxgiFormat)
    Ar.writeInt32(resourceDimension)
    Ar.writeInt32(miscFlag)
    Ar.writeInt32(arraySize)
    Ar.writeInt32(reserved)
}

private fun DDSHeader.serialize(Ar: FArchiveWriter) {
    Ar.writeInt32(fourcc)
    Ar.writeInt32(size)
    Ar.writeInt32(flags)
    Ar.writeInt32(height)
    Ar.writeInt32(width)
    Ar.writeInt32(pitch)
    Ar.writeInt32(depth)
    Ar.writeInt32(mipmapcount)
    reserved.forEach { Ar.writeInt32(it) }
    pf.serialize(Ar)
    caps.serialize(Ar)
    Ar.writeInt32(notused)

    if (hasDX10Header()) {
        header10.serialize(Ar)
    }
}