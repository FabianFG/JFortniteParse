package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import me.fungames.jfortniteparse.util.toFloat16

@ExperimentalUnsignedTypes
class FMeshUVHalf : UClass {
    var u : UShort
    var v : UShort

    constructor(Ar: FArchive) {
        super.init(Ar)
        u = Ar.readUInt16()
        v = Ar.readUInt16()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt16(u)
        Ar.writeUInt16(v)
        super.completeWrite(Ar)
    }

    constructor(u: UShort, v: UShort) {
        this.u = u
        this.v = v
    }

    fun toMeshUVFloat() = FMeshUVFloat(u.toFloat16(), v.toFloat16())
}
