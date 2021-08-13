package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FGPUVertFloat : FSkelMeshVertexBase {
    companion object {
        private const val MAX_SKELETAL_UV_SETS_UE4 = 4
    }

    var uv: Array<FMeshUVFloat>

    constructor() : super() {
        uv = emptyArray()
    }

    constructor(Ar: FArchive, numSkelUVSets: Int) : super() {
        serializeForGPU(Ar)
        uv = Array(MAX_SKELETAL_UV_SETS_UE4) { FMeshUVFloat(Ar) }
    }
}