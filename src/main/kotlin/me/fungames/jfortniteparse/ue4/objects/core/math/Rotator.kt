package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Implements a container for rotation information.
 *
 * All rotation values are stored in degrees.
 */
@ExperimentalUnsignedTypes
class FRotator : UClass {
    /** Rotation around the right axis (around Y axis), Looking up and down (0=Straight Ahead, +Up, -Down) */
    var pitch: Float

    /** Rotation around the up axis (around Z axis), Running in circles 0=East, +North, -South. */
    var yaw: Float

    /** Rotation around the forward axis (around X axis), Tilting your head, 0=Straight, +Clockwise, -CCW. */
    var roll: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        pitch = Ar.readFloat32()
        yaw = Ar.readFloat32()
        roll = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(pitch)
        Ar.writeFloat32(yaw)
        Ar.writeFloat32(roll)
        super.completeWrite(Ar)
    }

    /**
     * Default constructor (no initialization).
     */
    constructor() : this(0f)

    /**
     * Constructor
     *
     * @param f Value to set all components to.
     */
    constructor(f: Float) : this(f, f, f)

    /**
     * Constructor.
     *
     * @param pitch Pitch in degrees.
     * @param yaw Yaw in degrees.
     * @param roll Roll in degrees.
     */
    constructor(pitch: Float, yaw: Float, roll: Float) {
        this.pitch = pitch
        this.yaw = yaw
        this.roll = roll
    }

    /**
     * Get the result of adding a rotator to this.
     *
     * @param r The other rotator.
     * @return The result of adding a rotator to this.
     */
    operator fun plus(r: FRotator) = FRotator(pitch + r.pitch, yaw + r.yaw, roll + r.roll)

    /**
     * Get the result of subtracting a rotator from this.
     *
     * @param r The other rotator.
     * @return The result of subtracting a rotator from this.
     */
    operator fun minus(r: FRotator) = FRotator(pitch - r.pitch, yaw - r.yaw, roll - r.roll)

    /**
     * Get the result of scaling this rotator.
     *
     * @param scale The scaling factor.
     * @return The result of scaling.
     */
    operator fun times(scale: Float) = FRotator(pitch * scale, yaw * scale, roll * scale)

    /**
     * Multiply this rotator by a scaling factor.
     *
     * @param scale The scaling factor.
     */
    operator fun timesAssign(scale: Float) {
        pitch *= scale; yaw *= scale; roll *= scale
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FRotator

        if (pitch != other.pitch) return false
        if (yaw != other.yaw) return false
        if (roll != other.roll) return false

        return true
    }

    override fun hashCode(): Int {
        var result = pitch.hashCode()
        result = 31 * result + yaw.hashCode()
        result = 31 * result + roll.hashCode()
        return result
    }

    /**
     * Rotate a vector rotated by this rotator.
     *
     * @param v The vector to rotate.
     * @return The rotated vector.
     */
    fun rotateVector(v: FVector) = FVector(FRotationMatrix(this).transformVector(v))

    /**
     * Returns the vector rotated by the inverse of this rotator.
     *
     * @param v The vector to rotate.
     * @return The rotated vector.
     */
    fun unrotateVector(v: FVector) = FVector(FRotationMatrix(this).getTransposed().transformVector(v))

    /**
     * Get a textual representation of the vector.
     *
     * @return Text describing the vector.
     */
    override fun toString() = "P=%f Y=%f R=%f".format(pitch, yaw, roll)
}