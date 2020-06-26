package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.objects.FStripDataFlags
import me.fungames.jfortniteparse.ue4.assets.objects.FVector
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FStaticMeshVertexBuffer : UClass {
    var stripFlags : FStripDataFlags
    var numTexCoords : Int
    var stride : Int
    var numVertices : Int
    var useFullPrecisionUVs : Boolean
    var useHighPrecisionTangentBasis : Boolean
    var uv : Array<FStaticMeshUVItem>

    constructor(Ar : FArchive) {
        super.init(Ar)

        stripFlags = FStripDataFlags(Ar, VER_UE4_STATIC_SKELETAL_MESH_SERIALIZATION_FIX)
        numTexCoords = Ar.readInt32()
        stride = if (Ar.game < GAME_UE4(19))
            Ar.readInt32()
        else
            -1
        numVertices = Ar.readInt32()
        useFullPrecisionUVs = Ar.readBoolean()
        useHighPrecisionTangentBasis = if (Ar.game >= GAME_UE4(12))
            Ar.readBoolean()
        else
            false

        if (!stripFlags.isDataStrippedForServer()) {
            if (Ar.game < GAME_UE4(19))
                uv = Ar.readBulkTArray { FStaticMeshUVItem(Ar, useHighPrecisionTangentBasis, numTexCoords, useFullPrecisionUVs) }
            else {
                // Tangents: simulate TArray::BulkSerialize()
                var itemSize = Ar.readInt32()
                var itemCount = Ar.readInt32()
                if (itemCount != numVertices)
                    throw ParserException("FStaticMeshVertexBuffer: item count ($itemCount) != num vertices ($numVertices)")
                var pos = Ar.pos()
                uv = Array(numVertices) { FStaticMeshUVItem(FStaticMeshUVItem.serializeTangents(Ar, useHighPrecisionTangentBasis), emptyArray()) }
                if (Ar.pos() - pos != itemCount * itemSize)
                    throw ParserException("FStaticMeshVertexBuffer: read wrong amount of tangent bytes: ${Ar.pos() - pos}, should be ${itemCount * itemSize}")

                // Texture coordinates: simulate TArray::BulkSerialize()
                itemSize = Ar.readInt32()
                itemCount = Ar.readInt32()
                if (itemCount != numVertices * numTexCoords)
                    throw ParserException("FStaticMeshVertexBuffer: item count ($itemCount) != num vertices * num tex coords (${numVertices * numTexCoords})")
                pos = Ar.pos()
                for (i in 0 until numVertices)
                    uv[i].uv = FStaticMeshUVItem.serializeTexcoords(Ar, numTexCoords, useFullPrecisionUVs)
                if (Ar.pos() - pos != itemCount * itemSize)
                    throw ParserException("FStaticMeshVertexBuffer: read wrong amount of texcoord bytes: ${Ar.pos() - pos}, should be ${itemCount * itemSize}")
            }
        } else {
            uv = emptyArray()
        }

        super.complete(Ar)
    }

    constructor(
        stripFlags: FStripDataFlags,
        numTexCoords: Int,
        stride: Int,
        numVertices: Int,
        useFullPrecisionUVs: Boolean,
        useHighPrecisionTangentBasis: Boolean,
        uv: Array<FStaticMeshUVItem>
    ) : super() {
        this.stripFlags = stripFlags
        this.numTexCoords = numTexCoords
        this.stride = stride
        this.numVertices = numVertices
        this.useFullPrecisionUVs = useFullPrecisionUVs
        this.useHighPrecisionTangentBasis = useHighPrecisionTangentBasis
        this.uv = uv
    }
}