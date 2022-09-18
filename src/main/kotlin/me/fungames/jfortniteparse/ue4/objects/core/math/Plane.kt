package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.EUnrealEngineObjectUE5Version
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Structure for three dimensional planes.
 *
 * Stores the coeffecients as Xx+Yy+Zz=W.
 * Note that this is different from many other Plane classes that use Xx+Yy+Zz+W=0.
 */
@Suppress("NOTHING_TO_INLINE")
class FPlane : FVector {
    /** The w-component. */
    var w: Float

    constructor(Ar: FArchive) : super(Ar) {
        w = if (Ar.ver >= EUnrealEngineObjectUE5Version.LARGE_WORLD_COORDINATES) Ar.readDouble().toFloat() else Ar.readFloat32()
    }

    override fun serialize(Ar: FArchiveWriter) {
        super.serialize(Ar)
        Ar.writeFloat32(w)
    }

    /** Default constructor. */
    constructor() : this(0f, 0f, 0f, 0f)

    /**
     * Constructor.
     *
     * @param v 4D vector to set up plane.
     */
    constructor(v: FVector4) : super(v) {
        w = v.w
    }

    /**
     * Constructor.
     *
     * @param x X-coefficient.
     * @param y Y-coefficient.
     * @param z Z-coefficient.
     * @param w W-coefficient.
     */
    constructor(x: Float, y: Float, z: Float, w: Float) : super(x, y, z) {
        this.w = w
    }

    /**
     * Calculates distance between plane and a point.
     *
     * @param p The other point.
     * @return The distance from the plane to the point. 0: Point is on the plane. >0: Point is in front of the plane. <0: Point is behind the plane.
     */
    inline fun planeDot(p: FVector) = x * p.x + y * p.y + z * p.z - w
}