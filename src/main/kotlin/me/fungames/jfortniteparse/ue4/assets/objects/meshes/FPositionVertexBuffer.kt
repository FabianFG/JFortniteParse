package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FPositionVertexBuffer : UClass {
    var verts : Array<FVector>
    var stride : Int
    var numVertices : Int

    constructor(Ar : FArchive) {
        super.init(Ar)
        stride = Ar.readInt32()
        numVertices = Ar.readInt32()
        verts = Ar.readBulkTArray { FVector(Ar) }
        super.complete(Ar)
    }

    constructor(verts: Array<FVector>, stride: Int, numVertices: Int) {
        this.verts = verts
        this.stride = stride
        this.numVertices = numVertices
    }
}