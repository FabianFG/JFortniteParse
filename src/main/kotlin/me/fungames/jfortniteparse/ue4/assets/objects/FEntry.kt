package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FEntry : UClass {
    /** The index into Items of the first item */
    var startIndex : Int
    /** The number of currently valid items */
    var size : Int
    /** The total capacity of allowed items before reallocating */
    var capacity : Int

    constructor(Ar : FArchive) {
        super.init(Ar)
        startIndex = Ar.readInt32()
        size = Ar.readInt32()
        capacity = Ar.readInt32()
        super.complete(Ar)
    }

    constructor(startIndex: Int, size: Int, capacity: Int) {
        this.startIndex = startIndex
        this.size = size
        this.capacity = capacity
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(startIndex)
        Ar.writeInt32(size)
        Ar.writeInt32(capacity)
        super.completeWrite(Ar)
    }
}