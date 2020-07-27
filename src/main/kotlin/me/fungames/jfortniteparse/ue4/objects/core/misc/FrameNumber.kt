package me.fungames.jfortniteparse.ue4.objects.core.misc

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FFrameNumber : UClass {
    var value : Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        value = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(value)
        super.completeWrite(Ar)
    }

    constructor(value: Float) {
        this.value = value
    }
}