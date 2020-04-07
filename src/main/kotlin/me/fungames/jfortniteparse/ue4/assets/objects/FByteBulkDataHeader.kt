package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FByteBulkDataHeader : UClass {
    var bulkDataFlags : Int
    var elementCount : Int
    var sizeOnDisk : Int
    var offsetInFile : Long

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        bulkDataFlags = Ar.readInt32()
        elementCount = Ar.readInt32()
        sizeOnDisk = Ar.readInt32()
        offsetInFile = Ar.readInt64() + (Ar.info?.bulkDataStartOffset ?: 0)
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(bulkDataFlags)
        Ar.writeInt32(elementCount)
        Ar.writeInt32(sizeOnDisk)
        Ar.writeInt64(offsetInFile)
        super.completeWrite(Ar)
    }

    constructor(bulkDataFlags: Int, elementCount : Int, sizeOnDisk : Int, offsetInFile : Long) {
        this.bulkDataFlags = bulkDataFlags
        this.elementCount = elementCount
        this.sizeOnDisk = sizeOnDisk
        this.offsetInFile = offsetInFile
    }
}