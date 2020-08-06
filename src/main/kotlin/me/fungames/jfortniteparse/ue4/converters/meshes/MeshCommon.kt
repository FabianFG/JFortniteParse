@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package me.fungames.jfortniteparse.ue4.converters.meshes

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FMeshUVFloat
import me.fungames.jfortniteparse.ue4.objects.core.math.FColor
import me.fungames.jfortniteparse.ue4.objects.core.math.FVector
import me.fungames.jfortniteparse.ue4.objects.rendercore.FPackedNormal
import kotlin.math.round

internal const val MAX_MESH_UV_SETS = 8

class CMeshSection(val material: UMaterialInterface?, val firstIndex: Int, val numFaces: Int)

class CMeshUVFloat(var u: Float, var v: Float) {
    constructor() : this(0f, 0f)
    constructor(other: FMeshUVFloat) : this(other.u, other.v)
}

class CIndexBuffer(indices16: Array<UShort>, indices32: Array<UInt>) {
    val indices16: Array<UShort>
    val indices32: Array<UInt>
    val is32Bit = indices32.isNotEmpty()
    val size = if (is32Bit) indices32.size else indices16.size

    class Accessor internal constructor(indexBuffer: CIndexBuffer) {

        private val func = if (indexBuffer.is32Bit) indexBuffer::getIndex32 else indexBuffer::getIndex16

        operator fun get(index: Int) = func(index)
    }

    init {
        if (indices32.isNotEmpty()) {
            this.indices32 = indices32
            this.indices16 = emptyArray()
        } else {
            this.indices16 = indices16
            this.indices32 = indices32
        }
    }

    fun getAccessor() = Accessor(this)

    fun getIndex32(index: Int) = indices32[index].toInt()

    fun getIndex16(index: Int) = indices16[index].toInt()
}

data class CPackedNormal(var data: UInt) {
    constructor() : this(0u)
    constructor(other: FPackedNormal) : this(other.data xor 0x80808080u) // offset by 128

    fun setW(value: Float) {
        data = (data and 0xFFFFFFu) or (round(value * 127.0f).toUInt() shl 24)
    }

    fun getW() = (data shr 24).toByte() / 127.0f
}

open class CBaseMeshLod {
    // generic properties
    var numTexCoords = 0
    var hasNormals = false
    var hasTangents = false
    // geometry
    lateinit var sections: Array<CMeshSection>
    var numVerts = 0
    lateinit var extraUV: Array<Array<CMeshUVFloat>>
    var vertexColors: Array<FColor>? = null
    lateinit var indices: CIndexBuffer

    fun allocateUVBuffers() {
        extraUV = Array(numTexCoords - 1) {
            Array(numVerts) { CMeshUVFloat() }
        }
    }

    fun allocateVertexColorBuffer() {
        vertexColors = Array(numVerts) { FColor() }
    }
}

open class CMeshVertex(var position: FVector, var normal: CPackedNormal, var tangent: CPackedNormal, var uv: CMeshUVFloat)

internal fun unpackNormals(srcNormal: Array<FPackedNormal>, v: CMeshVertex) {
    // tangents: convert to FVector (unpack) then cast to CVec3
    v.tangent = CPackedNormal(srcNormal[0])
    v.normal = CPackedNormal(srcNormal[2])

    // new UE3 version - binormal is not serialized and restored in vertex shader

    if (srcNormal[1].data != 0u) {
        throw ParserException("Not implemented: Should only be used in UE3")
    }
}

internal fun buildNormalsCommon(verts: Array<CMeshVertex>, indices: CIndexBuffer) {
    throw ParserException("Not implemented yet: Build normals common")
}