package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FChunkPart : UClass {
    var guid : FGuid
    var offset : UInt
    var size : UInt

    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        guid = FGuid(Ar)
        offset = Ar.readUInt32()
        size = Ar.readUInt32()
        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        guid.serialize(Ar)
        Ar.writeUInt32(offset)
        Ar.writeUInt32(size)
        super.completeWrite(Ar)
    }
}