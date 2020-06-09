package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FIntVector : UClass {

    var x : Int
    var y : Int
    var z : Int

    constructor(Ar : FArchive) {
        super.init(Ar)
        x = Ar.readInt32()
        y = Ar.readInt32()
        z = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(x)
        Ar.writeInt32(y)
        Ar.writeInt32(z)
        super.completeWrite(Ar)
    }

    constructor(x: Int, y: Int, z: Int) {
        this.x = x
        this.y = y
        this.z = z
    }
}