package me.fungames.jfortniteparse.ue4.objects.moviescene

import me.fungames.jfortniteparse.ue4.objects.core.math.TRange
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FMovieSceneFrameRange {
    var value: TRange<Int>

    constructor(Ar: FArchive) {
        value = TRange(Ar) { Ar.readInt32() }
    }

    constructor(value: TRange<Int>) {
        this.value = value
    }

    fun serialize(Ar: FArchiveWriter) {
        value.serialize(Ar) { Ar.writeInt32(it) }
    }
}