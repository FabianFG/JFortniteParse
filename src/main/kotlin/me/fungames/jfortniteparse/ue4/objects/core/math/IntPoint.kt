package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Structure for integer points in 2-d space.
 */
class FIntPoint : UClass {
    /** Holds the point's x-coordinate. */
    var x: Int

    /** Holds the point's y-coordinate. */
    var y: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readInt32()
        y = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(x)
        Ar.writeInt32(y)
        super.completeWrite(Ar)
    }

    constructor(x: Int, y: Int) {
        this.x = x
        this.y = y
    }

    override fun toString() = "X=%d Y=%d".format(x, y)
}