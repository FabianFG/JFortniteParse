package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FSimpleCurveKey : UClass {
    var time : Float
    var value : Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        time = Ar.readFloat32()
        value = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(time)
        Ar.writeFloat32(value)
        super.completeWrite(Ar)
    }

    constructor(time : Float, value: Float) {
        this.time = time
        this.value = value
    }
}