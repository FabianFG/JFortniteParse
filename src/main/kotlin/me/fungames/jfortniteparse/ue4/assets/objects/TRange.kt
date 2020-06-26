package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class TRange<T> : UClass {
    var lowerBound : TRangeBound<T>
    var upperBound : TRangeBound<T>

    constructor(Ar : FArchive) {
        super.init(Ar)
        lowerBound = TRangeBound(Ar)
        upperBound = TRangeBound(Ar)
        super.complete(Ar)
    }

    constructor(lowerBound: TRangeBound<T>, upperBound: TRangeBound<T>) {
        this.lowerBound = lowerBound
        this.upperBound = upperBound
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        lowerBound.serialize(Ar)
        upperBound.serialize(Ar)
        super.completeWrite(Ar)
    }
}