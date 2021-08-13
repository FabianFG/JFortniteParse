package me.fungames.jfortniteparse.ue4.converters.meshes

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.exports.USkeletalMesh
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FMeshUVFloat
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FSkelMeshVertexBase
import me.fungames.jfortniteparse.ue4.objects.core.math.FBox
import me.fungames.jfortniteparse.ue4.objects.core.math.FQuat
import me.fungames.jfortniteparse.ue4.objects.core.math.FSphere
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

class CSkeletalMesh(val originalMesh: USkeletalMesh, val boundingBox: FBox, val boundingSphere: FSphere, val lods: List<CSkelMeshLod>, val refSkeleton: List<CSkelMeshBone>) {
    internal fun finalizeMesh() {
        lods.forEach { it.buildNormals() }
    }
}

class CSkelMeshLod : CBaseMeshLod() {
    var verts = emptyArray<CSkelMeshVertex>()

    fun allocateVerts(count: Int) {
        verts = Array(count) { CSkelMeshVertex(FVector(), CPackedNormal(), CPackedNormal(), FMeshUVFloat()) }
        numVerts = count
        allocateUVBuffers()
    }

    fun buildNormals() {
        if (hasNormals) return
        //buildNormalsCommon(verts, indices)
        hasNormals = true
    }
}

class CSkelMeshVertex : CMeshVertex {
    var packedWeights = 0u
    var bone: ShortArray

    constructor(position: FVector, normal: CPackedNormal, tangent: CPackedNormal, uv: FMeshUVFloat) : super(position, normal, tangent, uv) {
        bone = ShortArray(4)
    }

    fun unpackWeights(): FloatArray {
        val ret = FloatArray(4)
        val scale = 1.0f / 255
        ret[0] = (packedWeights        and 0xFFu).toInt() * scale
        ret[1] = (packedWeights shr 8  and 0xFFu).toInt() * scale
        ret[2] = (packedWeights shr 16 and 0xFFu).toInt() * scale
        ret[3] = (packedWeights shr 24 and 0xFFu).toInt() * scale
        return ret
    }
}

class CSkelMeshBone(val name: FName, val parentIndex: Int, val position: FVector, val orientation: FQuat)

fun USkeletalMesh.convertMesh(): CSkeletalMesh {
    val boundingSphere = FSphere(0f, 0f, 0f, importedBounds.sphereRadius / 2)
    val boundingBox = FBox(importedBounds.origin - importedBounds.boxExtent, importedBounds.origin + importedBounds.boxExtent)

    val lods = mutableListOf<CSkelMeshLod>()
    for (srcLod in lodModels) {
        /*if (srcLod.skipLod) {
            continue
        }*/

        val numTexCoords = srcLod.numTexCoords
        if (numTexCoords > MAX_MESH_UV_SETS) {
            throw ParserException("Skeletal mesh has too many UV sets ($numTexCoords)")
        }

        val skeletalMeshLod = CSkelMeshLod().apply {
            this.numTexCoords = numTexCoords
            hasNormals = true
            hasTangents = true
            indices = CIndexBuffer(srcLod.indices.indices16, srcLod.indices.indices32)
            sections = Array(srcLod.sections.size) { j ->
                var materialIndex = srcLod.sections[j].materialIndex
                if (materialIndex < 0) { // UE4 using Clamp(0, Materials.Num()), not Materials.Num()-1
                    materialIndex = 0
                }

                val m = materials.getOrNull(materialIndex.toInt())?.material
                CMeshSection(m, srcLod.sections[j].baseIndex, srcLod.sections[j].numTriangles)
            }
        }

        var useVerticesFromSections = false
        var vertexCount = srcLod.vertexBufferGPUSkin.vertexCount
        if (vertexCount == 0 && srcLod.sections.isNotEmpty() && srcLod.sections[0].softVertices.isNotEmpty()) {
            useVerticesFromSections = true
            for (section in srcLod.sections) {
                vertexCount += section.softVertices.size
            }
        }

        skeletalMeshLod.allocateVerts(vertexCount)

        var chunkIndex = -1
        var chunkVertexIndex = 0
        var lastChunkVertex = -1L
        var boneMap: UShortArray? = null
        val vertBuffer = srcLod.vertexBufferGPUSkin

        if (srcLod.colorVertexBuffer.data.size == vertexCount)
            skeletalMeshLod.allocateVertexColorBuffer()

        for (vert in 0 until vertexCount) {
            while (vert >= lastChunkVertex) { // this will fix any issues with empty chunks or sections
                if (srcLod.chunks.isNotEmpty()) { // proceed to next chunk or section
                    // pre-UE4.13 code: chunks
                    val c = srcLod.chunks[++chunkIndex]
                    lastChunkVertex = (c.baseVertexIndex + c.numRigidVertices + c.numSoftVertices).toLong()
                    boneMap = c.boneMap
                } else {
                    // UE4.13+ code: chunk information migrated to sections
                    val s = srcLod.sections[++chunkIndex]
                    lastChunkVertex = (s.baseVertexIndex.toInt() + s.numVertices).toLong()
                    boneMap = s.boneMap
                }

                chunkVertexIndex = 0
            }

            var v: FSkelMeshVertexBase // has everything but UV[]
            if (useVerticesFromSections) {
                val v0 = srcLod.sections[chunkIndex].softVertices[chunkVertexIndex++]
                v = v0

                skeletalMeshLod.verts[vert].uv = v0.uv[0] // UV: simply copy float data
                for (texCoordIndex in 1 until numTexCoords) {
                    skeletalMeshLod.extraUV[texCoordIndex - 1][vert] = v0.uv[texCoordIndex]
                }
            } else if (!vertBuffer.useFullPrecisionUVs) {
                val v0 = vertBuffer.vertsHalf[vert]
                v = v0

                skeletalMeshLod.verts[vert].uv = v0.uv[0].toMeshUVFloat() // UV: convert half -> float
                for (texCoordIndex in 1 until numTexCoords) {
                    skeletalMeshLod.extraUV[texCoordIndex - 1][vert] = v0.uv[texCoordIndex].toMeshUVFloat()
                }
            } else {
                val v0 = vertBuffer.vertsFloat[vert]
                v = v0

                skeletalMeshLod.verts[vert].uv = v0.uv[0] // UV: simply copy float data
                for (texCoordIndex in 1 until numTexCoords) {
                    skeletalMeshLod.extraUV[texCoordIndex - 1][vert] = v0.uv[texCoordIndex]
                }
            }

            skeletalMeshLod.verts[vert].position = v.pos
            unpackNormals(v.normal, skeletalMeshLod.verts[vert])
            skeletalMeshLod.vertexColors?.set(vert, srcLod.colorVertexBuffer.data[vert])

            var i2 = 0
            var packedWeights = 0u
            for (j in 0 until 4) {
                val boneWeight = v.infs!!.boneWeight[j]
                if (boneWeight == 0.toUByte()) {
                    continue // skip this influence (but do not stop the loop!)
                }
                packedWeights = packedWeights or (boneWeight.toUInt() shl (i2 * 8))
                skeletalMeshLod.verts[vert].bone[i2] = boneMap!![v.infs!!.boneIndex[j].toInt()].toShort()
                i2++
            }

            skeletalMeshLod.verts[vert].packedWeights = packedWeights
            if (i2 < 4) {
                skeletalMeshLod.verts[vert].bone[i2] = -1 // mark end of list
            }
        }

        lods.add(skeletalMeshLod)
    }

    val refSkeleton = mutableListOf<CSkelMeshBone>()
    referenceSkeleton.finalRefBoneInfo.forEachIndexed { i, it ->
        val skeletalMeshBone = CSkelMeshBone(
            it.name, it.parentIndex,
            referenceSkeleton.finalRefBonePose[i].translation,
            referenceSkeleton.finalRefBonePose[i].rotation
        )
        if (i >= 1) { // fix skeleton; all bones but 0
            skeletalMeshBone.orientation.conjugate()
        }
        refSkeleton.add(skeletalMeshBone)
    }

    val mesh = CSkeletalMesh(this, boundingBox, boundingSphere, lods, refSkeleton)
    mesh.finalizeMesh()
    return mesh
}