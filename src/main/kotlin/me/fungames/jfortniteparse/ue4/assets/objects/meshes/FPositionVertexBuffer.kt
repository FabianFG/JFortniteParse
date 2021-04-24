package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FPositionVertexBuffer {
    var verts: Array<FVector>
    var stride: Int
    var numVertices: Int

    constructor(Ar: FArchive) {
        stride = Ar.readInt32()
        numVertices = Ar.readInt32()
        verts = Ar.readBulkTArray { FVector(Ar) }
    }

    constructor(verts: Array<FVector>, stride: Int, numVertices: Int) {
        this.verts = verts
        this.stride = stride
        this.numVertices = numVertices
    }
}