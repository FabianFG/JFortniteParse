@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.meshes.psk

import me.fungames.jfortniteparse.converters.ue4.MaterialExport
import me.fungames.jfortniteparse.converters.ue4.meshes.CStaticMesh
import me.fungames.jfortniteparse.converters.ue4.meshes.CStaticMeshLod
import me.fungames.jfortniteparse.converters.ue4.meshes.CVertexShare
import me.fungames.jfortniteparse.converters.ue4.meshes.psk.common.VChunkHeader
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class StaticMeshExport(val fileName : String, val pskx : ByteArray, val materials: MutableList<MaterialExport>) {
    fun writeToDir(dir : File) {
        dir.mkdirs()
        File(dir.absolutePath + "/$fileName").writeBytes(pskx)
        materials.forEach { it.writeToDir(dir) }
    }

    fun appendToZip(zos : ZipOutputStream) {
        runCatching {
            val mat = ZipEntry(fileName)
            zos.putNextEntry(mat)
            zos.write(pskx)
            zos.flush()
            zos.closeEntry()
        }
        materials.forEach { it.appendToZip(zos) }
    }

    fun toZip() : ByteArray {
        val bos = ByteArrayOutputStream()
        val zos = ZipOutputStream(bos)
        zos.setMethod(ZipOutputStream.DEFLATED)
        appendToZip(zos)
        zos.close()
        return bos.toByteArray()
    }
}

fun CStaticMesh.export(exportLods : Boolean = false, exportMaterials : Boolean = true) = exportLods(exportLods, exportMaterials).firstOrNull()

fun CStaticMesh.exportLods(exportLods : Boolean = false, exportMaterials : Boolean = true) : List<StaticMeshExport> {
    if (lods.isEmpty()) {
        UClass.logger.warn { "Mesh ${originalMesh.name} has 0 lods" }
        return emptyList()
    }

    val exports = mutableListOf<StaticMeshExport>()
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

        val materialExports = if (exportMaterials) mutableListOf<MaterialExport>() else null

        exportStaticMeshLod(lods[lod], writer, materialExports)

        exports.add(StaticMeshExport(fileName, writer.toByteArray(), materialExports ?: mutableListOf()))
    }
    return exports
}

private fun exportStaticMeshLod(lod : CStaticMeshLod, Ar : FArchiveWriter, materialExports : MutableList<MaterialExport>?) {
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
        share,
        materialExports
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