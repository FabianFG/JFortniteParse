package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FDateTime : UClass {
    var date : Long

    constructor(Ar: FArchive) {
        super.init(Ar)
        date = Ar.readInt64()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt64(date)
        super.completeWrite(Ar)
    }

    constructor(date: Long) {
        this.date = date
    }
}