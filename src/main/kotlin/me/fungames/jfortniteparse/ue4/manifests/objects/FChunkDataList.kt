package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FChunkDataList : UClass {

    var chunkList : Array<FChunkInfo>

    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        val elementCount = Ar.readInt32()
        chunkList = Array(elementCount) { FChunkInfo() }
        for (chunkInfo in chunkList) chunkInfo.guid = FGuid(Ar)
        for (chunkInfo in chunkList) chunkInfo.hash = Ar.readUInt64()
        for (chunkInfo in chunkList) chunkInfo.shaHash = Ar.read(20)
        for (chunkInfo in chunkList) chunkInfo.groupNumber = Ar.readUInt8()
        for (chunkInfo in chunkList) chunkInfo.windowSize = Ar.readUInt32()
        for (chunkInfo in chunkList) chunkInfo.fileSize = Ar.readInt64()
        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchive) {

    }
}