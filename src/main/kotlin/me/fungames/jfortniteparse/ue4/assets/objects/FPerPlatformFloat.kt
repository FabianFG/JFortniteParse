package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPerPlatformFloat : UClass {
    var cooked : Boolean
    var value: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        cooked = Ar.readFlag()
        value = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFlag(cooked)
        Ar.writeFloat32(value)
        super.completeWrite(Ar)
    }

    constructor(cooked : Boolean, value : Float) {
        this.cooked = cooked
        this.value = value
    }
}