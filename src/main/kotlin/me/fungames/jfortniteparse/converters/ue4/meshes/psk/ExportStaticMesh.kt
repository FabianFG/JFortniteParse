@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.meshes.psk

import me.fungames.jfortniteparse.converters.ue4.meshes.CStaticMesh
import me.fungames.jfortniteparse.converters.ue4.meshes.CStaticMeshLod
import me.fungames.jfortniteparse.converters.ue4.meshes.CVertexShare
import me.fungames.jfortniteparse.converters.ue4.meshes.psk.common.VChunkHeader
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.io.File

fun CStaticMesh.export(exportLods : Boolean) {
    if (lods.isEmpty()) {
        UClass.logger.warn { "Mesh ${originalMesh.name} has 0 lods" }
        return
    }

    val maxLod = if (exportLods) lods.size else 1
    for (lod in 0 until maxLod) {
        if (lods[lod].sections.isEmpty()) {
            UClass.logger.warn { "Mesh ${originalMesh.name} Lod $lod has no sections" }
            continue
        }
        val fileName = if (lod == 0)
            "${originalMesh.name}.pskx"
        else
            "${originalMesh.name}_Lod$lod.pskx"

        val writer = FByteArchiveWriter()
        writer.ver = 128 // less than UE3 version (required at least for VJointPos structure)

        exportStaticMeshLod(lods[lod], writer)

        File(fileName).writeBytes(writer.toByteArray())
    }
}

private fun exportStaticMeshLod(lod : CStaticMeshLod, Ar : FArchiveWriter) {
    val share = CVertexShare()

    val boneHdr = VChunkHeader()
    val infHdr = VChunkHeader()

    share.prepare(lod.verts)
    for (s in lod.verts)
        share.addVertex(s.position, s.normal)

    exportCommonMeshData(
        Ar,
        lod.sections,
        lod.verts,
        lod.indices,
        share
    )

    boneHdr.dataCount = 0		// dummy ...
    boneHdr.dataSize = 120      // sizeof(VBone)
    Ar.saveChunkHeader(boneHdr, "REFSKELT")

    infHdr.dataCount = 0		// dummy ...
    infHdr.dataSize = 12        // sizeof(VBone)
    Ar.saveChunkHeader(infHdr, "RAWWEIGHTS")

    exportVertexColors(Ar, lod.vertexColors, lod.numVerts)
    exportExtraUV(Ar, lod.extraUV, lod.numVerts, lod.numTexCoords)
}