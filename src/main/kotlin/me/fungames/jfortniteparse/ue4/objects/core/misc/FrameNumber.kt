package me.fungames.jfortniteparse.ue4.objects.core.misc

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FFrameNumber {
    var value : Float

    constructor(Ar: FArchive) {
        value = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeFloat32(value)
    }

    constructor(value: Float) {
        this.value = value
    }
}