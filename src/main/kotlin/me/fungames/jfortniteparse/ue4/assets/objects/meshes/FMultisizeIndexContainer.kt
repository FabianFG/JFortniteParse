package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_KEEP_SKEL_MESH_INDEX_DATA

class FMultisizeIndexContainer {
    var indices16: Array<UShort>
    var indices32: Array<UInt>

    constructor(Ar: FArchive) : this() {
        if (Ar.ver < VER_UE4_KEEP_SKEL_MESH_INDEX_DATA) {
            val oldNeedsCPUAccess = Ar.readBoolean()
        }
        val dataSize = Ar.read().toByte()
        if (dataSize.toInt() == 0x02) {
            indices16 = Ar.readBulkTArray { Ar.readUInt16() }
        } else {
            indices32 = Ar.readBulkTArray { Ar.readUInt32() }
        }
    }

    constructor(indices16: Array<UShort> = emptyArray(), indices32: Array<UInt> = emptyArray()) {
        this.indices16 = indices16
        this.indices32 = indices32
    }
}