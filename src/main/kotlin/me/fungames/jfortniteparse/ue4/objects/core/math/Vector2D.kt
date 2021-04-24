package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * A vector in 2-D space composed of components (X, Y) with floating point precision.
 */
class FVector2D {
    /** Vector's X component. */
    var x: Float

    /** Vector's Y component. */
    var y: Float

    constructor(Ar: FArchive) {
        x = Ar.readFloat32()
        y = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
    }

    /**
     * Constructor which initializes all components to zero.
     */
    constructor() : this(0f, 0f)

    /**
     * Constructor using initial values for each component.
     *
     * @param x X coordinate.
     * @param y Y coordinate.
     */
    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }

    /**
     * Get a textual representation of the vector.
     *
     * @return Text describing the vector.
     */
    override fun toString() = "X=%3.3f Y=%3.3f".format(x, y)
}