package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

// from Linker.h
@ExperimentalUnsignedTypes
class FCompressedChunk : UClass {
    var uncompressedOffset: Int
    var uncompressedSize: Int
    var compressedOffset: Int
    var compressedSize: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        uncompressedOffset = Ar.readInt32()
        uncompressedSize = Ar.readInt32()
        compressedOffset = Ar.readInt32()
        compressedSize = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(uncompressedOffset)
        Ar.writeInt32(uncompressedSize)
        Ar.writeInt32(compressedOffset)
        Ar.writeInt32(compressedSize)
        super.completeWrite(Ar)
    }

    constructor(uncompressedOffset: Int, uncompressedSize: Int, compressedOffset: Int, compressedSize: Int) {
        this.uncompressedOffset = uncompressedOffset
        this.uncompressedSize = uncompressedSize
        this.compressedOffset = compressedOffset
        this.compressedSize = compressedSize
    }
}