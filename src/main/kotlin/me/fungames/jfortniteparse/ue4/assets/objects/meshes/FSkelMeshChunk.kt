package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_APEX_CLOTH

class FSkelMeshChunk {
    var baseVertexIndex = 0
    var rigidVertices = emptyArray<FRigidVertex>()
    var softVertices = emptyArray<FSoftVertex>()
    var boneMap: UShortArray
    var numRigidVertices: Int
    var numSoftVertices: Int
    var maxBoneInfluences: Int
    var hasClothData: Boolean

    constructor(Ar: FArchive) {
        val stripFlags = FStripDataFlags(Ar)
        if (!stripFlags.isDataStrippedForServer()) {
            baseVertexIndex = Ar.readInt32()
        }
        if (!stripFlags.isEditorDataStripped()) {
            rigidVertices = Ar.readTArray { FRigidVertex(Ar) }
            softVertices = Ar.readTArray { FSoftVertex(Ar) }
        }
        boneMap = UShortArray(Ar.readInt32()) { Ar.readUInt16() }
        numRigidVertices = Ar.readInt32()
        numSoftVertices = Ar.readInt32()
        maxBoneInfluences = Ar.readInt32()
        hasClothData = false
        if (Ar.ver >= VER_UE4_APEX_CLOTH) {
            // Physics data, drop
            val clothMappingData = Ar.readTArray { FMeshToMeshVertData(Ar) }
            Ar.readTArray { FVector(Ar) } // PhysicalMeshVertices
            Ar.readTArray { FVector(Ar) } // PhysicalMeshNormals
            Ar.skip(4) // CorrespondClothAssetIndex, ClothAssetSubmeshIndex
            hasClothData = clothMappingData.isNotEmpty()
        }
    }
}