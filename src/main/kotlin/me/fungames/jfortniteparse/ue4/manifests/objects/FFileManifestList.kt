package me.fungames.jfortniteparse.ue4.manifests.objects

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FFileManifestList {
    var fileList: Array<FFileManifest>

    constructor(Ar: FArchive) {
        val startPos = Ar.pos()
        val dataSize = Ar.readUInt32()
        /*val dataVersionInt = */Ar.readUInt8()
        val elementCount = Ar.readInt32()
        fileList = Array(elementCount) { FFileManifest() }
        for (fileManifest in fileList) fileManifest.fileName = Ar.readString()
        for (fileManifest in fileList) fileManifest.symlinkTarget = Ar.readString()
        for (fileManifest in fileList) fileManifest.fileHash = Ar.read(20)
        for (fileManifest in fileList) fileManifest.fileMetaFlags = Ar.readUInt8()
        for (fileManifest in fileList) fileManifest.installTags = Ar.readTArray { Ar.readString() }
        for (fileManifest in fileList) fileManifest.chunkParts = Ar.readTArray { FChunkPart(Ar) }
        Ar.seek(startPos + dataSize.toInt())
    }

    fun serialize(Ar: FArchiveWriter) {}
}