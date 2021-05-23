package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import me.fungames.jfortniteparse.util.KINDA_SMALL_NUMBER
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/** Allowed error for a normalized vector (against squared magnitude) */
const val THRESH_VECTOR_NORMALIZED = 0.01f

/**
 * A vector in 3-D space composed of components (X, Y, Z) with floating point precision.
 */
@Suppress("NOTHING_TO_INLINE")
open class FVector {
    /** Vector's X component. */
    var x: Float

    /** Vector's Y component. */
    var y: Float

    /** Vector's Z component. */
    var z: Float

    constructor(Ar: FArchive) {
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
    }

    open fun serialize(Ar: FArchiveWriter) {
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
    }

    /** Default constructor (no initialization). */
    constructor() : this(0f, 0f, 0f)

    /**
     * Constructor initializing all components to a single float value.
     *
     * @param f Value to set all components to.
     */
    constructor(f: Float) : this(f, f, f)

    /**
     * Constructor using initial values for each component.
     *
     * @param x X Coordinate.
     * @param y Y Coordinate.
     * @param z Z Coordinate.
     */
    constructor(x: Float, y: Float, z: Float) {
        this.x = x; this.y = y; this.z = z
    }

    /**
     * Constructs a vector from an FVector2D and Z value.
     *
     * @param v Vector to copy from.
     * @param z Z Coordinate.
     */
    constructor(v: FVector2D, z: Float) : this(v.x, v.y, z)

    /**
     * Constructor using the XYZ components from a 4D vector.
     *
     * @param v 4D Vector to copy from.
     */
    constructor(v: FVector4) : this(v.x, v.y, v.z)

    /**
     * Constructs a vector from an FLinearColor.
     *
     * @param color Color to copy from.
     */
    constructor(color: FLinearColor) : this(color.r, color.g, color.b)

    /**
     * Constructs a vector from an FIntVector.
     *
     * @param vector FIntVector to copy from.
     */
    constructor(vector: FIntVector) : this(vector.x.toFloat(), vector.y.toFloat(), vector.z.toFloat())

    /**
     * Constructs a vector from an FIntPoint.
     *
     * @param a Int Point used to set X and Y coordinates, Z is set to zero.
     */
    constructor(a: FIntPoint) : this(a.x.toFloat(), a.y.toFloat(), 0f)

    /**
     * Copy another FVector into this one
     *
     * @param other The other vector.
     * @return Reference to vector after copy.
     */
    inline fun set(other: FVector): FVector {
        x = other.x; y = other.y; z = other.z
        return this
    }

    /**
     * Calculate cross product between this and another vector.
     *
     * @param v The other vector.
     * @return The cross product.
     */
    inline infix fun xor(v: FVector) = FVector(
        y * v.z - z * v.y,
        z * v.x - x * v.z,
        x * v.y - y * v.x
    )

    /**
     * Calculate the dot product between this and another vector.
     *
     * @param v The other vector.
     * @return The dot product.
     */
    inline infix fun or(v: FVector) = x * v.x + y * v.y + z * v.z

    /**
     * Gets the result of component-wise addition of this and another vector.
     *
     * @param v The vector to add to this.
     * @return The result of vector addition.
     */
    inline operator fun plus(v: FVector) = FVector(x + v.x, y + v.y, z + v.z)

    /**
     * Gets the result of component-wise subtraction of this by another vector.
     *
     * @param v The vector to subtract from this.
     * @return The result of vector subtraction.
     */
    inline operator fun minus(v: FVector) = FVector(x - v.x, y - v.y, z - v.z)

    /**
     * Gets the result of subtracting from each component of the vector.
     *
     * @param bias How much to subtract from each component.
     * @return The result of subtraction.
     */
    inline operator fun minus(bias: Float) = FVector(x - bias, y - bias, z - bias)

    /**
     * Gets the result of adding to each component of the vector.
     *
     * @param bias How much to add to each component.
     * @return The result of addition.
     */
    inline operator fun plus(bias: Float) = FVector(x + bias, y + bias, z + bias)

    /**
     * Gets the result of scaling the vector (multiplying each component by a value).
     *
     * @param scale What to multiply each component by.
     * @return The result of multiplication.
     */
    inline operator fun times(scale: Float) = FVector(x * scale, y * scale, z * scale)

    /**
     * Gets the result of dividing each component of the vector by a value.
     *
     * @param scale What to divide each component by.
     * @return The result of division.
     */
    inline operator fun div(scale: Float): FVector {
        val rScale = 1f / scale
        return FVector(x * rScale, y * rScale, z * rScale)
    }

    /**
     * Gets the result of component-wise multiplication of this vector by another.
     *
     * @param v The vector to multiply with.
     * @return The result of multiplication.
     */
    inline operator fun times(v: FVector) = FVector(x * v.x, y * v.y, z * v.z)

    /**
     * Gets the result of component-wise division of this vector by another.
     *
     * @param v The vector to divide by.
     * @return The result of division.
     */
    inline operator fun div(v: FVector) = FVector(x / v.x, y / v.y, z / v.z)

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

    /**
     * Check against another vector for equality, within specified error limits.
     *
     * @param v The vector to check against.
     * @param tolerance Error tolerance.
     * @return true if the vectors are equal within tolerance limits, false otherwise.
     */
    inline fun equals(v: FVector, tolerance: Float = KINDA_SMALL_NUMBER) = abs(x - v.x) <= tolerance && abs(y - v.y) <= tolerance && abs(z - v.z) <= tolerance

    /**
     * Checks whether all components of this vector are the same, within a tolerance.
     *
     * @param tolerance Error tolerance.
     * @return true if the vectors are equal within tolerance limits, false otherwise.
     */
    inline fun allComponentsEqual(tolerance: Float = KINDA_SMALL_NUMBER) = abs(x - y) <= tolerance && abs(x - z) <= tolerance && abs(y - z) <= tolerance

    /**
     * Get a negated copy of the vector.
     *
     * @return A negated copy of the vector.
     */
    inline operator fun unaryMinus() = FVector(-x, -y, -z)

    /**
     * Adds another vector to this.
     * Uses component-wise addition.
     *
     * @param v Vector to add to this.
     */
    inline operator fun plusAssign(v: FVector) {
        x += v.x; y += v.y; z += v.z
    }

    /**
     * Subtracts another vector from this.
     * Uses component-wise subtraction.
     *
     * @param v Vector to subtract from this.
     */
    inline operator fun minusAssign(v: FVector) {
        x -= v.x; y -= v.y; z -= v.z
    }

    /**
     * Scales the vector.
     *
     * @param scale Amount to scale this vector by.
     */
    inline operator fun timesAssign(scale: Float) {
        x *= scale; y *= scale; z *= scale
    }

    /**
     * Divides the vector by a number.
     *
     * @param V What to divide this vector by.
     */
    inline operator fun divAssign(v: FVector) {
        x /= v.x; y /= v.y; z /= v.z
    }

    /**
     * Multiplies the vector with another vector, using component-wise multiplication.
     *
     * @param v What to multiply this vector with.
     */
    inline operator fun timesAssign(v: FVector) {
        x *= v.x; y *= v.y; z *= v.z
    }

    /**
     * Gets specific component of the vector.
     *
     * @param index the index of vector component
     * @return Copy of the component.
     */
    operator fun get(index: Int) = when (index) {
        0 -> x
        1 -> y
        2 -> z
        else -> throw IndexOutOfBoundsException()
    }

    /**
     * Sets specific component of the vector.
     *
     * @param index the index of vector component
     * @param value the new value of vector component
     */
    operator fun set(index: Int, value: Float) = when (index) {
        0 -> x = value
        1 -> y = value
        2 -> z = value
        else -> throw IndexOutOfBoundsException()
    }

    /**
     * Set the values of the vector directly.
     *
     * @param x New X coordinate.
     * @param y New Y coordinate.
     * @param z New Z coordinate.
     */
    inline fun set(x: Float, y: Float, z: Float) {
        this.x = x; this.y = y; this.z = z
    }

    /**
     * Get the maximum value of the vector's components.
     *
     * @return The maximum value of the vector's components.
     */
    inline fun getMax() = max(max(x, y), z)

    /**
     * Get the maximum absolute value of the vector's components.
     *
     * @return The maximum absolute value of the vector's components.
     */
    inline fun getAbsMax() = max(max(abs(x), abs(y)), abs(z))

    /**
     * Get the minimum value of the vector's components.
     *
     * @return The minimum value of the vector's components.
     */
    inline fun getMin() = min(min(x, y), z)

    /**
     * Get the minimum absolute value of the vector's components.
     *
     * @return The minimum absolute value of the vector's components.
     */
    inline fun getAbsMin() = min(min(abs(x), abs(y)), abs(z))

    /** Gets the component-wise min of two vectors. */
    inline fun componentMin(other: FVector) = FVector(min(x, other.x), min(y, other.y), min(z, other.z))

    /** Gets the component-wise max of two vectors. */
    inline fun componentMax(other: FVector) = FVector(max(x, other.x), max(y, other.y), max(z, other.z))

    /**
     * Get a copy of this vector with absolute value of each component.
     *
     * @return A copy of this vector with absolute value of each component.
     */
    inline fun getAbs() = FVector(abs(x), abs(y), abs(z))

    /**
     * Get the length (magnitude) of this vector.
     *
     * @return The length of this vector.
     */
    inline fun size() = sqrt(x * x + y * y + z * z)

    /**
     * Get the squared length of this vector.
     *
     * @return The squared length of this vector.
     */
    inline fun sizeSquared() = x * x + y * y + z * z

    /**
     * Get the length of the 2D components of this vector.
     *
     * @return The 2D length of this vector.
     */
    inline fun size2D() = sqrt(x * x + y * y)

    /**
     * Get the squared length of the 2D components of this vector.
     *
     * @return The squared 2D length of this vector.
     */
    inline fun sizeSquared2D() = x * x + y * y

    /**
     * Checks whether vector is near to zero within a specified tolerance.
     *
     * @param tolerance Error tolerance.
     * @return true if the vector is near to zero, false otherwise.
     */
    inline fun isNearlyZero(tolerance: Float = KINDA_SMALL_NUMBER) = abs(x) <= tolerance && abs(y) <= tolerance && abs(z) <= tolerance

    /**
     * Checks whether all components of the vector are exactly zero.
     *
     * @return true if the vector is exactly zero, false otherwise.
     */
    inline fun isZero() = x == 0f && y == 0f && z == 0f

    /**
     * Check if the vector is of unit length, with specified tolerance.
     *
     * @param lengthSquaredTolerance Tolerance against squared length.
     * @return true if the vector is a unit vector within the specified tolerance.
     */
    inline fun isUnit(lengthSquaredTolerance: Float = KINDA_SMALL_NUMBER) = abs(1f - sizeSquared()) < lengthSquaredTolerance

    /**
     * Checks whether vector is normalized.
     *
     * @return true if normalized, false otherwise.
     */
    inline fun isNormalized() = abs(1f - sizeSquared()) < THRESH_VECTOR_NORMALIZED // TODO port more methods

    /**
     * Get a textual representation of this vector.
     *
     * @return A string describing the vector.
     */
    override fun toString() = "X=%3.3f Y=%3.3f Z=%3.3f".format(x, y, z)

    /**
     * Squared distance between two points.
     *
     * @param other The other point.
     * @return The squared distance between two points.
     */
    inline fun distSquared(other: FVector) = square(other.x - x) + square(other.y - y)

    companion object {
        /**
         * Calculate the cross product of two vectors.
         *
         * @param a The first vector.
         * @param b The second vector.
         * @return The cross product.
         */
        @JvmStatic
        fun crossProduct(a: FVector, b: FVector) = a xor b

        /**
         * Calculate the dot product of two vectors.
         *
         * @param a The first vector.
         * @param b The second vector.
         * @return The dot product.
         */
        @JvmStatic
        fun dotProduct(a: FVector, b: FVector) = a or b

        /**
         * Util to calculate distance from a point to a bounding box
         *
         * @param mins 3D Point defining the lower values of the axis of the bound box
         * @param max 3D Point defining the lower values of the axis of the bound box
         * @param point 3D position of interest
         * @return the distance from the Point to the bounding box.
         */
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
}