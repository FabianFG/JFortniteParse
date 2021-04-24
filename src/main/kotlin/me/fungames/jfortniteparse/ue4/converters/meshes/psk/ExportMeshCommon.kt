package me.fungames.jfortniteparse.ue4.converters.meshes.psk

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.converters.MaterialExport
import me.fungames.jfortniteparse.ue4.converters.export
import me.fungames.jfortniteparse.ue4.converters.meshes.*
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.common.VChunkHeader
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk.VMaterial
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk.VMeshUV
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk.VTriangle16
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.psk.VVertex
import me.fungames.jfortniteparse.ue4.converters.meshes.psk.pskx.VTriangle32
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import me.fungames.jfortniteparse.util.MIRROR_MESH

fun exportCommonMeshData(Ar: FArchiveWriter, sections: Array<CMeshSection>, verts: Array<CMeshVertex>, indices: CIndexBuffer, share: CVertexShare, materialExports: MutableList<MaterialExport>?) {
    val mainHdr = VChunkHeader()
    val ptsHdr = VChunkHeader()
    val wedgHdr = VChunkHeader()
    val facesHdr = VChunkHeader()
    val matrHdr = VChunkHeader()

    val numVerts = verts.size
    val numSections = sections.size

    // main psk header
    Ar.saveChunkHeader(mainHdr, "ACTRHEAD")

    ptsHdr.dataCount = share.points.size
    ptsHdr.dataSize = 12 // sizeof(FVector)
    Ar.saveChunkHeader(ptsHdr, "PNTS0000")
    for (v in share.points) {
        if (MIRROR_MESH)
            v.y = -v.y
        v.serialize(Ar)
    }

    // get number of faces (some Gears3 meshes may have index buffer larger than needed)
    // get wedge-material mapping
    var numFaces = 0
    val wedgeMat = IntArray(numVerts)
    val index = indices.getAccessor()
    for (i in sections.indices) {
        val sec = sections[i]
        numFaces += sec.numFaces
        for (j in 0 until sec.numFaces * 3) {
            val idx = index[j + sec.firstIndex]
            wedgeMat[idx] = i
        }
    }

    wedgHdr.dataCount = numVerts
    wedgHdr.dataSize = 16 // sizeof(VVertex)
    Ar.saveChunkHeader(wedgHdr, "VTXW0000")
    for (i in 0 until numVerts) {
        val s = verts[i]
        val w = VVertex(
            share.wedgeToVert[i],
            s.uv.u, s.uv.v,
            wedgeMat[i].toByte(),
            0, 0
        )
        w.serialize(Ar)
    }

    if (numVerts <= 65536) {
        facesHdr.dataCount = numFaces
        facesHdr.dataSize = 12 // sizeof(VTriangle16)
        Ar.saveChunkHeader(facesHdr, "FACE0000")
        for (i in 0 until numSections) {
            val sec = sections[i]
            for (j in 0 until sec.numFaces) {
                val wedgeIndex = UShortArray(3) { k ->
                    val idx = index[sec.firstIndex + j * 3 + k]
                    if (idx < 0 || idx >= 65536)
                        throw ParserException("Invalid index out of range of uint16")
                    idx.toUShort()
                }
                val t = VTriangle16(
                    wedgeIndex,
                    i.toByte(),
                    0,
                    1u
                )
                if (MIRROR_MESH)
                    t.wedgeIndex[0] = t.wedgeIndex[1].also { t.wedgeIndex[1] = t.wedgeIndex[0] }
                t.serialize(Ar)
            }
        }
    } else {
        // pskx extension
        facesHdr.dataCount = numFaces
        facesHdr.dataSize = 18 // sizeof(VTriangle32) without alignment
        Ar.saveChunkHeader(facesHdr, "FACE3200")
        for (i in 0 until numSections) {
            val sec = sections[i]
            for (j in 0 until sec.numFaces) {
                val wedgeIndex = IntArray(3) { k ->
                    index[sec.firstIndex + j * 3 + k]
                }
                val t = VTriangle32(
                    wedgeIndex,
                    i.toByte(),
                    0,
                    1u
                )
                if (MIRROR_MESH)
                    t.wedgeIndex[0] = t.wedgeIndex[1].also { t.wedgeIndex[1] = t.wedgeIndex[0] }
                t.serialize(Ar)
            }
        }
    }

    matrHdr.dataCount = numSections
    matrHdr.dataSize = 88
    Ar.saveChunkHeader(matrHdr, "MATT0000")
    for (i in 0 until numSections) {
        val tex = sections[i].material?.value
        //!! this will not handle (UMaterialWithPolyFlags->Material==NULL) correctly - will make MaterialName=="None"
        val materialName = tex?.name ?: "material_${i}"
        if (tex != null) materialExports?.add(tex.export())
        val m = VMaterial(materialName, i, 0u, 0, 0u, 0, 0)
        m.serialize(Ar)
    }
}

fun exportVertexColors(Ar: FArchiveWriter, colors: Array<FColor>?, numVerts: Int) {
    if (colors == null) return

    val colorHdr = VChunkHeader()
    colorHdr.dataCount = numVerts
    colorHdr.dataSize = 4 // sizeof(FColor)

    Ar.saveChunkHeader(colorHdr, "VERTEXCOLOR")
    for (i in 0 until numVerts)
        colors[i].serialize(Ar)
}

fun exportExtraUV(Ar: FArchiveWriter, extraUV: Array<Array<CMeshUVFloat>>, numVerts: Int, numTexCoords: Int) {
    val uvHdr = VChunkHeader()
    uvHdr.dataCount = numVerts
    uvHdr.dataSize = 8 // sizeof(VMeshUV)

    for (j in 1 until numTexCoords) {
        val chunkName = "EXTRAUVS${j - 1}"
        Ar.saveChunkHeader(uvHdr, chunkName)
        val suv = extraUV[j - 1]
        for (i in 0 until numVerts) {
            val uv = VMeshUV(suv[i].u, suv[i].v)
            uv.serialize(Ar)
        }
    }
}