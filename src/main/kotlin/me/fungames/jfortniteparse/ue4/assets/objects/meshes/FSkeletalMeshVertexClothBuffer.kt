package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.FSkeletalMeshCustomVersion
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX

class FSkeletalMeshVertexClothBuffer {
    var clothIndexMapping: Array<ULong>? = null

    constructor(Ar: FArchive) {
        val stripFlags = FStripDataFlags(Ar, VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX)
        if (stripFlags.isDataStrippedForServer()) return
        Ar.skipBulkArray()
        if (FSkeletalMeshCustomVersion.get(Ar) >= FSkeletalMeshCustomVersion.CompactClothVertexBuffer) {
            clothIndexMapping = Ar.readTArray { Ar.readUInt64() }
        }
    }
}