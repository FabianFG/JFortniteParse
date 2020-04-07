package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class TEvaluationTreeEntryContainer<T> : UClass {
    var entries : Array<FEntry>
    var items : Array<Any?>//<T>

    constructor(Ar : FArchive) {
        super.init(Ar)
        entries = Ar.readTArray { FEntry(it) }
        items = Array(Ar.readInt32()) { null }
        super.complete(Ar)
    }

    constructor(entries: Array<FEntry>, items: Array<Any?>) {
        this.entries = entries
        this.items = items
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeTArray(entries) { it.serialize(Ar) }
        Ar.writeTArray(items) {  }
        super.completeWrite(Ar)
    }
}