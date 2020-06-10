@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.converters.ue4.meshes

import glm_.vec4.Vec4
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UStaticMesh
import me.fungames.jfortniteparse.ue4.assets.objects.FBox
import me.fungames.jfortniteparse.ue4.assets.objects.FSphere


class CStaticMesh(val originalMesh : UObject, val boundingBox : FBox, val boundingSphere : FSphere, val lods : Array<CStaticMeshLod>)

class CStaticMeshLod : CBaseMeshLod() {
    lateinit var verts : CStaticMeshVertex
}

class CStaticMeshVertex(position: Vec4, normal: CPackedNormal, tangent: CPackedNormal, uv: CMeshUVFloat) :
    CMeshVertex(position, normal, tangent, uv)

fun UStaticMesh.convertMesh() {

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
            UClass.logger.debug { "Lod $lodIndex is stripped, skipping..." }
            continue
        }

        if (numTexCoords > MAX_MESH_UV_SETS)
            throw ParserException("Static mesh has too many UV sets ($numTexCoords)")

        val lod = CStaticMeshLod()
        lod.numTexCoords = numTexCoords
        lod.hasNormals = true
        lod.hasTangents = true

        // sections
        val sections = mutableListOf<CMeshSection>()
        for (src in srcLod.sections) {
            val material = staticMaterials.getOrNull(src.materialIndex)
        }
    }

}

