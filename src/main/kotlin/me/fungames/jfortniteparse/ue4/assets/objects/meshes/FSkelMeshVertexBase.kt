package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector4
import me.fungames.jfortniteparse.ue4.objects.rendercore.FPackedNormal
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.FRenderingObjectVersion
import me.fungames.jfortniteparse.ue4.versions.FSkeletalMeshCustomVersion
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_SUPPORT_8_BONE_INFLUENCES_SKELETAL_MESHES

open class FSkelMeshVertexBase {
    lateinit var pos: FVector
    var normal: Array<FPackedNormal>
    var infs: FSkinWeightInfo? = null

    constructor() {
        normal = emptyArray()
    }

    fun serializeForGPU(Ar: FArchive) {
        normal = arrayOf(FPackedNormal(Ar), FPackedNormal(0u), FPackedNormal(Ar))
        if (FSkeletalMeshCustomVersion.get(Ar) < FSkeletalMeshCustomVersion.UseSeparateSkinWeightBuffer) {
            // serialized as separate buffer starting with UE4.15
            infs = FSkinWeightInfo(Ar, Ar.ver >= VER_UE4_SUPPORT_8_BONE_INFLUENCES_SKELETAL_MESHES)
        }
        pos = FVector(Ar)
    }

    fun serializeForEditor(Ar: FArchive) {
        pos = FVector(Ar)
        normal = if (FRenderingObjectVersion.get(Ar) < FRenderingObjectVersion.IncreaseNormalPrecision) {
            Array(3) { FPackedNormal(Ar) }
        } else {
            // New normals are stored with full floating point precision
            arrayOf(FPackedNormal(FVector(Ar)), FPackedNormal(FVector(Ar)), FPackedNormal(FVector4(Ar)))
        }
    }
}