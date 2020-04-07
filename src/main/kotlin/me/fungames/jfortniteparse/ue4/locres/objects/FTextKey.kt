package me.fungames.jfortniteparse.ue4.locres.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FTextKey : UClass {
    var stringHash : UInt
    var text : String

    constructor(Ar : FArchive) {
        super.init(Ar)
        stringHash = Ar.readUInt32()
        text = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(stringHash)
        Ar.writeString(text)
        super.completeWrite(Ar)
    }

    constructor(stringHash: UInt, text: String) : super() {
        this.stringHash = stringHash
        this.text = text
    }
}