package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FEvaluationTreeEntryHandle : UClass {
    var entryIndex : Int

    constructor(Ar : FArchive) {
        super.init(Ar)
        entryIndex = Ar.readInt32()
        super.complete(Ar)
    }

    constructor(entryIndex: Int) {
        this.entryIndex = entryIndex
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(entryIndex)
        super.completeWrite(Ar)
    }
}