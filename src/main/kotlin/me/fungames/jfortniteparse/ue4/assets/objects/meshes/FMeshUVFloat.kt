package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FMeshUVFloat : UClass {
    var u : Float
    var v : Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        u = Ar.readFloat32()
        v = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(u)
        Ar.writeFloat32(v)
        super.completeWrite(Ar)
    }

    constructor(u: Float, v: Float) {
        this.u = u
        this.v = v
    }
}
