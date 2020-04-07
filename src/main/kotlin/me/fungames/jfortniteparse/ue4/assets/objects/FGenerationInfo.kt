package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FGenerationInfo : UClass {
    var exportCount: Int
    var nameCount: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        exportCount = Ar.readInt32()
        nameCount = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(exportCount)
        Ar.writeInt32(nameCount)
        super.completeWrite(Ar)
    }

    constructor(exportCount: Int, nameCount: Int) {
        this.exportCount = exportCount
        this.nameCount = nameCount
    }
}