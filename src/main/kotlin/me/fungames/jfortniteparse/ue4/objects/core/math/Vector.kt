package me.fungames.jfortniteparse.ue4.objects.core.math

import glm_.vec3.Vec3
import glm_.vec4.Vec4
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

@ExperimentalUnsignedTypes
open class FVector : UClass {
    var x: Float
    var y: Float
    var z: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
        super.complete(Ar)
    }

    open fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
        super.completeWrite(Ar)
    }

    constructor() : this(0f, 0f, 0f)

    constructor(f: Float) : this(f, f, f)

    constructor(x: Float, y: Float, z: Float) {
        this.x = x; this.y = y; this.z = z
    }

    constructor(v: FVector2D, z: Float) : this(v.x, v.y, z)

    // constructor(v: FVector4)

    constructor(color: FLinearColor) : this(color.r, color.g, color.b)

    // constructor(inVector: FIntVector)

    // constructor(a: FIntPoint)

    infix fun xor(v: FVector) = FVector(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    )

    infix fun or(v: FVector) = x * v.x + y * v.y + z * v.z

    operator fun plus(v: FVector) = FVector(x + v.x, y + v.y, z + v.z)

    operator fun minus(v: FVector) = FVector(x - v.x, y - v.y, z - v.z)

    operator fun minus(bias: Float) = FVector(x - bias, y - bias, z - bias)

    operator fun plus(bias: Float) = FVector(x - bias, y - bias, z - bias)

    operator fun times(scale: Float) = FVector(x * scale, y * scale, z * scale)

    operator fun div(scale: Float): FVector {
        val rScale = 1f / scale
        return FVector(x * rScale, y * rScale, z * rScale)
    }

    operator fun times(v: FVector) = FVector(x * v.x, y * v.y, z * v.z)

    operator fun div(v: FVector) = FVector(x / v.x, y / v.y, z / v.z)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FVector

        return x == other.x && y == other.y && z == other.z
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    fun equals(v: FVector, tolerance: Float) = abs(x - v.x) <= tolerance && abs(y - v.y) <= tolerance && abs(z - v.z) <= tolerance

    fun allComponentsEqual(tolerance: Float) = abs(x - y) <= tolerance && abs(x - z) <= tolerance && abs(y - z) <= tolerance

    operator fun unaryMinus() = FVector(-x, -y, -z)

    operator fun plusAssign(v: FVector) {
        x += v.x; y += v.y; z += v.z
    }

    operator fun minusAssign(v: FVector) {
        x -= v.x; y -= v.y; z -= v.z
    }

    operator fun timesAssign(v: FVector) {
        x *= v.x; y *= v.y; z *= v.z
    }

    operator fun divAssign(v: FVector) {
        x /= v.x; y /= v.y; z /= v.z
    }

    operator fun get(index: Int) = when (index) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException()
    }

    fun set(inX: Float, inY: Float, inZ: Float) {
        x = inX; y = inY; z = inZ
    }

    fun getMax() = max(max(x, y), z)

    fun getAbsMax() = max(max(abs(x), abs(y)), abs(z))

    fun getMin() = min(min(x, y), z)

    fun getAbsMin() = min(min(abs(x), abs(y)), abs(z))

    fun componentMin(other: FVector) = FVector(min(x, other.x), min(y, other.y), min(z, other.z))

    fun componentMax(other: FVector) = FVector(max(x, other.x), max(y, other.y), max(z, other.z))

    fun getAbs() = FVector(abs(x), abs(y), abs(z))

    fun size() = sqrt(x * x + y * y + z * z)

    fun sizeSquared() = x * x + y * y + z * z

    fun size2D() = sqrt(x * x + y * y)

    fun sizeSquared2D() = x * x + y * y

    fun isNearlyZero(tolerance: Float) = abs(x) <= tolerance && abs(y) <= tolerance && abs(z) <= tolerance

    fun isZero() = x == 0f && y == 0f && z == 0f // TODO port more methods

    override fun toString() = "X=%3.3f Y=%3.3f Z=%3.3f".format(x, y, z)

    companion object {
        @JvmStatic
        fun crossProduct(a: FVector, b: FVector) = a xor b

        @JvmStatic
        fun dotProduct(a: FVector, b: FVector) = a or b

        @JvmStatic
        fun computeSquaredDistanceFromBoxToPoint(mins: FVector, maxs: FVector, point: FVector): Float {
            // Accumulates the distance as we iterate axis
            var distSquared = 0f

            // Check each axis for min/max and add the distance accordingly
            // NOTE: Loop manually unrolled for > 2x speed up
            if (point.x < mins.x) {
                distSquared += (point.x - mins.x) * (point.x - mins.x)
            } else if (point.x > maxs.x) {
                distSquared += (point.x - maxs.x) * (point.x - maxs.x)
            }

            if (point.y < mins.y) {
                distSquared += (point.y - mins.y) * (point.y - mins.y)
            } else if (point.y > maxs.y) {
                distSquared += (point.y - maxs.y) * (point.y - maxs.y)
            }

            if (point.z < mins.z) {
                distSquared += (point.z - mins.z) * (point.z - mins.z)
            } else if (point.z > maxs.z) {
                distSquared += (point.z - maxs.z) * (point.z - maxs.z)
            }

            return distSquared
        }
    }

    fun toVec3() = Vec3(x, y, z)
    fun toVec4() = Vec4(x, y, z, 0)
}