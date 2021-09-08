package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import me.fungames.jfortniteparse.util.toFloat16

class FMeshUVHalf {
    var u: UShort
    var v: UShort

    constructor(Ar: FArchive) {
        u = Ar.readUInt16()
        v = Ar.readUInt16()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt16(u)
        Ar.writeUInt16(v)
    }

    constructor(u: UShort = 0u, v: UShort = 0u) {
        this.u = u
        this.v = v
    }

    fun toMeshUVFloat() = FMeshUVFloat(u.toFloat16(), v.toFloat16())
}
