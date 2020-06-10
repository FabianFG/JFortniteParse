package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FBox : UClass {
    var min: FVector
    var max: FVector
    var isValid: Boolean

    constructor(Ar: FArchive) {
        super.init(Ar)
        min = FVector(Ar)
        max = FVector(Ar)
        isValid = Ar.readFlag()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        min.serialize(Ar)
        max.serialize(Ar)
        Ar.writeFlag(isValid)
        super.completeWrite(Ar)
    }

    constructor(min: FVector, max: FVector) {
        this.min = min
        this.max = max
        this.isValid = true
    }
}