package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class TRange<T> : UClass {
    var lowerBound: TRangeBound<T>
    var upperBound: TRangeBound<T>

    constructor(Ar: FArchive, init: () -> T) {
        super.init(Ar)
        lowerBound = TRangeBound(Ar, init)
        upperBound = TRangeBound(Ar, init)
        super.complete(Ar)
    }

    constructor(lowerBound: TRangeBound<T>, upperBound: TRangeBound<T>) {
        this.lowerBound = lowerBound
        this.upperBound = upperBound
    }

    fun serialize(Ar: FArchiveWriter, write: (T) -> Unit) {
        super.initWrite(Ar)
        lowerBound.serialize(Ar, write)
        upperBound.serialize(Ar, write)
        super.completeWrite(Ar)
    }
}