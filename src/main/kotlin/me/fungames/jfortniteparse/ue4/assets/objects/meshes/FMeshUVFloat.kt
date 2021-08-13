package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FMeshUVFloat {
    var u: Float
    var v: Float

    constructor(Ar: FArchive) {
        u = Ar.readFloat32()
        v = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeFloat32(u)
        Ar.writeFloat32(v)
    }

    constructor(u: Float = 0f, v: Float = 0f) {
        this.u = u
        this.v = v
    }
}
