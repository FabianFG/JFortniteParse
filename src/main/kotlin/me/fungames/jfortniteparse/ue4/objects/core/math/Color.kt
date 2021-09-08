package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.awt.Color
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * A linear, 32-bit/component floating point RGBA color.
 */
class FLinearColor {
    var r: Float
    var g: Float
    var b: Float
    var a: Float

    constructor(Ar: FArchive) {
        r = Ar.readFloat32()
        g = Ar.readFloat32()
        b = Ar.readFloat32()
        a = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeFloat32(r)
        Ar.writeFloat32(g)
        Ar.writeFloat32(b)
        Ar.writeFloat32(a)
    }

    fun toColor() = toFColor(true).run { Color(r.toInt(), g.toInt(), b.toInt(), a.toInt()) }

    constructor() : this(0f, 0f, 0f, 0f)

    constructor(r: Float, g: Float, b: Float, a: Float = 1.0f) {
        this.r = r; this.g = g; this.b = b; this.a = a
    }

    /** Quantizes the linear color and returns the result as a FColor.  This bypasses the SRGB conversion. */
    fun quantize() = FColor(
        (r * 255f).toInt().coerceIn(0, 255).toUByte(),
        (g * 255f).toInt().coerceIn(0, 255).toUByte(),
        (b * 255f).toInt().coerceIn(0, 255).toUByte(),
        (a * 255f).toInt().coerceIn(0, 255).toUByte()
    )

    /** Quantizes the linear color with rounding and returns the result as a FColor.  This bypasses the SRGB conversion. */
    fun quantizeRound() = FColor(
        (r * 255f).roundToInt().coerceIn(0, 255).toUByte(),
        (g * 255f).roundToInt().coerceIn(0, 255).toUByte(),
        (b * 255f).roundToInt().coerceIn(0, 255).toUByte(),
        (a * 255f).roundToInt().coerceIn(0, 255).toUByte()
    )

    /** Quantizes the linear color and returns the result as a FColor with optional sRGB conversion and quality as goal. */
    fun toFColor(srgb: Boolean): FColor {
        var floatR = r.coerceIn(0.0f, 1.0f)
        var floatG = g.coerceIn(0.0f, 1.0f)
        var floatB = b.coerceIn(0.0f, 1.0f)
        val floatA = a.coerceIn(0.0f, 1.0f)

        if (srgb) {
            floatR = if (floatR <= 0.0031308f) floatR * 12.92f else floatR.pow(1.0f / 2.4f) * 1.055f - 0.055f
            floatG = if (floatG <= 0.0031308f) floatG * 12.92f else floatG.pow(1.0f / 2.4f) * 1.055f - 0.055f
            floatB = if (floatB <= 0.0031308f) floatB * 12.92f else floatB.pow(1.0f / 2.4f) * 1.055f - 0.055f
        }

        return FColor(
            (floatR * 255.999f).toInt().toUByte(),
            (floatG * 255.999f).toInt().toUByte(),
            (floatB * 255.999f).toInt().toUByte(),
            (floatA * 255.999f).toInt().toUByte()
        )
    }

    override fun toString() = "(R=%f,G=%f,B=%f,A=%f)".format(r, g, b, a)
}

/**
 * Stores a color with 8 bits of precision per channel.
 *
 * Note: Linear color values should always be converted to gamma space before stored in an FColor, as 8 bits of precision is not enough to store linear space colors!
 * This can be done with FLinearColor.toFColor(true)
 */
class FColor {
    var r: UByte
    var g: UByte
    var b: UByte
    var a: UByte

    constructor(Ar: FArchive) {
        r = Ar.readUInt8()
        g = Ar.readUInt8()
        b = Ar.readUInt8()
        a = Ar.readUInt8()
    }

    constructor() : this(0u, 0u, 0u, 0u)

    fun toColor() = Color(r.toInt(), g.toInt(), b.toInt(), a.toInt())

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt8(r)
        Ar.writeUInt8(g)
        Ar.writeUInt8(b)
        Ar.writeUInt8(a)
    }

    constructor(r: UByte, g: UByte, b: UByte, a: UByte = 255u) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }

    /**
     * Converts this color value to a hexadecimal string.
     *
     * The format of the string is RRGGBBAA.
     *
     * @return Hexadecimal string.
     * @see fromHex
     * @see toString
     */
    fun toHex() = "%02X%02X%02X%02X".format(r.toInt(), g.toInt(), b.toInt(), a.toInt())

    /**
     * Converts this color value to a string.
     *
     * @return The string representation.
     * @see toHex
     */
    override fun toString() = "(R=%d,G=%d,B=%d,A=%d)".format(r.toInt(), g.toInt(), b.toInt(), a.toInt())

    /**
     * Gets the color in a packed int32 format packed in the order ARGB.
     */
    inline fun toPackedARGB() = (a.toInt() shl 24) or (r.toInt() shl 16) or (g.toInt() shl 8) or (b.toInt() shl 0)

    /**
     * Gets the color in a packed int32 format packed in the order ABGR.
     */
    inline fun toPackedABGR() = (a.toInt() shl 24) or (r.toInt() shl 16) or (g.toInt() shl 8) or (b.toInt() shl 0)

    /**
     * Gets the color in a packed int32 format packed in the order RGBA.
     */
    inline fun toPackedRGBA() = (r.toInt() shl 24) or (r.toInt() shl 16) or (b.toInt() shl 8) or (b.toInt() shl 0)

    /**
     * Gets the color in a packed int32 format packed in the order BGRA.
     */
    inline fun toPackedBGRA() = (b.toInt() shl 24) or (r.toInt() shl 16) or (r.toInt() shl 8) or (b.toInt() shl 0)
}