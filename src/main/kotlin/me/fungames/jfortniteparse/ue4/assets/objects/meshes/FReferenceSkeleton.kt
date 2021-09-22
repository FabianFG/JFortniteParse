package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FColor
import me.fungames.jfortniteparse.ue4.objects.core.math.FTransform
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_FIXUP_ROOTBONE_PARENT
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_REFERENCE_SKELETON_REFACTOR

class FMeshBoneInfo {
    var name: FName
    var parentIndex: Int

    constructor(Ar: FArchive) {
        name = Ar.readFName()
        parentIndex = Ar.readInt32()
        if (Ar.ver < VER_UE4_REFERENCE_SKELETON_REFACTOR) {
            val dummyColor = FColor(Ar)
        }
    }

    constructor(name: FName, parentIndex: Int) {
        this.name = name
        this.parentIndex = parentIndex
    }
}

class FReferenceSkeleton {
    var finalRefBoneInfo: Array<FMeshBoneInfo>
    var finalRefBonePose: Array<FTransform>
    var finalNameToIndexMap: Map<FName, Int>? = null

    constructor(Ar: FArchive) {
        finalRefBoneInfo = Ar.readTArray { FMeshBoneInfo(Ar) }
        finalRefBonePose = Ar.readTArray { FTransform(Ar) }

        if (Ar.ver >= VER_UE4_REFERENCE_SKELETON_REFACTOR) {
            finalNameToIndexMap = Ar.readTMap { Ar.readFName() to Ar.readInt32() }
        }

        if (Ar.ver < VER_UE4_FIXUP_ROOTBONE_PARENT) {
            if (finalRefBoneInfo.isNotEmpty() && finalRefBoneInfo[0].parentIndex != -1) {
                finalRefBoneInfo[0] = FMeshBoneInfo(finalRefBoneInfo[0].name, -1)
            }
        }
    }
}