package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FIntPoint : UClass {
    var x: UInt
    var y: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readUInt32()
        y = Ar.readUInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(x)
        Ar.writeUInt32(y)
        super.completeWrite(Ar)
    }

    constructor(x: UInt, y: UInt) {
        this.x = x
        this.y = y
    }
}