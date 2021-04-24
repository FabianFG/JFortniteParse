package me.fungames.jfortniteparse.ue4.pak.objects

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FPakCompressedBlock {
    var compressedStart : Long
    var compressedEnd : Long

    constructor(Ar: FArchive) {
        compressedStart = Ar.readInt64()
        compressedEnd = Ar.readInt64()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt64(compressedStart)
        Ar.writeInt64(compressedEnd)
    }

    constructor(compressedStart: Long, compressedEnd: Long) {
        this.compressedStart = compressedStart
        this.compressedEnd = compressedEnd
    }
}