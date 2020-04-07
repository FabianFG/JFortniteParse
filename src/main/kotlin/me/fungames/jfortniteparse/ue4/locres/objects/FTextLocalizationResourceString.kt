package me.fungames.jfortniteparse.ue4.locres.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FTextLocalizationResourceString : UClass {
    var data : String
    var refCount : Int

    constructor(Ar : FArchive) {
        super.init(Ar)
        data = Ar.readString()
        refCount = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeString(data)
        Ar.writeInt32(refCount)
        super.completeWrite(Ar)
    }

    constructor(data: String, refCount: Int) : super() {
        this.data = data
        this.refCount = refCount
    }
}