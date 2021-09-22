package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

// from Linker.h
class FCompressedChunk {
    var uncompressedOffset: Int
    var uncompressedSize: Int
    var compressedOffset: Int
    var compressedSize: Int

    constructor(Ar: FArchive) {
        uncompressedOffset = Ar.readInt32()
        uncompressedSize = Ar.readInt32()
        compressedOffset = Ar.readInt32()
        compressedSize = Ar.readInt32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt32(uncompressedOffset)
        Ar.writeInt32(uncompressedSize)
        Ar.writeInt32(compressedOffset)
        Ar.writeInt32(compressedSize)
    }

    constructor(uncompressedOffset: Int, uncompressedSize: Int, compressedOffset: Int, compressedSize: Int) {
        this.uncompressedOffset = uncompressedOffset
        this.uncompressedSize = uncompressedSize
        this.compressedOffset = compressedOffset
        this.compressedSize = compressedSize
    }
}