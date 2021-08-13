package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.engine.FStripDataFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.FAnimObjectVersion
import me.fungames.jfortniteparse.ue4.versions.FSkeletalMeshCustomVersion

class FSkinWeightVertexBuffer {
    companion object {
        private const val NUM_INFLUENCES_UE4 = 4

        @JvmStatic
        fun metadataSize(Ar: FArchive): Int {
            var numBytes = 0
            val newWeightFormat = FAnimObjectVersion.get(Ar) >= FAnimObjectVersion.UnlimitedBoneInfluences
            if (!Ar.versions["SkeletalMesh.UseNewCookedFormat"]) {
                numBytes = 2 * 4
            } else if (!newWeightFormat) {
                numBytes = 3 * 4
            } else {
                numBytes = 4 * 4
                if (FAnimObjectVersion.get(Ar) >= FAnimObjectVersion.IncreaseBoneIndexLimitPerChunk) {
                    numBytes += 4
                }
            }
            if (newWeightFormat) {
                numBytes += 4
            }
            return numBytes
        }
    }

    var weights = emptyArray<FSkinWeightInfo>()

    constructor(Ar: FArchive, numSkelCondition: Boolean) {
        val newWeightFormat = FAnimObjectVersion.get(Ar) >= FAnimObjectVersion.UnlimitedBoneInfluences

        // region FSkinWeightDataVertexBuffer
        val dataStripFlags = FStripDataFlags(Ar)

        // region FSkinWeightDataVertexBuffer::SerializeMetaData
        val variableBonesPerVertex: Boolean
        var extraBoneInfluences: Boolean
        val maxBoneInfluences: UInt
        val use16BitBoneIndex: Boolean
        val numVertices: UInt
        val numBones: UInt

        if (!Ar.versions["SkeletalMesh.UseNewCookedFormat"]) {
            extraBoneInfluences = Ar.readBoolean()
            numVertices = Ar.readUInt32()
            maxBoneInfluences = if (extraBoneInfluences) 8u else 4u
        } else if (!newWeightFormat) {
            extraBoneInfluences = Ar.readBoolean()
            if (FSkeletalMeshCustomVersion.get(Ar) >= FSkeletalMeshCustomVersion.SplitModelAndRenderData) {
                Ar.skip(4) // val stride = Ar.readUInt32()
            }
            numVertices = Ar.readUInt32()
            maxBoneInfluences = if (extraBoneInfluences) 8u else 4u
            numBones = maxBoneInfluences * numVertices
            variableBonesPerVertex = false
        } else {
            variableBonesPerVertex = Ar.readBoolean()
            maxBoneInfluences = Ar.readUInt32()
            numBones = Ar.readUInt32()
            numVertices = Ar.readUInt32()
            extraBoneInfluences = maxBoneInfluences > NUM_INFLUENCES_UE4.toUInt()
            // use16BitBoneIndex doesn't exist before version IncreaseBoneIndexLimitPerChunk
            if (FAnimObjectVersion.get(Ar) >= FAnimObjectVersion.IncreaseBoneIndexLimitPerChunk) {
                use16BitBoneIndex = Ar.readBoolean()
            }
        }
        // endregion

        var newData = ByteArray(0)
        if (!dataStripFlags.isDataStrippedForServer()) {
            if (!newWeightFormat) {
                weights = Ar.readBulkTArray { FSkinWeightInfo(Ar, extraBoneInfluences) }
            } else {
                newData = Ar.readBulkByteArray()
            }
        } else {
            extraBoneInfluences = numSkelCondition
        }
        // endregion

        if (newWeightFormat) {
            // region FSkinWeightLookupVertexBuffer
            val lookupStripFlags = FStripDataFlags(Ar)

            // region FSkinWeightLookupVertexBuffer::SerializeMetaData
            //if (newWeightFormat) {
            var numLookupVertices = Ar.readInt32()
            //}
            // endregion

            if (!lookupStripFlags.isDataStrippedForServer()) {
                Ar.readBulkTArray { Ar.readUInt32() } // LookupData
            }
            // endregion

            // Convert influence data
            if (newData.isNotEmpty()) {
                val tempAr = FByteArchive(newData, Ar.versions)
                weights = Array(numVertices.toInt()) { FSkinWeightInfo(tempAr, extraBoneInfluences) }
            }
        }
    }
}