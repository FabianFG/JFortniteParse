@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package me.fungames.jfortniteparse.converters.ue4.meshes

import glm_.vec4.Vec4
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FColor
import kotlin.math.round
import kotlin.properties.Delegates

internal const val MAX_MESH_UV_SETS = 8

class CMeshSection(material : /*TODO: UUnrealMaterial*/UObject, firstIndex : Int, numFaces : Int)

class CMeshUVFloat(val u : Float, val v : Float)

class CIndexBuffer(val indices16 : Array<UShort> = emptyArray(), val indices32 : Array<UInt> = emptyArray()) {
    val is32Bit = indices32.isNotEmpty()
    val size = if (is32Bit) indices32.size else indices16.size
}

data class CPackedNormal(var data : UInt) {
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
    lateinit var extraUV : Array<CMeshUVFloat>
    lateinit var vertexColors : FColor
    lateinit var indices : CIndexBuffer
}

open class CMeshVertex(val position : Vec4, val normal : CPackedNormal, val tangent : CPackedNormal, val uv : CMeshUVFloat)