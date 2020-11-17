package me.fungames.jfortniteparse.ue4.objects.core.misc

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import me.fungames.jfortniteparse.util.parseHexBinary

/**
 * Enumerates known GUID formats.
 */
enum class EGuidFormats {
    /**
     * 32 digits.
     *
     * For example: "00000000000000000000000000000000"
     */
    Digits,

    /**
     * 32 digits separated by hyphens.
     *
     * For example: 00000000-0000-0000-0000-000000000000
     */
    DigitsWithHyphens,

    /**
     * 32 digits separated by hyphens and enclosed in braces.
     *
     * For example: {00000000-0000-0000-0000-000000000000}
     */
    DigitsWithHyphensInBraces,

    /**
     * 32 digits separated by hyphens and enclosed in parentheses.
     *
     * For example: (00000000-0000-0000-0000-000000000000)
     */
    DigitsWithHyphensInParentheses,

    /**
     * Comma-separated hexadecimal values enclosed in braces.
     *
     * For example: {0x00000000,0x0000,0x0000,{0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00}}
     */
    HexValuesInBraces,

    /**
     * This format is currently used by the FUniqueObjectGuid class.
     *
     * For example: 00000000-00000000-00000000-00000000
     */
    UniqueObjectGuid,

    /**
     * Base64 characters with dashes and underscores instead of pluses and slashes (respectively)
     *
     * For example: AQsMCQ0PAAUKCgQEBAgADQ
     */
    Short,

    /**
     * Base-36 encoded, compatible with case-insensitive OS file systems (such as Windows).
     *
     * For example: 1DPF6ARFCM4XH5RMWPU8TGR0J
     */
    Base36Encoded,
}

class FGuid : UClass {
    companion object {
        @JvmStatic
        val mainGuid = FGuid()
    }

    /** Holds the first component. */
    var a: UInt

    /** Holds the second component. */
    var b: UInt

    /** Holds the third component. */
    var c: UInt

    /** Holds the fourth component. */
    var d: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        a = Ar.readUInt32()
        b = Ar.readUInt32()
        c = Ar.readUInt32()
        d = Ar.readUInt32()
        super.complete(Ar)
    }

    /** Default constructor. */
    constructor() : this(0u, 0u, 0u, 0u)

    /**
     * Creates and initializes a new GUID from the specified components.
     *
     * @param a The first component.
     * @param b The second component.
     * @param c The third component.
     * @param d The fourth component.
     */
    constructor(a: UInt, b: UInt, c: UInt, d: UInt) {
        this.a = a
        this.b = b
        this.c = c
        this.d = d
    }

    constructor(hexString: String) {
        val ar = FByteArchive(hexString.parseHexBinary())
        a = ar.readUInt32()
        b = ar.readUInt32()
        c = ar.readUInt32()
        d = ar.readUInt32()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FGuid

        return ((a xor other.a) or (b xor other.b) or (c xor other.c) or (d xor other.d)) == 0u
    }

    /**
     * Provides read-only access to the GUIDs components.
     *
     * @param index The index of the component to return (0...3).
     * @return The component.
     */
    operator fun get(index: Int) = when (index) {
        0 -> a
        1 -> b
        2 -> c
        3 -> d
        else -> throw IndexOutOfBoundsException()
    }

    /**
     * Serializes a GUID from or into an archive.
     *
     * @param Ar The archive to serialize into.
     */
    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(a)
        Ar.writeUInt32(b)
        Ar.writeUInt32(c)
        Ar.writeUInt32(d)
        super.completeWrite(Ar)
    }

    /**
     * Invalidates the GUID.
     *
     * @see isValid
     */
    fun invalidate() {
        a = 0u; b = 0u; c = 0u; d = 0u
    }

    /**
     * Checks whether this GUID is valid or not.
     *
     * A GUID that has all its components set to zero is considered invalid.
     *
     * @return true if valid, false otherwise.
     * @see invalidate
     */
    fun isValid() = (a or b or c or d) != 0u

    /**
     * Converts this GUID to its string representation.
     *
     * @return The string representation.
     */
    override fun toString() = toString(EGuidFormats.Digits)

    /**
     * Converts this GUID to its string representation using the specified format.
     *
     * @param format The string format to use.
     * @return The string representation.
     */
    fun toString(format: EGuidFormats) = when (format) {
        EGuidFormats.DigitsWithHyphens ->
            "%08X-%04X-%04X-%04X-%04X%08X".format(
                a.toInt(),
                (b shr 16).toInt(),
                (b and 0xFFFFu).toInt(),
                (c shr 16).toInt(),
                (c and 0xFFFFu).toInt(),
                d.toInt())
        EGuidFormats.DigitsWithHyphensInBraces ->
            "{%08X-%04X-%04X-%04X-%04X%08X}".format(
                a.toInt(),
                (b shr 16).toInt(),
                (b and 0xFFFFu).toInt(),
                (c shr 16).toInt(),
                (c and 0xFFFFu).toInt(),
                d.toInt())
        EGuidFormats.DigitsWithHyphensInParentheses ->
            "(%08X-%04X-%04X-%04X-%04X%08X)".format(
                a.toInt(),
                (b shr 16).toInt(),
                (b and 0xFFFFu).toInt(),
                (c shr 16).toInt(),
                (c and 0xFFFFu).toInt(),
                d.toInt())
        EGuidFormats.HexValuesInBraces ->
            "{0x%08X,0x%04X,0x%04X,{0x%02X,0x%02X,0x%02X,0x%02X,0x%02X,0x%02X,0x%02X,0x%02X}}".format(
                a.toInt(),
                (b shr 16).toInt(),
                (b and 0xFFFFu).toInt(),
                (c shr 24).toInt(),
                ((c shr 16) and 0xFFu).toInt(),
                ((c shr 8) and 0xFFu).toInt(),
                (c and 0xFFu).toInt(),
                (d shr 24).toInt(),
                ((d shr 16) and 0xFFu).toInt(),
                ((d shr 8) and 0xFFu).toInt(),
                (d and 0xFFu).toInt())
        EGuidFormats.UniqueObjectGuid ->
            "%08X-%08X-%08X-%08X".format(a.toInt(), b.toInt(), c.toInt(), d.toInt())
        else ->
            "%08X%08X%08X%08X".format(a.toInt(), b.toInt(), c.toInt(), d.toInt())
    }

    /**
     * Calculates the hash for a GUID.
     *
     * @return The hash.
     */
    override fun hashCode(): Int {
        var result = a.hashCode()
        result = 31 * result + b.hashCode()
        result = 31 * result + c.hashCode()
        result = 31 * result + d.hashCode()
        return result
    }
}