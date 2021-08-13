package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FColor
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX

class FSkeletalMeshVertexColorBuffer {
    val data: Array<FColor>

    constructor(Ar: FArchive) {
        val stripFlags = FStripDataFlags(Ar, VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX)
        data = if (!stripFlags.isDataStrippedForServer()) Ar.readBulkTArray { FColor(Ar) } else emptyArray()
    }

    constructor(data: Array<FColor> = emptyArray()) {
        this.data = data
    }
}