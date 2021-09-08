package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Implements a rectangular 2D Box.
 */
class FBox2D {
    /** Holds the box's minimum point. */
    var min: FVector2D

    /** Holds the box's maximum point. */
    var max: FVector2D

    /** Holds a flag indicating whether this box is valid. */
    var isValid: Boolean

    constructor(Ar: FArchive) {
        min = FVector2D(Ar)
        max = FVector2D(Ar)
        isValid = Ar.readFlag()
    }

    fun serialize(Ar: FArchiveWriter) {
        min.serialize(Ar)
        max.serialize(Ar)
        Ar.writeFlag(isValid)
    }

    /**
     * Creates and initializes a new box.
     *
     * The box extents are initialized to zero and the box is marked as invalid.
     */
    constructor() : this(FVector2D(0f, 0f), FVector2D(0f, 0f)) {
        isValid = false
    }

    /**
     * Creates and initializes a new box from the specified parameters.
     *
     * @param min The box's minimum point.
     * @param max The box's maximum point.
     */
    constructor(min: FVector2D, max: FVector2D) {
        this.min = min
        this.max = max
        isValid = true
    }

    /**
     * Get a textual representation of this box.
     *
     * @return A string describing the box.
     */
    override fun toString() = "bIsValid=%s, Min=(%s), Max=(%s)".format(isValid.toString(), min.toString(), max.toString())
}