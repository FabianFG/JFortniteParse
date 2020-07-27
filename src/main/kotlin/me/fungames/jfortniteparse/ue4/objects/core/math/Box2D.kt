package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FBox2D : UClass {
    var min: FVector2D
    var max: FVector2D
    var isValid: Boolean

    constructor(Ar: FArchive) {
        super.init(Ar)
        min = FVector2D(Ar)
        max = FVector2D(Ar)
        isValid = Ar.readFlag()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        min.serialize(Ar)
        max.serialize(Ar)
        Ar.writeFlag(isValid)
        super.completeWrite(Ar)
    }

    constructor(inMin: FVector2D, inMax: FVector2D) {
        min = inMin
        max = inMax
        isValid = true
    }

    /*constructor(points: Array<FVector2D>) {
        min = FVector2D(0f, 0f)
        max = FVector2D(0f, 0f)
        isValid = false
        points.forEach { this += it }
    }

    constructor(box: FBox2D) {
        this.min = box.min
        this.max = box.max
        this.isValid = box.isValid
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FBox2D

        return min == other.min && max == other.max
    }

    override fun hashCode(): Int {
        var result = min.hashCode()
        result = 31 * result + max.hashCode()
        return result
    }

    operator fun plusAssign(other: FVector2D) {
        if (isValid) {
            min.x = min(min.x, other.x)
            min.y = min(min.y, other.y)

            max.x = max(max.x, other.x)
            max.y = max(max.y, other.y)
        } else {
            min = other
            max = other
            isValid = true
        }
    }

    operator fun plus(other: FVector2D): FBox2D {
        val box = FBox2D(this)
        box += other
        return box
    }

    operator fun plusAssign(other: FBox2D) {
        if (isValid && other.isValid) {
            min.x = min(min.x, other.min.x)
            min.y = min(min.y, other.min.y)

            max.x = max(max.x, other.max.x)
            max.y = max(max.y, other.max.y)
        } else if (other.isValid) {
            min = other.min
            max = other.max
            isValid = other.isValid
        }
    }

    operator fun plus(other: FBox2D): FBox2D {
        val box = FBox2D(this)
        box += other
        return box
    }

    operator fun get(index: Int) = when (index) {
        0 -> min
        1 -> max
        else -> throw IndexOutOfBoundsException()
    }

    fun computeSquaredDistanceToPoint(point: FVector2D) = FVector2D.computeSquaredDistanceFromBoxToPoint(min, max, point)

    fun expandBy(w: Float) = FBox2D(min - FVector2D(w, w, w), max + FVector2D(w, w, w))

    fun expandBy(v: FVector2D) = FBox2D(min - v, max + v)

    fun expandBy(neg: FVector2D, pos: FVector2D) = FBox2D(min - neg, max + pos)

    fun shiftBy(offset: FVector2D) = FBox2D(min + offset, max + offset)

    fun moveTo(destination: FVector2D): FBox2D {
        val offset = destination - getCenter()
        return FBox2D(min + offset, max + offset)
    }

    fun getCenter() = (min + max) * 0.5f

    // fun getCenterAndExtents(center: FVector2D&, extents: FVector2D&)

    fun getClosestPointTo(point: FVector2D): FVector2D {
        // start by considering the point inside the box
        val closestPoint: FVector2D = point

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

        return closestPoint
    }

    fun getExtent() = (max - min) * 0.5f

    fun getSize() = max - min

    fun getVolume() = (max.x - min.x) * (max.y - min.y) * (max.z - min.z)

    fun intersect(other: FBox2D): Boolean {
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

    fun intersectXY(other: FBox2D): Boolean {
        if ((min.x > other.max.x) || (other.min.x > max.x)) {
            return false;
        }

        if ((min.y > other.max.y) || (other.min.y > max.y)) {
            return false;
        }

        return true;
    }

    fun overlap(other: FBox2D): FBox2D {
        if (!intersect(other)) {
            return FBox2D(FVector2D(0f, 0f, 0f), FVector2D(0f, 0f, 0f))
        }

        // otherwise they overlap
        // so find overlapping box
        val minVector = FVector2D()
        val maxVector = FVector2D()

        minVector.x = max(min.x, other.min.x)
        maxVector.x = min(max.x, other.max.x)

        minVector.y = max(min.y, other.min.y)
        maxVector.y = min(max.y, other.max.y)

        minVector.z = max(min.z, other.min.z)
        maxVector.z = min(max.z, other.max.z)

        return FBox2D(minVector, maxVector)
    }

    // fun inverseTransformBy(m: FTransform): FBox

    fun isInside(`in`: FVector2D) = (`in`.x > min.x) && (`in`.x < max.x) && (`in`.y > min.y) && (`in`.y < max.y) && (`in`.z > min.z) && (`in`.z < max.z)

    fun isInsideOrOn(`in`: FVector2D) = (`in`.x >= min.x) && (`in`.x <= max.x) && (`in`.y >= min.y) && (`in`.y <= max.y) && (`in`.z >= min.z) && (`in`.z <= max.z)

    fun isInside(other: FBox2D) = isInside(other.min) && isInside(other.max)

    fun isInsideXY(`in`: FVector2D) = (`in`.x > min.x) && (`in`.x < max.x) && (`in`.y > min.y) && (`in`.y < max.y)

    fun isInsideXY(other: FBox2D) = isInsideXY(other.min) && isInsideXY(other.max)

    // fun transformBy(m: FMatrix): FBox

    // fun transformBy(m: FTransform): FBox

    // fun transformProjectBy(projM: FMatrix): FBox

    override fun toString() = "IsValid=%s, Min=(%s), Max=(%s)".format(isValid.toString(), min.toString(), max.toString())

    fun buildAABB(origin: FVector2D, extent: FVector2D) = FBox2D(origin - extent, origin + extent)*/
}