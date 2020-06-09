package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_SUPPORT_32BIT_STATIC_MESH_INDICES

@ExperimentalUnsignedTypes
class FRawStaticIndexBuffer : UClass {

    var indices16 : Array<UShort>
    var indices32 : Array<UInt>

    constructor(Ar : FArchive) {
        super.init(Ar)
        if (Ar.ver < VER_UE4_SUPPORT_32BIT_STATIC_MESH_INDICES) {
            indices16 = Ar.readBulkTArray { Ar.readUInt16() }
            indices32 = emptyArray()
        } else {
            // serialize all indices as byte array
            val is32Bit = Ar.readBoolean()
            val data = Ar.readBulkTArray { Ar.readInt8() }.toByteArray()
            if (Ar.game >= GAME_UE4(25))
                Ar.readBoolean() // shouldExpandTo32Bit

            if (data.isEmpty()) {
                super.complete(Ar)
                indices16 = emptyArray()
                indices32 = emptyArray()
                return
            }
            if (is32Bit) {
                val count = data.size / 4
                val tempAr = FByteArchive(data)
                tempAr.littleEndian = Ar.littleEndian
                indices32 = Array(count) { tempAr.readUInt32() }
                indices16 = emptyArray()
            } else {
                val count = data.size / 2
                val tempAr = FByteArchive(data)
                tempAr.littleEndian = Ar.littleEndian
                indices16 = Array(count) { tempAr.readUInt16() }
                indices32 = emptyArray()
            }
        }
        super.complete(Ar)
    }

    constructor() : this(emptyArray(), emptyArray())

    constructor(indices16: Array<UShort>, indices32: Array<UInt>) {
        this.indices16 = indices16
        this.indices32 = indices32
    }

}