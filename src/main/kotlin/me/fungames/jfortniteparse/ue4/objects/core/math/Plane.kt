package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive

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
        w = Ar.readFloat32()
    }

    /**
     * Calculates distance between plane and a point.
     *
     * @param p The other point.
     * @return The distance from the plane to the point. 0: Point is on the plane. >0: Point is in front of the plane. <0: Point is behind the plane.
     */
    inline fun planeDot(p: FVector) = x * p.x + y * p.y + z * p.z - w
}