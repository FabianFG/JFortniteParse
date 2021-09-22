package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Enumerates the valid types of range bounds.
 */
enum class ERangeBoundTypes {
    /** The range excludes the bound. */
    Exclusive,

    /** The range includes the bound. */
    Inclusive,

    /** The bound is open. */
    Open
}

/**
 * Template for range bounds.
 */
class TRangeBound<ElementType> {
    /** Holds the type of the bound. */
    var type: ERangeBoundTypes

    /** Holds the bound's value. */
    var value: ElementType

    constructor(Ar: FArchive, init: () -> ElementType) {
        type = ERangeBoundTypes.values()[Ar.readInt8().toInt()]
        value = init()
    }

    constructor(boundType: ERangeBoundTypes, value: ElementType) {
        this.type = boundType
        this.value = value
    }

    fun serialize(Ar: FArchiveWriter, write: (ElementType) -> Unit) {
        Ar.writeInt8(type.ordinal.toByte())
        write(value)
    }
}