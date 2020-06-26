package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.awt.Color

@ExperimentalUnsignedTypes
class FColor : UClass {
    var r: UByte
    var g: UByte
    var b: UByte
    var a: UByte

    constructor(Ar: FArchive) {
        super.init(Ar)
        r = Ar.readUInt8()
        g = Ar.readUInt8()
        b = Ar.readUInt8()
        a = Ar.readUInt8()
        super.complete(Ar)
    }

    constructor() : this(0u, 0u, 0u, 0u)

    fun toColor() = Color(r.toInt(), g.toInt(), b.toInt(), a.toInt())

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt8(r)
        Ar.writeUInt8(g)
        Ar.writeUInt8(b)
        Ar.writeUInt8(a)
        super.completeWrite(Ar)
    }

    constructor(r: UByte, g: UByte, b: UByte, a: UByte) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }
}