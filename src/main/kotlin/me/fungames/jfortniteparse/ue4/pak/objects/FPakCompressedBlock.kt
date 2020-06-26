package me.fungames.jfortniteparse.ue4.pak.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPakCompressedBlock : UClass {
    var compressedStart : Long
    var compressedEnd : Long

    constructor(Ar: FArchive) {
        super.init(Ar)
        compressedStart = Ar.readInt64()
        compressedEnd = Ar.readInt64()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt64(compressedStart)
        Ar.writeInt64(compressedEnd)
        super.completeWrite(Ar)
    }


    constructor(compressedStart: Long, compressedEnd: Long) : super() {
        this.compressedStart = compressedStart
        this.compressedEnd = compressedEnd
    }
}