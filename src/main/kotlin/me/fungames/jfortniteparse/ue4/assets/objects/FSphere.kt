package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FSphere : FVector {

    var r : Float

    constructor(Ar : FArchive) : super(Ar) {
        super.init(Ar)
        r = if (Ar.ver >= 61)
            Ar.readFloat32()
        else
            0f
        super.complete(Ar)
    }

    override fun serialize(Ar: FArchiveWriter) {
        super.serialize(Ar)
        super.initWrite(Ar)
        if (Ar.ver >= 61)
            Ar.writeFloat32(r)
        super.completeWrite(Ar)
    }

    constructor(x: Float, y: Float, z: Float, r: Float) : super(x, y, z) {
        this.r = r
    }
}