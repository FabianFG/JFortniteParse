package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.exports.USkeletalMesh
import me.fungames.jfortniteparse.ue4.assets.objects.FByteBulkData
import me.fungames.jfortniteparse.ue4.assets.objects.FIntBulkData
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.*

class FSkeletalMeshLODModel {
    companion object {
        const val CDSF_AdjacencyData: UByte = 1u
        const val CDSF_MinLodData: UByte = 2u
    }

    var sections = emptyArray<FSkelMeshSection>()
    lateinit var indices: FMultisizeIndexContainer
    var activeBoneIndices: ShortArray? = null
    var chunks: Array<FSkelMeshChunk>
    var size = 0
    var numVertices = 0
    var requiredBones: ShortArray? = null
    var rawPointIndices: FIntBulkData? = null
    var meshToImportVertexMap: IntArray
    var maxImportVertex = 0
    var numTexCoords = 0
    lateinit var vertexBufferGPUSkin: FSkeletalMeshVertexBuffer
    lateinit var colorVertexBuffer: FSkeletalMeshVertexColorBuffer
    lateinit var adjacencyIndexBuffer: FMultisizeIndexContainer
    lateinit var clothVertexBuffer: FSkeletalMeshVertexClothBuffer
    val skipLod get() = !::indices.isInitialized || indices.indices16.isEmpty() && indices.indices32.isEmpty()

    constructor() {
        chunks = emptyArray()
        meshToImportVertexMap = IntArray(0)
        colorVertexBuffer = FSkeletalMeshVertexColorBuffer()
    }

    constructor(Ar: FAssetArchive, owner: USkeletalMesh) : this() {
        val stripFlags = FStripDataFlags(Ar)
        val skelMeshVer = FSkeletalMeshCustomVersion.get(Ar)
        sections = Ar.readTArray { FSkelMeshSection(Ar) }
        indices = if (skelMeshVer < FSkeletalMeshCustomVersion.SplitModelAndRenderData) {
            FMultisizeIndexContainer(Ar)
        } else {
            // UE4.19+ uses 32-bit index buffer (for editor data)
            FMultisizeIndexContainer(indices32 = Ar.readBulkTArray { Ar.readUInt32() })
        }
        activeBoneIndices = ShortArray(Ar.readInt32()) { Ar.readInt16() }
        if (skelMeshVer < FSkeletalMeshCustomVersion.CombineSectionWithChunk) {
            chunks = Ar.readTArray { FSkelMeshChunk(Ar) }
        }
        size = Ar.readInt32()
        if (!stripFlags.isDataStrippedForServer()) {
            numVertices = Ar.readInt32()
        }
        requiredBones = ShortArray(Ar.readInt32()) { Ar.readInt16() }
        if (!stripFlags.isEditorDataStripped()) {
            rawPointIndices = FIntBulkData(Ar)
        }
        if (Ar.ver >= VER_UE4_ADD_SKELMESH_MESHTOIMPORTVERTEXMAP) {
            meshToImportVertexMap = IntArray(Ar.readInt32()) { Ar.readInt32() }
            maxImportVertex = Ar.readInt32()
        }
        if (!stripFlags.isDataStrippedForServer()) {
            numTexCoords = Ar.readInt32()
            if (skelMeshVer < FSkeletalMeshCustomVersion.SplitModelAndRenderData) {
                vertexBufferGPUSkin = FSkeletalMeshVertexBuffer(Ar)
                if (skelMeshVer >= FSkeletalMeshCustomVersion.UseSeparateSkinWeightBuffer) {
                    val skinWeights = FSkinWeightVertexBuffer(Ar, vertexBufferGPUSkin.extraBoneInfluences)
                    if (skinWeights.weights.isNotEmpty()) {
                        // Copy data to VertexBufferGPUSkin
                        if (vertexBufferGPUSkin.useFullPrecisionUVs) {
                            repeat(numVertices) {
                                vertexBufferGPUSkin.vertsFloat[it].infs = skinWeights.weights[it]
                            }
                        } else {
                            repeat(numVertices) {
                                vertexBufferGPUSkin.vertsHalf[it].infs = skinWeights.weights[it]
                            }
                        }
                    }
                }
                if (owner.hasVertexColors) {
                    colorVertexBuffer = if (skelMeshVer < FSkeletalMeshCustomVersion.UseSharedColorBufferFormat) {
                        FSkeletalMeshVertexColorBuffer(Ar)
                    } else {
                        val newColorVertexBuffer = FColorVertexBuffer(Ar)
                        FSkeletalMeshVertexColorBuffer(newColorVertexBuffer.data)
                    }
                }
                if (Ar.ver < VER_UE4_REMOVE_EXTRA_SKELMESH_VERTEX_INFLUENCES) {
                    throw ParserException("Unsupported: extra SkelMesh vertex influences (old mesh format)")
                }
                if (!stripFlags.isClassDataStripped(CDSF_AdjacencyData)) {
                    adjacencyIndexBuffer = FMultisizeIndexContainer(Ar)
                }
                if (Ar.ver >= VER_UE4_APEX_CLOTH && hasClothData) {
                    clothVertexBuffer = FSkeletalMeshVertexClothBuffer(Ar)
                }
            }
        }
    }

    fun serializeRenderItem(Ar: FAssetArchive, owner: USkeletalMesh) {
        val stripFlags = FStripDataFlags(Ar)
        val isLodCookedOut = Ar.readBoolean()
        val inlined = Ar.readBoolean()

        requiredBones = ShortArray(Ar.readInt32()) { Ar.readInt16() }
        if (!stripFlags.isDataStrippedForServer() && !isLodCookedOut) {
            sections = Ar.readTArray { FSkelMeshSection().apply { serializeRenderItem(Ar) } }

            activeBoneIndices = ShortArray(Ar.readInt32()) { Ar.readInt16() }
            Ar.skip(4) //val buffersSize = Ar.readUInt32()

            if (inlined) {
                serializeStreamedData(Ar, owner)
            } else {
                val bulk = FByteBulkData(Ar)
                if (bulk.header.elementCount > 0) {
                    val tempAr = FByteArchive(bulk.data, Ar.versions)
                    serializeStreamedData(tempAr, owner)

                    var skipBytes = 5
                    if (FUE5ReleaseStreamObjectVersion.get(Ar) < FUE5ReleaseStreamObjectVersion.RemovingTessellation && !stripFlags.isClassDataStripped(CDSF_AdjacencyData))
                        skipBytes += 5
                    skipBytes += 4 * 4 + 2 * 4 + 2 * 4
                    skipBytes += FSkinWeightVertexBuffer.metadataSize(Ar)
                    Ar.skip(skipBytes.toLong())

                    if (hasClothData) {
                        var clothIndexMapping = LongArray(Ar.readInt32()) { Ar.readInt64() }
                        Ar.skip(2 * 4)
                    }

                    var profileNames = Ar.readTArray { Ar.readFName() }
                }
            }
        }
    }

    fun serializeRenderItemLegacy(Ar: FAssetArchive, owner: USkeletalMesh) {
        val stripFlags = FStripDataFlags(Ar)

        sections = Ar.readTArray { FSkelMeshSection().apply { serializeRenderItem(Ar) } }

        indices = FMultisizeIndexContainer(Ar)
        vertexBufferGPUSkin = FSkeletalMeshVertexBuffer().apply { useFullPrecisionUVs = true }

        activeBoneIndices = ShortArray(Ar.readInt32()) { Ar.readInt16() }
        requiredBones = ShortArray(Ar.readInt32()) { Ar.readInt16() }

        if (!stripFlags.isDataStrippedForServer() && !stripFlags.isClassDataStripped(CDSF_MinLodData)) {
            val positionVertexBuffer = FPositionVertexBuffer(Ar)
            val staticMeshVertexBuffer = FStaticMeshVertexBuffer(Ar)
            val skinWeightVertexBuffer = FSkinWeightVertexBuffer(Ar, vertexBufferGPUSkin.extraBoneInfluences)

            if (owner.hasVertexColors) {
                val newColorVertexBuffer = FColorVertexBuffer(Ar)
                colorVertexBuffer = FSkeletalMeshVertexColorBuffer(newColorVertexBuffer.data)
            }

            if (!stripFlags.isClassDataStripped(CDSF_AdjacencyData))
                adjacencyIndexBuffer = FMultisizeIndexContainer(Ar)

            if (hasClothData)
                clothVertexBuffer = FSkeletalMeshVertexClothBuffer(Ar)

            numVertices = positionVertexBuffer.numVertices
            numTexCoords = staticMeshVertexBuffer.numTexCoords

            vertexBufferGPUSkin.vertsFloat = Array(numVertices) {
                FGPUVertFloat().apply {
                    pos = positionVertexBuffer.verts[it]
                    infs = skinWeightVertexBuffer.weights[it]
                    normal = staticMeshVertexBuffer.uv[it].normal
                    uv = staticMeshVertexBuffer.uv[it].uv
                }
            }
        }

        if (Ar.game >= GAME_UE4(23)) {
            var skinWeightProfilesData = FSkinWeightProfilesData(Ar)
        }
    }

    private fun serializeStreamedData(Ar: FArchive, owner: USkeletalMesh) {
        val stripFlags = FStripDataFlags(Ar)

        indices = FMultisizeIndexContainer(Ar)
        vertexBufferGPUSkin = FSkeletalMeshVertexBuffer().apply { useFullPrecisionUVs = true }

        val positionVertexBuffer = FPositionVertexBuffer(Ar)
        val staticMeshVertexBuffer = FStaticMeshVertexBuffer(Ar)
        val skinWeightVertexBuffer = FSkinWeightVertexBuffer(Ar, vertexBufferGPUSkin.extraBoneInfluences)

        if (owner.hasVertexColors) {
            val newColorVertexBuffer = FColorVertexBuffer(Ar)
            colorVertexBuffer = FSkeletalMeshVertexColorBuffer(newColorVertexBuffer.data)
        }
        if (FUE5ReleaseStreamObjectVersion.get(Ar) < FUE5ReleaseStreamObjectVersion.RemovingTessellation && !stripFlags.isClassDataStripped(CDSF_AdjacencyData)) {
            adjacencyIndexBuffer = FMultisizeIndexContainer(Ar)
        }
        if (hasClothData) {
            clothVertexBuffer = FSkeletalMeshVertexClothBuffer(Ar)
        }

        var skinWeightProfilesData = FSkinWeightProfilesData(Ar)

        if (Ar.game >= GAME_UE5_BASE) { // Note: This was added in UE4.27, but we're only reading it on UE5 for compatibility with Fortnite
            var rayTracingData = Ar.read(Ar.readInt32())
        }

        numVertices = positionVertexBuffer.numVertices
        numTexCoords = staticMeshVertexBuffer.numTexCoords

        vertexBufferGPUSkin.vertsFloat = Array(numVertices) {
            FGPUVertFloat().apply {
                pos = positionVertexBuffer.verts[it]
                infs = skinWeightVertexBuffer.weights[it]
                normal = staticMeshVertexBuffer.uv[it].normal
                uv = staticMeshVertexBuffer.uv[it].uv
            }
        }
    }

    val hasClothData get() = chunks.any { it.hasClothData } /*pre-UE4.13*/ || sections.any { it.hasClothData }
}