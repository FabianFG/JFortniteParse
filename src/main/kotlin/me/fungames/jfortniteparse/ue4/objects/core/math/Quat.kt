package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FQuat {
    var x: Float
    var y: Float
    var z: Float
    var w: Float

    constructor(Ar: FArchive) {
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
        w = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
        Ar.writeFloat32(w)
    }

    constructor(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    fun conjugate() {
        x = -x
        y = -y
        z = -z
    }
}