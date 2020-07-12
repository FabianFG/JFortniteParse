package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.ERangeBoundType
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class TRangeBound<T> : UClass {
    var boundType: ERangeBoundType
    var value: T

    constructor(Ar: FArchive, init: () -> T) {
        super.init(Ar)
        boundType = ERangeBoundType.values()[Ar.readInt8().toInt()]
        value = init()
        super.complete(Ar)
    }

    constructor(boundType: ERangeBoundType, value: T) {
        this.boundType = boundType
        this.value = value
    }

    fun serialize(Ar: FArchiveWriter, write: (T) -> Unit) {
        super.initWrite(Ar)
        Ar.writeInt8(boundType.ordinal.toByte())
        write(value)
        super.completeWrite(Ar)
    }
}