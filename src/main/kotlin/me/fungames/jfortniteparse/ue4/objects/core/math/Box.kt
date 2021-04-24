package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import kotlin.math.max
import kotlin.math.min

/**
 * Implements an axis-aligned box.
 *
 * Boxes describe an axis-aligned extent in three dimensions. They are used for many different things in the
 * Engine and in games, such as bounding volumes, collision detection and visibility calculation.
 */
class FBox {
    /** Holds the box's minimum point. */
    val min: FVector

    /** Holds the box's maximum point. */
    val max: FVector

    /** Holds a flag indicating whether this box is valid. */
    var isValid: Boolean

    constructor(Ar: FArchive) {
        min = FVector(Ar)
        max = FVector(Ar)
        isValid = Ar.readFlag()
    }

    fun serialize(Ar: FArchiveWriter) {
        min.serialize(Ar)
        max.serialize(Ar)
        Ar.writeFlag(isValid)
    }

    /**
     * Creates and initializes a new box with zero extent and marks it as invalid.
     */
    constructor() : this(FVector(0f, 0f, 0f), FVector(0f, 0f, 0f)) {
        isValid = false
    }

    /**
     * Creates and initializes a new box from the specified extents.
     *
     * @param min The box's minimum point.
     * @param max The box's maximum point.
     */
    constructor(min: FVector, max: FVector) {
        this.min = min
        this.max = max
        isValid = true
    }

    /**
     * Creates and initializes a new box from an array of points.
     *
     * @param points Array of Points to create for the bounding volume.
     */
    constructor(points: Array<FVector>) {
        min = FVector(0f, 0f, 0f)
        max = FVector(0f, 0f, 0f)
        isValid = false
        points.forEach { this += it }
    }

    constructor(box: FBox) {
        this.min = box.min
        this.max = box.max
        this.isValid = box.isValid
    }

    /**
     * Compares two boxes for equality.
     *
     * @return true if the boxes are equal, false otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FBox

        return min == other.min && max == other.max
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        return result
    }

    /**
     * Adds to this bounding box to include a given point.
     *
     * @param other the point to increase the bounding volume to.
     * @return Reference to this bounding box after resizing to include the other point.
     */
    operator fun plusAssign(other: FVector) {
        if (isValid) {
            min.x = min(min.x, other.x)
            min.y = min(min.y, other.y)
            min.z = min(min.z, other.z)

            max.x = max(max.x, other.x)
            max.y = max(max.y, other.y)
            max.z = max(max.z, other.z)
        } else {
            min.set(other)
            max.set(other)
            isValid = true
        }
    }

    /**
     * Gets the result of addition to this bounding volume.
     *
     * @param other The other point to add to this.
     * @return A new bounding volume.
     */
    operator fun plus(other: FVector): FBox {
        val box = FBox(this)
        box += other
        return box
    }

    /**
     * Adds to this bounding box to include a new bounding volume.
     *
     * @param other the bounding volume to increase the bounding volume to.
     * @return Reference to this bounding volume after resizing to include the other bounding volume.
     */
    operator fun plusAssign(other: FBox) {
        if (isValid && other.isValid) {
            min.x = min(min.x, other.min.x)
            min.y = min(min.y, other.min.y)
            min.z = min(min.z, other.min.z)

            max.x = max(max.x, other.max.x)
            max.y = max(max.y, other.max.y)
            max.z = max(max.z, other.max.z)
        } else if (other.isValid) {
            min.set(other.min)
            max.set(other.max)
            isValid = other.isValid
        }
    }

    /**
     * Gets the result of addition to this bounding volume.
     *
     * @param other The other volume to add to this.
     * @return A new bounding volume.
     */
    operator fun plus(other: FBox): FBox {
        val box = FBox(this)
        box += other
        return box
    }

    /**
     * Gets the min or max of this bounding volume.
     *
     * @param index the index into points of the bounding volume.
     * @return a point of the bounding volume.
     */
    operator fun get(index: Int) = when (index) {
        0 -> min
        1 -> max
        else -> throw IndexOutOfBoundsException()
    }

    /**
     * Calculates the distance of a point to this box.
     *
     * @param point The point.
     * @return The distance.
     */
    fun computeSquaredDistanceToPoint(point: FVector) = FVector.computeSquaredDistanceFromBoxToPoint(min, max, point)

    /**
     * Increases the box size.
     *
     * @param w The size to increase the volume by.
     * @return A new bounding box.
     */
    fun expandBy(w: Float) = FBox(min - FVector(w, w, w), max + FVector(w, w, w))

    /**
     * Increases the box size.
     *
     * @param v The size to increase the volume by.
     * @return A new bounding box.
     */
    fun expandBy(v: FVector) = FBox(min - v, max + v)

    /**
     * Increases the box size.
     *
     * @param neg The size to increase the volume by in the negative direction (positive values move the bounds outwards)
     * @param pos The size to increase the volume by in the positive direction (positive values move the bounds outwards)
     * @return A new bounding box.
     */
    fun expandBy(neg: FVector, pos: FVector) = FBox(min - neg, max + pos)

    /**
     * Shifts the bounding box position.
     *
     * @param offset The vector to shift the box by.
     * @return A new bounding box.
     */
    fun shiftBy(offset: FVector) = FBox(min + offset, max + offset)

    /**
     * Moves the center of bounding box to new destination.
     *
     * @param destination The destination point to move center of box to.
     * @return A new bounding box.
     */
    fun moveTo(destination: FVector): FBox {
        val offset = destination - getCenter()
        return FBox(min + offset, max + offset)
    }

    /**
     * Gets the center point of this box.
     *
     * @return The center point.
     * @see getCenterAndExtents
     * @see getExtent
     * @see getSize
     * @see getVolume
     */
    fun getCenter() = (min + max) * 0.5f

    /**
     * Gets the center and extents of this box.
     *
     * @param center(out) Will contain the box center point.
     * @param extents(out) Will contain the extent around the center.
     * @see getCenter
     * @see getExtent
     * @see getSize
     * @see getVolume
     */
    fun getCenterAndExtents(center: FVector, extents: FVector) {
        extents.set(getExtent())
        center.set(min + extents)
    }

    /**
     * Calculates the closest point on or inside the box to a given point in space.
     *
     * @param point The point in space.
     * @return The closest point on or inside the box.
     */
    fun getClosestPointTo(point: FVector): FVector {
        // start by considering the point inside the box
        val closestPoint: FVector = point

        // now clamp to inside box if it's outside
        if (point.x < min.x) {
            closestPoint.x = min.x
        } else if (point.x > max.x) {
            closestPoint.x = max.x
        }

        // now clamp to inside box if it's outside
        if (point.y < min.y) {
            closestPoint.y = min.y
        } else if (point.y > max.y) {
            closestPoint.y = max.y
        }

        // Now clamp to inside box if it's outside.
        if (point.z < min.z) {
            closestPoint.z = min.z
        } else if (point.z > max.z) {
            closestPoint.z = max.z
        }

        return closestPoint
    }

    /**
     * Gets the extents of this box.
     *
     * @return The box extents.
     * @see getCenter
     * @see getCenterAndExtents
     * @see getSize
     * @see getVolume
     */
    fun getExtent() = (max - min) * 0.5f

    /**
     * Gets the size of this box.
     *
     * @return The box size.
     * @see getCenter
     * @see getCenterAndExtents
     * @see getExtent
     * @see getVolume
     */
    fun getSize() = max - min

    /**
     * Gets the volume of this box.
     *
     * @return The box volume.
     * @see getCenter
     * @see getCenterAndExtents
     * @see getExtent
     * @see getSize
     */
    fun getVolume() = (max.x - min.x) * (max.y - min.y) * (max.z - min.z)

    /**
     * Checks whether the given bounding box intersects this bounding box.
     *
     * @param other The bounding box to intersect with.
     * @return true if the boxes intersect, false otherwise.
     */
    fun intersect(other: FBox): Boolean {
        if ((min.x > other.max.x) || (other.min.x > max.x)) {
            return false;
        }

        if ((min.y > other.max.y) || (other.min.y > max.y)) {
            return false;
        }

        if ((min.z > other.max.z) || (other.min.z > max.z)) {
            return false;
        }

        return true;
    }

    /**
     * Checks whether the given bounding box intersects this bounding box in the XY plane.
     *
     * @param other The bounding box to test intersection.
     * @return true if the boxes intersect in the XY Plane, false otherwise.
     */
    fun intersectXY(other: FBox): Boolean {
        if ((min.x > other.max.x) || (other.min.x > max.x)) {
            return false;
        }

        if ((min.y > other.max.y) || (other.min.y > max.y)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the overlap FBox of two box
     *
     * @param other The bounding box to test overlap
     * @return the overlap box. It can be 0 if they don't overlap
     */
    fun overlap(other: FBox): FBox {
        if (!intersect(other)) {
            return FBox(FVector(0f, 0f, 0f), FVector(0f, 0f, 0f))
        }

        // otherwise they overlap
        // so find overlapping box
        val minVector = FVector()
        val maxVector = FVector()

        minVector.x = max(min.x, other.min.x)
        maxVector.x = min(max.x, other.max.x)

        minVector.y = max(min.y, other.min.y)
        maxVector.y = min(max.y, other.max.y)

        minVector.z = max(min.z, other.min.z)
        maxVector.z = min(max.z, other.max.z)

        return FBox(minVector, maxVector)
    }

    // fun inverseTransformBy(m: FTransform): FBox

    /**
     * Checks whether the given location is inside this box.
     *
     * @param in The location to test for inside the bounding volume.
     * @return true if location is inside this volume.
     * @see isInsideXY
     */
    fun isInside(`in`: FVector) = (`in`.x > min.x) && (`in`.x < max.x) && (`in`.y > min.y) && (`in`.y < max.y) && (`in`.z > min.z) && (`in`.z < max.z)

    /**
     * Checks whether the given location is inside or on this box.
     *
     * @param in The location to test for inside the bounding volume.
     * @return true if location is inside this volume.
     * @see isInsideXY
     */
    fun isInsideOrOn(`in`: FVector) = (`in`.x >= min.x) && (`in`.x <= max.x) && (`in`.y >= min.y) && (`in`.y <= max.y) && (`in`.z >= min.z) && (`in`.z <= max.z)

    /**
     * Checks whether a given box is fully encapsulated by this box.
     *
     * @param other The box to test for encapsulation within the bounding volume.
     * @return true if box is inside this volume.
     */
    fun isInside(other: FBox) = isInside(other.min) && isInside(other.max)

    /**
     * Checks whether the given location is inside this box in the XY plane.
     *
     * @param in The location to test for inside the bounding box.
     * @return true if location is inside this box in the XY plane.
     * @see isInside
     */
    fun isInsideXY(`in`: FVector) = (`in`.x > min.x) && (`in`.x < max.x) && (`in`.y > min.y) && (`in`.y < max.y)

    /**
     * Checks whether the given box is fully encapsulated by this box in the XY plane.
     *
     * @param other The box to test for encapsulation within the bounding box.
     * @return true if box is inside this box in the XY plane.
     */
    fun isInsideXY(other: FBox) = isInsideXY(other.min) && isInsideXY(other.max)

    // fun transformBy(m: FMatrix): FBox

    // fun transformBy(m: FTransform): FBox

    // fun transformProjectBy(projM: FMatrix): FBox

    override fun toString() = "IsValid=%s, Min=(%s), Max=(%s)".format(isValid.toString(), min.toString(), max.toString())

    companion object {
        /**
         * Utility function to build an AABB from Origin and Extent
         *
         * @param origin The location of the bounding box.
         * @param extent Half size of the bounding box.
         * @return A new axis-aligned bounding box.
         */
        @JvmStatic
        fun buildAABB(origin: FVector, extent: FVector) = FBox(origin - extent, origin + extent)
    }
}