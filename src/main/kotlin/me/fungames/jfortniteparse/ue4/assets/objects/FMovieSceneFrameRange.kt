package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FMovieSceneFrameRange : UClass {
    var value : TRange<Int>

    constructor(Ar : FArchive) {
        super.init(Ar)
        value = TRange(Ar) { Ar.readInt32() }
        super.complete(Ar)
    }

    constructor(value: TRange<Int>) {
        this.value = value
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        value.serialize(Ar) { Ar.writeInt32(it) }
        super.completeWrite(Ar)
    }
}