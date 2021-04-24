package me.fungames.jfortniteparse.ue4.objects.moviescene.evaluation

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FMovieSceneEvaluationTemplate {
    /** The internal value of the serial number */
    var value: UInt

    constructor(Ar: FArchive) {
        value = Ar.readUInt32()
    }

    constructor(value: UInt) {
        this.value = value
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt32(value)
    }
}