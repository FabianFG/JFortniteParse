package me.fungames.jfortniteparse.ue4.converters.meshes

import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.exports.UStaticMesh
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FMeshUVFloat
import me.fungames.jfortniteparse.ue4.objects.core.math.FBox
import me.fungames.jfortniteparse.ue4.objects.core.math.FSphere
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector

class CStaticMesh(val originalMesh: UStaticMesh, val boundingBox: FBox, val boundingSphere: FSphere, val lods: List<CStaticMeshLod>) {
    internal fun finalizeMesh() {
        lods.forEach { it.buildNormals() }
    }
}

class CStaticMeshLod : CBaseMeshLod() {
    var verts = emptyArray<CMeshVertex>()

    fun allocateVerts(count: Int) {
        verts = Array(count) { CStaticMeshVertex(FVector(), CPackedNormal(), CPackedNormal(), FMeshUVFloat()) }
        numVerts = count
        allocateUVBuffers()
    }

    fun buildNormals() {
        if (hasNormals) return
        buildNormalsCommon(verts, indices)
        hasNormals = true
    }
}

class CStaticMeshVertex(position: FVector, normal: CPackedNormal, tangent: CPackedNormal, uv: FMeshUVFloat) : CMeshVertex(position, normal, tangent, uv)

fun UStaticMesh.convertMesh(): CStaticMesh {
    // convert bounds
    val boundingSphere = FSphere(0f, 0f, 0f, bounds.sphereRadius / 2) //?? UE3 meshes has radius 2 times larger than mesh itself; verify for UE4
    val boundingBox = FBox(bounds.origin - bounds.boxExtent, bounds.origin + bounds.boxExtent)

    // convert lods
    val lods = mutableListOf<CStaticMeshLod>()
    for (lodIndex in this.lods.indices) {
        val srcLod = this.lods[lodIndex]

        val numTexCoords = srcLod.vertexBuffer.numTexCoords
        val numVerts = srcLod.positionVertexBuffer.verts.size

        if (numVerts == 0 && numTexCoords == 0 && lodIndex < this.lods.size - 1) {
            LOG_JFP.debug { "Lod $lodIndex is stripped, skipping..." }
            continue
        }

        if (numTexCoords > MAX_MESH_UV_SETS)
            throw ParserException("Static mesh has too many UV sets ($numTexCoords)")

        val lod = CStaticMeshLod()
        lods.add(lod)
        lod.numTexCoords = numTexCoords
        lod.hasNormals = true
        lod.hasTangents = true

        // sections
        val sections = mutableListOf<CMeshSection>()
        for (src in srcLod.sections) {
            val material = materials.getOrNull(src.materialIndex)
            sections.add(CMeshSection(material, src.firstIndex, src.numTriangles))
        }
        lod.sections = sections.toTypedArray()

        // vertices
        lod.allocateVerts(numVerts)
        if (srcLod.colorVertexBuffer.numVertices != 0)
            lod.allocateVertexColorBuffer()
        for (i in 0 until numVerts) {
            val suv = srcLod.vertexBuffer.uv[i]
            val v = lod.verts[i]

            v.position = srcLod.positionVertexBuffer.verts[i].run { FVector(x, y, z) }
            unpackNormals(suv.normal, v)
            // copy UV
            v.uv = suv.uv[0]
            for (texCoordIndex in 1 until numTexCoords) {
                lod.extraUV[texCoordIndex - 1][i].u = suv.uv[texCoordIndex].u
                lod.extraUV[texCoordIndex - 1][i].v = suv.uv[texCoordIndex].v
            }
            if (srcLod.colorVertexBuffer.numVertices != 0)
                lod.vertexColors!![i] = srcLod.colorVertexBuffer.data[i]
        }

        // indices
        lod.indices = CIndexBuffer(srcLod.indexBuffer.indices16, srcLod.indexBuffer.indices32)
    }

    val mesh = CStaticMesh(this, boundingBox, boundingSphere, lods)
    mesh.finalizeMesh()
    return mesh
}