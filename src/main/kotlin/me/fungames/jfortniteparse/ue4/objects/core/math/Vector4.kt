package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * A 4D homogeneous vector, 4x1 FLOATs.
 */
class FVector4 {
    /** The vector's X-component. */
    var x: Float

    /** The vector's Y-component. */
    var y: Float

    /** The vector's Z-component. */
    var z: Float

    /** The vector's W-component. */
    var w: Float

    constructor(Ar: FArchive) {
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
        w = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
        Ar.writeFloat32(w)
    }

    /**
     * Constructor.
     *
     * @param vector 3D Vector to set first three components.
     * @param w W Coordinate.
     */
    constructor(vector: FVector, w: Float = 1.0f) : this(vector.x, vector.y, vector.z, w)

    /**
     * Creates and initializes a new vector from a color value.
     *
     * @param color Color used to set vector.
     */
    constructor(color: FLinearColor) : this(color.r, color.g, color.b, color.a)

    /**
     * Creates and initializes a new vector from the specified components.
     *
     * @param x X Coordinate.
     * @param y Y Coordinate.
     * @param z Z Coordinate.
     * @param w W Coordinate.
     */
    constructor(x: Float = 0.0f, y: Float = 0.0f, z: Float = 0.0f, w: Float = 0.0f) {
        this.x = x; this.y = y; this.z = z; this.w = w
    }

    /**
     * Creates and initializes a new vector from the specified 2D vectors.
     *
     * @param xy A 2D vector holding the X- and Y-components.
     * @param zw A 2D vector holding the Z- and W-components.
     */
    constructor(xy: FVector2D, zw: FVector2D) : this(xy.x, xy.y, zw.x, zw.y)

    //constructor(vector: FIntVector4)

    /**
     * Get a textual representation of the vector.
     *
     * @return Text describing the vector.
     */
    override fun toString() = "X=%3.3f Y=%3.3f Z=%3.3f W=%3.3f".format(x, y, z, w)
}