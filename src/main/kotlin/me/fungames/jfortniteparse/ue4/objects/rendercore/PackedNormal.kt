package me.fungames.jfortniteparse.ue4.objects.rendercore

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/** A normal vector, quantized and packed into 32-bits. */
class FPackedNormal {
    var data: UInt

    constructor(Ar: FArchive) {
        data = Ar.readUInt32()
        if (Ar.game >= GAME_UE4(20))
            data = data xor 0x80808080u
    }

    constructor(vector: FVector) {
        data = (((vector.x + 1) * 127.5f).toInt()
                + (((vector.y + 1) * 127.5f).toInt() shl 8)
                + (((vector.z + 1) * 127.5f).toInt() shl 16)).toUInt()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt32(if (Ar.game >= GAME_UE4(20)) data xor 0x80808080u else data)
    }

    constructor(data: UInt) {
        this.data = data
    }
}

/** A vector, quantized and packed into 32-bits. */
class FPackedRGBA16N {
    var x: UShort
    var y: UShort
    var z: UShort
    var w: UShort

    constructor(Ar: FArchive) {
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
    }

    fun serialize(Ar: FArchiveWriter) {
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
    }

    constructor(x: UShort, y: UShort, z: UShort, w: UShort) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }

    fun toPackedNormal() = FPackedNormal(
        FVector(
            (x.toFloat() - 32767.5f) / 32767.5f,
            (y.toFloat() - 32767.5f) / 32767.5f,
            (z.toFloat() - 32767.5f) / 32767.5f
        )
    )
}