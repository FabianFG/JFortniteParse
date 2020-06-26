package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPackedRGBA16N : UClass {
    var x : UShort
    var y : UShort
    var z : UShort
    var w : UShort

    constructor(Ar : FArchive) {
        super.init(Ar)
        x = Ar.readUInt16()
        y = Ar.readUInt16()
        z = Ar.readUInt16()
        w = Ar.readUInt16()
        if (Ar.game >= GAME_UE4(20)) {
            x = x xor 0x8000u
            y = y xor 0x8000u
            z = z xor 0x8000u
            w = w xor 0x8000u
        }
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        if (Ar.game >= GAME_UE4(20)) {
            Ar.writeUInt16(x xor 0x8000u)
            Ar.writeUInt16(y xor 0x8000u)
            Ar.writeUInt16(z xor 0x8000u)
            Ar.writeUInt16(w xor 0x8000u)
        } else {
            Ar.writeUInt16(x)
            Ar.writeUInt16(y)
            Ar.writeUInt16(z)
            Ar.writeUInt16(w)
        }
        super.completeWrite(Ar)
    }

    constructor(x: UShort, y: UShort, z: UShort, w: UShort) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    fun toPackedNormal() : FPackedNormal {
        val vector = FVector(this)
        return FPackedNormal(vector)
    }
}