@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package me.fungames.jfortniteparse.converters.ue4.meshes

import glm_.vec3.Vec3
import glm_.vec4.Vec4
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.objects.FColor
import me.fungames.jfortniteparse.ue4.assets.objects.FPackedNormal
import me.fungames.jfortniteparse.ue4.assets.objects.meshes.FMeshUVFloat
import kotlin.math.round
import kotlin.properties.Delegates

internal const val MAX_MESH_UV_SETS = 8

class CMeshSection(material : UMaterialInterface?, firstIndex : Int, numFaces : Int)

class CMeshUVFloat(var u : Float, var v : Float) {
    constructor() : this(0f, 0f)
    constructor(other : FMeshUVFloat) : this(other.u, other.v)
}

class CIndexBuffer(indices16: Array<UShort>, indices32: Array<UInt>) {
    val indices16 : Array<UShort>
    val indices32 : Array<UInt>
    val is32Bit = indices32.isNotEmpty()
    val size = if (is32Bit) indices32.size else indices16.size

    init {
        if (indices32.isNotEmpty()) {
            this.indices32 = indices32
            this.indices16 = emptyArray()
        } else {
            this.indices16 = indices16
            this.indices32 = indices32
        }
    }
}

data class CPackedNormal(var data : UInt) {

    constructor() : this(0u)
    constructor(other : FPackedNormal) : this(other.data xor 0x80808080u) // offset by 128

    fun setW(value : Float) {
        data = (data and 0xFFFFFFu) or (round(value * 127.0f).toUInt() shl 24)
    }
    fun getW() = (data shr 24).toByte() / 127.0f
}

open class CBaseMeshLod {
    // generic properties
    var numTexCoords by Delegates.notNull<Int>()
    var hasNormals by Delegates.notNull<Boolean>()
    var hasTangents by Delegates.notNull<Boolean>()
    // geometry
    lateinit var sections : Array<CMeshSection>
    var numVerts by Delegates.notNull<Int>()
    lateinit var extraUV : Array<Array<CMeshUVFloat>>
    lateinit var vertexColors : Array<FColor>
    lateinit var indices : CIndexBuffer

    fun allocateUVBuffers() {
        extraUV = Array(MAX_MESH_UV_SETS - 1) {
            Array(numVerts) { CMeshUVFloat() }
        }
    }

    fun allocateVertexColorBuffer() {
        vertexColors = Array(numVerts) { FColor() }
    }
}

open class CMeshVertex(var position : Vec4, var normal : CPackedNormal, var tangent : CPackedNormal, var uv : CMeshUVFloat)

internal fun unpackNormals(srcNormal : Array<FPackedNormal>, v : CMeshVertex) {
    // tangents: convert to FVector (unpack) then cast to CVec3
    v.tangent = CPackedNormal(srcNormal[0])
    v.normal = CPackedNormal(srcNormal[2])

    // new UE3 version - binormal is not serialized and restored in vertex shader

    if (srcNormal[1].data != 0u) {
        throw ParserException("Not implemented: Should only be used in UE3")
    }
}