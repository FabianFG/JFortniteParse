package me.fungames.jfortniteparse.ue4.objects.core.misc

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Typesafe 32-bit signed frame number. Defined in this way to prevent erroneous float->int conversions and afford type-safe operator overloading.
 */
class FFrameNumber : Comparable<FFrameNumber> {
    var value: Int

    constructor(Ar: FArchive) {
        value = Ar.readInt32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt32(value)
    }

    constructor(value: Int = 0) {
        this.value = value
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FFrameNumber

        if (value != other.value) return false

        return true
    }

    override fun hashCode() = value

    override operator fun compareTo(other: FFrameNumber) = value.compareTo(other.value)
}