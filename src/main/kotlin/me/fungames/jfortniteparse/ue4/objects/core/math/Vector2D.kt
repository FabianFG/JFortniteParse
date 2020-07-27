package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FVector2D : UClass {
    var x: Float
    var y: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        super.completeWrite(Ar)
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}