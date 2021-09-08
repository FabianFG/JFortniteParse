package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FChunkPart {
    var guid: FGuid
    var offset: UInt
    var size: UInt

    constructor(Ar: FArchive) {
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        guid = FGuid(Ar)
        offset = Ar.readUInt32()
        size = Ar.readUInt32()
        Ar.seek(startPos + dataSize.toInt())
    }

    fun serialize(Ar: FArchiveWriter) {
        guid.serialize(Ar)
        Ar.writeUInt32(offset)
        Ar.writeUInt32(size)
    }
}