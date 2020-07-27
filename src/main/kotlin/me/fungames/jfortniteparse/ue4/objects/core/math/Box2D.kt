package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FBox2D : UClass {
    var min: FVector2D
    var max: FVector2D
    var isValid: Boolean

    constructor(Ar: FArchive) {
        super.init(Ar)
        min = FVector2D(Ar)
        max = FVector2D(Ar)
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

    constructor(inMin: FVector2D, inMax: FVector2D) {
        min = inMin
        max = inMax
        isValid = true
    }
}