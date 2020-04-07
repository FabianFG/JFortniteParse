package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPerPlatformInt : UClass {
    var cooked : Boolean
    var value: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        cooked = Ar.readFlag()
        value = Ar.readUInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFlag(cooked)
        Ar.writeUInt32(value)
        super.completeWrite(Ar)
    }

    constructor(cooked : Boolean, value : UInt) {
        this.cooked = cooked
        this.value = value
    }
}