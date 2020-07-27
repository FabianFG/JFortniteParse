package me.fungames.jfortniteparse.ue4.objects.core.math

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FRotator : UClass {
    var pitch: Float
    var yaw: Float
    var roll: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        pitch = Ar.readFloat32()
        yaw = Ar.readFloat32()
        roll = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(pitch)
        Ar.writeFloat32(yaw)
        Ar.writeFloat32(roll)
        super.completeWrite(Ar)
    }

    constructor(pitch: Float, yaw: Float, roll: Float) {
        this.pitch = pitch
        this.yaw = yaw
        this.roll = roll
    }
}