package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FVector : UClass {
    var x: Float
    var y: Float
    var z: Float

    constructor(packedRGBA16N: FPackedRGBA16N) {
        this.x = (packedRGBA16N.x.toFloat() - 32767.5f) / 32767.5f
        this.y = (packedRGBA16N.y.toFloat() - 32767.5f) / 32767.5f
        this.z = (packedRGBA16N.z.toFloat() - 32767.5f) / 32767.5f
    }

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
        super.completeWrite(Ar)
    }

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }
}