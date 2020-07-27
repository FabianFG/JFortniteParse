package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.rendercore.FPackedNormal
import me.fungames.jfortniteparse.ue4.objects.rendercore.FPackedRGBA16N
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FStaticMeshUVItem : UClass {
    companion object {
        fun serializeTangents(Ar: FArchive, useHighPrecisionTangents: Boolean): Array<FPackedNormal> {
            return if (!useHighPrecisionTangents) {
                arrayOf(FPackedNormal(Ar), FPackedNormal(0u), FPackedNormal(Ar)) // TangentX and TangentZ
            } else {
                val normal = FPackedRGBA16N(Ar)
                val tangent = FPackedRGBA16N(Ar)
                arrayOf(normal.toPackedNormal(), FPackedNormal(0u), tangent.toPackedNormal())
            }
        }

        fun serializeTexcoords(Ar: FArchive, numStaticUVSets: Int, useStaticFloatUVs: Boolean): Array<FMeshUVFloat> {
            return if (useStaticFloatUVs)
                Array(numStaticUVSets) { FMeshUVFloat(Ar) }
            else
                Array(numStaticUVSets) {
                    val half = FMeshUVHalf(Ar)
                    half.toMeshUVFloat()
                }
        }
    }

    var normal: Array<FPackedNormal>
    var uv: Array<FMeshUVFloat>

    constructor(Ar: FArchive, useHighPrecisionTangents: Boolean, numStaticUVSets: Int, useStaticFloatUVs: Boolean) {
        super.init(Ar)
        //Serialize Tangents
        normal = serializeTangents(Ar, useHighPrecisionTangents)
        //Serialize Texcoords
        uv = serializeTexcoords(Ar, numStaticUVSets, useStaticFloatUVs)
        super.complete(Ar)
    }

    constructor(normal: Array<FPackedNormal>, uv: Array<FMeshUVFloat>) {
        this.normal = normal
        this.uv = uv
    }
}