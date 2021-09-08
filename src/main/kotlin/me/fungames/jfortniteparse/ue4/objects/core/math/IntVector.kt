package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FIntVector {
    var x: Int
    var y: Int
    var z: Int

    constructor(Ar: FArchive) {
        x = Ar.readInt32()
        y = Ar.readInt32()
        z = Ar.readInt32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt32(x)
        Ar.writeInt32(y)
        Ar.writeInt32(z)
    }

    constructor(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }
}