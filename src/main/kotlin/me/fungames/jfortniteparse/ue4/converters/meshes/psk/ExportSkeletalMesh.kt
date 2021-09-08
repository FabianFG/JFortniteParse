package me.fungames.jfortniteparse.ue4.converters.meshes.psk

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.converters.MaterialExport
import me.fungames.jfortniteparse.ue4.converters.meshes.CSkelMeshBone
import me.fungames.jfortniteparse.ue4.converters.meshes.CSkelMeshLod
import me.fungames.jfortniteparse.ue4.converters.meshes.CSkeletalMesh
import me.fungames.jfortniteparse.ue4.converters.meshes.CVertexShare
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.common.VChunkHeader
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk.VBone
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk.VJointPosPsk
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

fun CSkeletalMesh.export(exportLods: Boolean = false, exportMaterials: Boolean = true) = exportLods(exportLods, exportMaterials).firstOrNull()

fun CSkeletalMesh.exportLods(exportLods: Boolean = false, exportMaterials: Boolean = true): List<MeshExport> {
    if (lods.isEmpty()) {
        LOG_JFP.warn { "Mesh ${originalMesh.name} has 0 lods" }
        return emptyList()
    }

    val exports = mutableListOf<MeshExport>()
    val maxLod = if (exportLods) lods.size else 1
    for (lod in 0 until maxLod) {
        if (lods[lod].skipLod) {
            LOG_JFP.warn { "LOD $lod in mesh '${originalMesh.name}' should be skipped" }
            continue
        }
        val usePskx = lods[lod].numVerts > 65536
        val extension = if (usePskx) "pskx" else "psk"
        val fileName = if (lod == 0)
            "${originalMesh.name}.$extension"
        else
            "${originalMesh.name}_Lod$lod.$extension"

        val writer = FByteArchiveWriter()
        writer.ver = 128 // less than UE3 version (required at least for VJointPos structure)

        val materialExports = if (exportMaterials) mutableListOf<MaterialExport>() else null

        exportSkeletalMeshLod(lods[lod], refSkeleton, writer, materialExports)

        exports.add(MeshExport(fileName, writer.toByteArray(), materialExports ?: mutableListOf()))
    }
    return exports
}

private fun exportSkeletalMeshLod(lod: CSkelMeshLod, bones: List<CSkelMeshBone>, Ar: FArchiveWriter, materialExports: MutableList<MaterialExport>?) {
    val share = CVertexShare()
    val boneHdr = VChunkHeader()
    val infHdr = VChunkHeader()

    share.prepare(lod.verts)
    for (vert in lod.verts) {
        var weightsHash = vert.packedWeights
        vert.bone.forEachIndexed { i, it ->
            weightsHash = weightsHash xor (it.toUInt() shl i)
        }

        share.addVertex(vert.position, vert.normal, weightsHash)
    }

    exportCommonMeshData(Ar, lod.sections, lod.verts, lod.indices, share, materialExports)

    val numBones = bones.size
    boneHdr.dataCount = numBones
    boneHdr.dataSize = 120
    Ar.saveChunkHeader(boneHdr, "REFSKELT")
    repeat(numBones) { i ->
        var numChildren = 0
        repeat(numBones) { j ->
            if (j != i && bones[j].parentIndex == i)
                numChildren++
        }

        val bone = VBone(
            bones[i].name.text,
            0u,
            numChildren,
            bones[i].parentIndex,
            VJointPosPsk(
                bones[i].orientation,
                bones[i].position,
                0f,
                FVector()
            )
        )

        // MIRROR_MESH
        bone.bonePos.orientation.y *= -1
        bone.bonePos.orientation.w *= -1
        bone.bonePos.position.y *= -1

        bone.serialize(Ar)
    }

    var numInfluences = 0
    for (i in 0 until share.points.size) {
        for (j in 0 until 4) {
            if (lod.verts[share.vertToWedge[i]].bone[j] < 0) {
                break
            }
            numInfluences++
        }
    }
    infHdr.dataCount = numInfluences
    infHdr.dataSize = 12
    Ar.saveChunkHeader(infHdr, "RAWWEIGHTS")
    for (i in 0 until share.points.size) {
        val v = lod.verts[share.vertToWedge[i]]
        val unpackedWeights = v.unpackWeights()

        for (j in 0 until 4) {
            if (v.bone[j] < 0) {
                break
            }
            Ar.writeFloat32(unpackedWeights[j])
            Ar.writeInt32(i)
            Ar.writeInt32(v.bone[j].toInt())
        }
    }

    exportVertexColors(Ar, lod.vertexColors, lod.numVerts)
    exportExtraUV(Ar, lod.extraUV, lod.numVerts, lod.numTexCoords)
}