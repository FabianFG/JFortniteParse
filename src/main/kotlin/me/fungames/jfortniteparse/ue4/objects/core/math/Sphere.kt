package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.EUnrealEngineObjectUE5Version
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FSphere : FVector {
    var r: Float

    constructor(Ar: FArchive) : super(Ar) {
        r = if (Ar.ver >= 61) {
            if (Ar.ver >= EUnrealEngineObjectUE5Version.LARGE_WORLD_COORDINATES) Ar.readDouble().toFloat() else Ar.readFloat32()
        } else 0f
    }

    override fun serialize(Ar: FArchiveWriter) {
        super.serialize(Ar)
        if (Ar.ver >= 61)
            Ar.writeFloat32(r)
    }

    constructor(x: Float, y: Float, z: Float, r: Float) : super(x, y, z) {
        this.r = r
    }
}