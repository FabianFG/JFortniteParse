package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_SUPPORT_32BIT_STATIC_MESH_INDICES

class FRawStaticIndexBuffer {
    var indices16: Array<UShort>
    var indices32: Array<UInt>

    constructor(Ar: FArchive) {
        if (Ar.ver < VER_UE4_SUPPORT_32BIT_STATIC_MESH_INDICES) {
            indices16 = Ar.readBulkTArray { Ar.readUInt16() }
            indices32 = emptyArray()
        } else {
            // serialize all indices as byte array
            val is32Bit = Ar.readBoolean()
            val data = Ar.readBulkByteArray()
            if (Ar.versions["RawIndexBuffer.HasShouldExpandTo32Bit"])
                Ar.readBoolean() // shouldExpandTo32Bit

            if (data.isEmpty()) {
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
    }

    constructor() : this(emptyArray(), emptyArray())

    constructor(indices16: Array<UShort>, indices32: Array<UInt>) {
        this.indices16 = indices16
        this.indices32 = indices32
    }
}