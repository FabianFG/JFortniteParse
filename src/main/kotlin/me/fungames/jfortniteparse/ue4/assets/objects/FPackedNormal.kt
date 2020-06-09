package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPackedNormal : UClass {
    var data : UInt

    constructor(Ar : FArchive) {
        super.init(Ar)
        data = Ar.readUInt32()
        if (Ar.game >= GAME_UE4(20))
            data = data xor 0x80808080u
        super.complete(Ar)
    }

    constructor(vector : FVector) {
        data = (((vector.x + 1) * 127.5f).toInt()
        + (((vector.y + 1) * 127.5f).toInt() shl 8)
        + (((vector.z + 1) * 127.5f).toInt() shl 16)).toUInt()
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        if (Ar.game >= GAME_UE4(20))
            Ar.writeUInt32(data xor 0x80808080u)
        else
            Ar.writeUInt32(data)
        super.completeWrite(Ar)
    }

    constructor(data: UInt) {
        this.data = data
    }
}