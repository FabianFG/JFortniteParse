package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FColor
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_SUPPORT_8_BONE_INFLUENCES_SKELETAL_MESHES

open class FSoftVertex : FSkelMeshVertexBase {
    companion object {
        private const val MAX_SKELETAL_UV_SETS_UE4 = 4
    }

    var uv: Array<FMeshUVFloat>
    var color: FColor

    constructor(Ar: FArchive, isRigid: Boolean = false) {
        serializeForEditor(Ar)
        uv = Array(MAX_SKELETAL_UV_SETS_UE4) { FMeshUVFloat(Ar) }
        color = FColor(Ar)
        if (!isRigid) {
            infs = FSkinWeightInfo(Ar, Ar.ver >= VER_UE4_SUPPORT_8_BONE_INFLUENCES_SKELETAL_MESHES)
        } else {
            infs = FSkinWeightInfo().apply {
                boneIndex[0] = Ar.readUInt8()
                boneWeight[0] = 255u
            }
        }
    }
}

class FRigidVertex : FSoftVertex {
    constructor(Ar: FArchive) : super(Ar, true)
}