package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FFileManifestList : UClass {

    var fileList : Array<FFileManifest>

    constructor(Ar : FArchive) {
        super.init(Ar)
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        val elementCount = Ar.readInt32()
        fileList = Array(elementCount) { FFileManifest() }
        for (fileManifest in fileList) fileManifest.fileName = Ar.readString()
        for (fileManifest in fileList) fileManifest.symlinkTarget = Ar.readString()
        for (fileManifest in fileList) fileManifest.fileHash = Ar.read(20)
        for (fileManifest in fileList) fileManifest.fileMetaFlags = Ar.readUInt8()
        for (fileManifest in fileList) fileManifest.installTags = Ar.readTArray { it.readString() }
        for (fileManifest in fileList) fileManifest.chunkParts = Ar.readTArray { FChunkPart(Ar) }
        Ar.seek(startPos + dataSize.toInt())
        super.complete(Ar)
    }

    fun serialize(Ar : FArchive) {

    }
}