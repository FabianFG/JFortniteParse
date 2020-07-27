package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class TRange<ElementType> : UClass {
    /** Holds the range's lower bound. */
    var lowerBound: TRangeBound<ElementType>

    /** Holds the range's upper bound. */
    var upperBound: TRangeBound<ElementType>

    constructor(Ar: FArchive, init: () -> ElementType) {
        super.init(Ar)
        lowerBound = TRangeBound(Ar, init)
        upperBound = TRangeBound(Ar, init)
        super.complete(Ar)
    }

    constructor(lowerBound: TRangeBound<ElementType>, upperBound: TRangeBound<ElementType>) {
        this.lowerBound = lowerBound
        this.upperBound = upperBound
    }

    fun serialize(Ar: FArchiveWriter, write: (ElementType) -> Unit) {
        super.initWrite(Ar)
        lowerBound.serialize(Ar, write)
        upperBound.serialize(Ar, write)
        super.completeWrite(Ar)
    }
}