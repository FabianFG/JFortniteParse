package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Structure for integer points in 2-d space.
 */
class FIntPoint {
    /** Holds the point's x-coordinate. */
    var x: Int

    /** Holds the point's y-coordinate. */
    var y: Int

    constructor(Ar: FArchive) {
        x = Ar.readInt32()
        y = Ar.readInt32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt32(x)
        Ar.writeInt32(y)
    }

    constructor(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    override fun toString() = "X=%d Y=%d".format(x, y)
}