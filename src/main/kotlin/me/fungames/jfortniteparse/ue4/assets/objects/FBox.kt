package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import kotlin.math.max
import kotlin.math.min

@ExperimentalUnsignedTypes
class FBox : UClass {
    var min: FVector
    var max: FVector
    var isValid: Boolean

    constructor(Ar: FArchive) {
        super.init(Ar)
        min = FVector(Ar)
        max = FVector(Ar)
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

    constructor(inMin: FVector, inMax: FVector) {
        min = inMin
        max = inMax
        isValid = true
    }

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

    operator fun plusAssign(other: FVector) {
        if (isValid) {
            min.x = min(min.x, other.x)
            min.y = min(min.y, other.y)
            min.z = min(min.z, other.z)

            max.x = max(max.x, other.x)
            max.y = max(max.y, other.y)
            max.z = max(max.z, other.z)
        } else {
            min = other
            max = other
            isValid = true
        }
    }

    operator fun plus(other: FVector): FBox {
        val box = FBox(this)
        box += other
        return box
    }

    operator fun plusAssign(other: FBox) {
        if (isValid && other.isValid) {
            min.x = min(min.x, other.min.x)
            min.y = min(min.y, other.min.y)
            min.z = min(min.z, other.min.z)

            max.x = max(max.x, other.max.x)
            max.y = max(max.y, other.max.y)
            max.z = max(max.z, other.max.z)
        } else if (other.isValid) {
            min = other.min
            max = other.max
            isValid = other.isValid
        }
    }

    operator fun plus(other: FBox): FBox {
        val box = FBox(this)
        box += other
        return box
    }

    operator fun get(index: Int) = when (index) {
        0 -> min
        1 -> max
        else -> throw IndexOutOfBoundsException()
    }

    fun computeSquaredDistanceToPoint(point: FVector) = FVector.computeSquaredDistanceFromBoxToPoint(min, max, point)

    fun expandBy(w: Float) = FBox(min - FVector(w, w, w), max + FVector(w, w, w))

    fun expandBy(v: FVector) = FBox(min - v, max + v)

    fun expandBy(neg: FVector, pos: FVector) = FBox(min - neg, max + pos)

    fun shiftBy(offset: FVector) = FBox(min + offset, max + offset)

    fun moveTo(destination: FVector): FBox {
        val offset = destination - getCenter()
        return FBox(min + offset, max + offset)
    }

    fun getCenter() = (min + max) * 0.5f

    // fun getCenterAndExtents(center: FVector&, extents: FVector&)

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

    fun getExtent() = (max - min) * 0.5f

    fun getSize() = max - min

    fun getVolume() = (max.x - min.x) * (max.y - min.y) * (max.z - min.z)

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

    fun intersectXY(other: FBox): Boolean {
        if ((min.x > other.max.x) || (other.min.x > max.x)) {
            return false;
        }

        if ((min.y > other.max.y) || (other.min.y > max.y)) {
            return false;
        }

        return true;
    }

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

    fun isInside(`in`: FVector) = (`in`.x > min.x) && (`in`.x < max.x) && (`in`.y > min.y) && (`in`.y < max.y) && (`in`.z > min.z) && (`in`.z < max.z)

    fun isInsideOrOn(`in`: FVector) = (`in`.x >= min.x) && (`in`.x <= max.x) && (`in`.y >= min.y) && (`in`.y <= max.y) && (`in`.z >= min.z) && (`in`.z <= max.z)

    fun isInside(other: FBox) = isInside(other.min) && isInside(other.max)

    fun isInsideXY(`in`: FVector) = (`in`.x > min.x) && (`in`.x < max.x) && (`in`.y > min.y) && (`in`.y < max.y)

    fun isInsideXY(other: FBox) = isInsideXY(other.min) && isInsideXY(other.max)

    // fun transformBy(m: FMatrix): FBox

    // fun transformBy(m: FTransform): FBox

    // fun transformProjectBy(projM: FMatrix): FBox

    override fun toString() = "IsValid=%s, Min=(%s), Max=(%s)".format(isValid.toString(), min.toString(), max.toString())

    fun buildAABB(origin: FVector, extent: FVector) = FBox(origin - extent, origin + extent)
}