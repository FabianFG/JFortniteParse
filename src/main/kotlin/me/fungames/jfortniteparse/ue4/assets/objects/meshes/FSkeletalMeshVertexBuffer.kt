package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.FSkeletalMeshCustomVersion
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_SUPPORT_GPUSKINNING_8_BONE_INFLUENCES

class FSkeletalMeshVertexBuffer {
    var numTexCoords = 0
    var meshExtension = FVector()
    var meshOrigin = FVector()
    var useFullPrecisionUVs = false
    var extraBoneInfluences = false
    var vertsHalf: Array<FGPUVertHalf>
    var vertsFloat: Array<FGPUVertFloat>

    constructor() {
        vertsHalf = emptyArray()
        vertsFloat = emptyArray()
    }

    constructor(Ar: FArchive) : this() {
        val stripFlags = FStripDataFlags(Ar, VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX)
        numTexCoords = Ar.readInt32()
        useFullPrecisionUVs = Ar.readBoolean()
        if (Ar.ver >= VER_UE4_SUPPORT_GPUSKINNING_8_BONE_INFLUENCES &&
            FSkeletalMeshCustomVersion.get(Ar) < FSkeletalMeshCustomVersion.UseSeparateSkinWeightBuffer) {
            extraBoneInfluences = Ar.readBoolean()
        }
        meshExtension = FVector(Ar)
        meshOrigin = FVector(Ar)
        if (!useFullPrecisionUVs) {
            vertsHalf = Ar.readBulkTArray { FGPUVertHalf(Ar, numTexCoords) }
        } else {
            vertsFloat = Ar.readBulkTArray { FGPUVertFloat(Ar, numTexCoords) }
        }
    }

    val vertexCount
        get() = when {
            vertsHalf.isNotEmpty() -> vertsHalf.size
            vertsFloat.isNotEmpty() -> vertsFloat.size
            else -> 0
        }
}