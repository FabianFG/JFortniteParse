@file:Suppress("EXPERIMENTAL_UNSIGNED_LITERALS", "EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.objects.FByteBulkData
import me.fungames.jfortniteparse.ue4.assets.objects.FDistanceFieldVolumeData
import me.fungames.jfortniteparse.ue4.assets.objects.FStripDataFlags
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_FTEXT_HISTORY
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_RENAME_CROUCHMOVESCHARACTERDOWN
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_SOUND_CONCURRENCY_PACKAGE

internal val CDSF_AdjancencyData : UByte = 1u
// UE4.20+
internal val CDSF_MinLodData : UByte = 2u
internal val CDSF_ReversedIndexBuffer : UByte = 4u
internal val CDSF_RaytracingResources : UByte = 8u

@ExperimentalUnsignedTypes
class FStaticMeshLODResources : UClass {
    var stripFlags : FStripDataFlags
    var sections: Array<FStaticMeshSection>
    var vertexBuffer = FStaticMeshVertexBuffer(FStripDataFlags(0u, 0u), 0, 0, 0, false, false, emptyArray())
    var positionVertexBuffer = FPositionVertexBuffer(emptyArray(), 0, 0)
    var colorVertexBuffer = FColorVertexBuffer(FStripDataFlags(0u, 0u), 0, 0, emptyArray())
    var indexBuffer = FRawStaticIndexBuffer()
    var reversedIndexBuffer = FRawStaticIndexBuffer()
    var depthOnlyIndexBuffer = FRawStaticIndexBuffer()
    var reversedDepthOnlyIndexBuffer = FRawStaticIndexBuffer()
    var wireframeIndexBuffer = FRawStaticIndexBuffer()
    var adjacencyIndexBuffer = FRawStaticIndexBuffer()
    var maxDeviation : Float
    var isLODCookedOut = false
    var inlined = false

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        stripFlags = FStripDataFlags(Ar)
        sections = Ar.readTArray { FStaticMeshSection(Ar) }
        maxDeviation = Ar.readFloat32()

        if (Ar.game < GAME_UE4(23)) {
            if (!stripFlags.isDataStrippedForServer() && !stripFlags.isClassDataStripped(CDSF_MinLodData))
                serializeBuffersLegacy(Ar)
            return
        }

        // UE4.23+
        isLODCookedOut = Ar.readBoolean()
        inlined = Ar.readBoolean()

        if (!stripFlags.isDataStrippedForServer() && !isLODCookedOut) {
            if (inlined) {
                serializeBuffers(Ar)
            } else {
                val bulk = FByteBulkData(Ar)
                if (bulk.header.elementCount > 0) {
                    val tempAr = FByteArchive(bulk.data)
                    tempAr.littleEndian = Ar.littleEndian
                    tempAr.game = Ar.game
                    tempAr.ver = Ar.ver
                    serializeBuffers(tempAr)
                }

                // FStaticMeshLODResources::SerializeAvailabilityInfo()
                Ar.readUInt32() // DepthOnlyNumTriangles
                Ar.readUInt32() // PackedData

                // ... SerializeMetaData() for all arrays
                Ar.seek(Ar.pos() + 4*4 + 2*4 + 2*4 + 6*(2*4));
/*				StaticMeshVertexBuffer = 2x int32, 2x bool
				PositionVertexBuffer = 2x int32
				ColorVertexBuffer = 2x int32
				IndexBuffer = int32 + bool
				ReversedIndexBuffer
				DepthOnlyIndexBuffer
				ReversedDepthOnlyIndexBuffer
				WireframeIndexBuffer
				AdjacencyIndexBuffer */
            }
        }
        // FStaticMeshBuffersSize
        Ar.readUInt32() // SerializedBuffersSize
        Ar.readUInt32() // DepthOnlyIBSize
        Ar.readUInt32() // ReversedIBsSize
        super.complete(Ar)
    }

    private fun serializeBuffersLegacy(Ar: FArchive) {
        positionVertexBuffer = FPositionVertexBuffer(Ar)
        vertexBuffer = FStaticMeshVertexBuffer(Ar)
        colorVertexBuffer = FColorVertexBuffer(Ar)
        indexBuffer = FRawStaticIndexBuffer(Ar)

        if (Ar.ver >= VER_UE4_SOUND_CONCURRENCY_PACKAGE && !stripFlags.isClassDataStripped(CDSF_ReversedIndexBuffer)) {
            reversedIndexBuffer = FRawStaticIndexBuffer(Ar)
            depthOnlyIndexBuffer = FRawStaticIndexBuffer(Ar)
            reversedDepthOnlyIndexBuffer = FRawStaticIndexBuffer(Ar)
        } else {
            // UE4.8 or older, or when has CDSF_ReversedIndexBuffer
            depthOnlyIndexBuffer = FRawStaticIndexBuffer(Ar)
        }

        if (Ar.ver in VER_UE4_FTEXT_HISTORY until VER_UE4_RENAME_CROUCHMOVESCHARACTERDOWN) {
            FDistanceFieldVolumeData(Ar) // distanceFieldData
        }

        if (!stripFlags.isEditorDataStripped())
            wireframeIndexBuffer = FRawStaticIndexBuffer(Ar)

        if (!stripFlags.isClassDataStripped(CDSF_AdjancencyData))
            adjacencyIndexBuffer = FRawStaticIndexBuffer(Ar)

        if (Ar.game >= GAME_UE4(16)) {
            // AreaWeightedSectionSamplers
            for (i in sections.indices)
                FStaticMeshSectionAreaWeightedTriangleSampler(Ar)
            FStaticMeshAreaWeightedSectionSampler(Ar) // AreaWeightedSampler
        }
    }

    private fun serializeBuffers(Ar: FArchive) {
        val stripFlags = FStripDataFlags(Ar)
        positionVertexBuffer = FPositionVertexBuffer(Ar)
        vertexBuffer = FStaticMeshVertexBuffer(Ar)
        colorVertexBuffer = FColorVertexBuffer(Ar)
        indexBuffer = FRawStaticIndexBuffer(Ar)

        if (!stripFlags.isClassDataStripped(CDSF_ReversedIndexBuffer))
            reversedIndexBuffer = FRawStaticIndexBuffer(Ar)

        depthOnlyIndexBuffer = FRawStaticIndexBuffer(Ar)
        if (!stripFlags.isClassDataStripped(CDSF_ReversedIndexBuffer))
            reversedDepthOnlyIndexBuffer = FRawStaticIndexBuffer(Ar)

        if (!stripFlags.isEditorDataStripped())
            wireframeIndexBuffer = FRawStaticIndexBuffer(Ar)

        if (!stripFlags.isClassDataStripped(CDSF_AdjancencyData))
            adjacencyIndexBuffer = FRawStaticIndexBuffer(Ar)

        // UE4.25+
        if (Ar.game >= GAME_UE4(25) && !stripFlags.isClassDataStripped(CDSF_RaytracingResources))
            Ar.readBulkTArray { Ar.readUInt8() } // Raw data

        // AreaWeightedSectionSamplers
        for (i in sections.indices)
            FStaticMeshSectionAreaWeightedTriangleSampler(Ar)
        FStaticMeshAreaWeightedSectionSampler(Ar) // AreaWeightedSampler
    }
}