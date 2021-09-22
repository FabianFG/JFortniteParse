package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.*

class FSkelMeshSection {
    var materialIndex: Short = 0
    var baseIndex = 0
    var numTriangles = 0
    var disabled = false
    var correspondClothSectionIndex: Short = 0
    var generateUpToLodIndex = 0
    // Data from FSkelMeshChunk, appeared in FSkelMeshSection after UE4.13
    var numVertices = 0
    var baseVertexIndex = 0u
    var softVertices = emptyArray<FSoftVertex>()
    var boneMap = UShortArray(0)
    var maxBoneInfluences = 0
    var hasClothData = false
    // UE4.14
    var castShadow = false

    constructor()

    constructor(Ar: FArchive) {
        val stripFlags = FStripDataFlags(Ar)
        val skelMeshVer = FSkeletalMeshCustomVersion.get(Ar)

        materialIndex = Ar.readInt16()
        if (skelMeshVer < FSkeletalMeshCustomVersion.CombineSectionWithChunk) {
            Ar.skip(2) // ChunkIndex
        }
        if (!stripFlags.isDataStrippedForServer()) {
            baseIndex = Ar.readInt32()
            numTriangles = Ar.readInt32()
        }
        if (skelMeshVer < FSkeletalMeshCustomVersion.RemoveTriangleSorting) {
            Ar.skip(1) // TEnumAsByte<ETriangleSortOption>
        }
        if (Ar.ver >= VER_UE4_APEX_CLOTH) {
            if (skelMeshVer < FSkeletalMeshCustomVersion.DeprecateSectionDisabledFlag) {
                disabled = Ar.readBoolean()
            }
            if (skelMeshVer < FSkeletalMeshCustomVersion.RemoveDuplicatedClothingSections) {
                correspondClothSectionIndex = Ar.readInt16()
            }
        }
        if (Ar.ver >= VER_UE4_APEX_CLOTH_LOD) {
            Ar.skip(1) // bEnableClothLOD_DEPRECATED
        }
        if (FRecomputeTangentCustomVersion.get(Ar) >= FRecomputeTangentCustomVersion.RuntimeRecomputeTangent) {
            val recomputeTangent = Ar.readBoolean()
        }
        if (FRecomputeTangentCustomVersion.get(Ar) >= FRecomputeTangentCustomVersion.RecomputeTangentVertexColorMask) {
            Ar.skip(1) // RecomputeTangentsVertexMaskChannel
        }
        hasClothData = false
        if (skelMeshVer >= FSkeletalMeshCustomVersion.CombineSectionWithChunk) {
            if (!stripFlags.isDataStrippedForServer()) {
                baseVertexIndex = Ar.readUInt32()
            }
            if (!stripFlags.isEditorDataStripped()) {
                if (skelMeshVer < FSkeletalMeshCustomVersion.CombineSoftAndRigidVerts) {
                    val rigidVertices = Ar.readTArray { FRigidVertex(Ar) }
                }
                softVertices = Ar.readTArray { FSoftVertex(Ar) }
            }
            boneMap = UShortArray(Ar.readInt32()) { Ar.readUInt16() }
            if (skelMeshVer >= FSkeletalMeshCustomVersion.SaveNumVertices) {
                numVertices = Ar.readInt32()
            }
            if (skelMeshVer < FSkeletalMeshCustomVersion.CombineSoftAndRigidVerts) {
                Ar.skip(8) // NumRigidVerts, NumSoftVerts
            }
            maxBoneInfluences = Ar.readInt32()
            val clothMappingData = Ar.readTArray { FMeshToMeshVertData(Ar) }
            if (skelMeshVer < FSkeletalMeshCustomVersion.RemoveDuplicatedClothingSections) {
                val physicalMeshVertices = Ar.readTArray { FVector(Ar) }
                val physicalMeshNormals = Ar.readTArray { FVector(Ar) }
            }
            val correspondClothAssetIndex = Ar.readInt16()
            if (skelMeshVer < FSkeletalMeshCustomVersion.NewClothingSystemAdded) {
                val clothAssetSubmeshIndex = Ar.readInt16()
            } else {
                // UE4.16+
                val clothingData = Ar.readTArray { FClothingSectionData(Ar) }
            }
            hasClothData = clothMappingData.isNotEmpty()
            if (FOverlappingVerticesCustomVersion.get(Ar) >= FOverlappingVerticesCustomVersion.DetectOVerlappingVertices) {
                val overlappingVertices = Ar.readTMap { Ar.readInt32() to Ar.readTArray { Ar.readInt32() } }
            }
            if (FReleaseObjectVersion.get(Ar) >= FReleaseObjectVersion.AddSkeletalMeshSectionDisable) {
                disabled = Ar.readBoolean()
            }
            if (FSkeletalMeshCustomVersion.get(Ar) >= FSkeletalMeshCustomVersion.SectionIgnoreByReduceAdded) {
                generateUpToLodIndex = Ar.readInt32()
            }
        }
    }

    fun serializeRenderItem(Ar: FArchive) {
        val stripFlags = FStripDataFlags(Ar)

        materialIndex = Ar.readInt16()
        baseIndex = Ar.readInt32()
        numTriangles = Ar.readInt32()

        val recomputeTangent = Ar.readBoolean()
        if (FRecomputeTangentCustomVersion.get(Ar) >= FRecomputeTangentCustomVersion.RecomputeTangentVertexColorMask) {
            Ar.skip(1) // RecomputeTangentsVertexMaskChannel
        }

        castShadow = Ar.readBoolean()
        baseVertexIndex = Ar.readUInt32()

        val clothMappingData = Ar.readTArray { FMeshToMeshVertData(Ar) }
        hasClothData = clothMappingData.isNotEmpty()

        boneMap = UShortArray(Ar.readInt32()) { Ar.readUInt16() }
        numVertices = Ar.readInt32()
        maxBoneInfluences = Ar.readInt32()

        val correspondClothAssetIndex = Ar.readInt16()
        val clothingData = FClothingSectionData(Ar)

        if (Ar.game <= GAME_UE4(23) || !stripFlags.isClassDataStripped(1u)) { // DuplicatedVertices, introduced in UE4.23
            Ar.skipFixedArray(4) // DupVertData
            Ar.skipFixedArray(4 + 4) // DupVertIndexData
        }
        if (FReleaseObjectVersion.get(Ar) >= FReleaseObjectVersion.AddSkeletalMeshSectionDisable) {
            disabled = Ar.readBoolean()
        }
    }
}