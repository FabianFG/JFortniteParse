package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.awt.Color

@ExperimentalUnsignedTypes
class FLinearColor : UClass {
    var r: Float
    var g: Float
    var b: Float
    var a: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        r = Ar.readFloat32()
        g = Ar.readFloat32()
        b = Ar.readFloat32()
        a = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(r)
        Ar.writeFloat32(g)
        Ar.writeFloat32(b)
        Ar.writeFloat32(a)
        super.completeWrite(Ar)
    }

    fun toColor() = Color(r, g, b, a)

    constructor(r: Float, g: Float, b: Float, a: Float) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }
}