package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FColor
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX

class FColorVertexBuffer {
    var stripFlags: FStripDataFlags
    var stride: Int
    var numVertices: Int
    var data: Array<FColor>

    constructor(Ar: FArchive) {
        stripFlags = FStripDataFlags(Ar, VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX)
        stride = Ar.readInt32()
        numVertices = Ar.readInt32()
        data = if (!stripFlags.isDataStrippedForServer() && numVertices > 0)
            Ar.readBulkTArray { FColor(Ar) }
        else
            emptyArray()
    }

    constructor(stripFlags: FStripDataFlags, stride: Int, numVertices: Int, data: Array<FColor>) {
        this.stripFlags = stripFlags
        this.stride = stride
        this.numVertices = numVertices
        this.data = data
    }
}