package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.ERangeBoundType
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class TRangeBound<T> : UClass {
    var boundType : ERangeBoundType
    var value : T? = null

    constructor(Ar : FArchive) {
        super.init(Ar)
        boundType = ERangeBoundType.values()[Ar.readInt8().toInt()]
        super.complete(Ar)
    }

    constructor(boundType: ERangeBoundType, value: T?) {
        this.boundType = boundType
        this.value = value
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt8(boundType.ordinal.toByte())
        super.completeWrite(Ar)
    }
}