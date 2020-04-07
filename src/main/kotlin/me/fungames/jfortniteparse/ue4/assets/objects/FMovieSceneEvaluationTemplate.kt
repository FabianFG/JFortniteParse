package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FMovieSceneEvaluationTemplate : UClass {
    /** The internal value of the serial number */
    var value : UInt

    constructor(Ar : FArchive) {
        super.init(Ar)
        value = Ar.readUInt32()
        super.complete(Ar)
    }

    constructor(value: UInt) {
        this.value = value
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(value)
        super.completeWrite(Ar)
    }
}