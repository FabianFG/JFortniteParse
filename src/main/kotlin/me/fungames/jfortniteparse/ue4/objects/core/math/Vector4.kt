package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * A 4D homogeneous vector, 4x1 FLOATs.
 */
@ExperimentalUnsignedTypes
class FVector4 : UClass {
    /** The vector's X-component. */
    var x: Float

    /** The vector's Y-component. */
    var y: Float

    /** The vector's Z-component. */
    var z: Float

    /** The vector's W-component. */
    var w: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
        w = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
        Ar.writeFloat32(w)
        super.completeWrite(Ar)
    }

    /**
     * Constructor.
     *
     * @param vector 3D Vector to set first three components.
     * @param w W Coordinate.
     */
    constructor(vector: FVector, w: Float = 1f) : this(vector.x, vector.y, vector.z, w)

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
    constructor(x: Float = 0f, y: Float = 0f, z: Float = 0f, w: Float = 0f) {
        this.x = x; this.y = y; this.z = z; this.w = w
    }

    /**
     * Get a textual representation of the vector.
     *
     * @return Text describing the vector.
     */
    override fun toString() = "X=%3.3f Y=%3.3f Z=%3.3f W=%3.3f".format(x, y, z, w)
}