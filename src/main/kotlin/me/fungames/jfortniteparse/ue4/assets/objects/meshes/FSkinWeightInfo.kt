package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FSkinWeightInfo {
    companion object {
        private const val NUM_INFLUENCES_UE4 = 4
        private const val MAX_TOTAL_INFLUENCES_UE4 = 8
    }

    val boneIndex = UByteArray(NUM_INFLUENCES_UE4)
    val boneWeight = UByteArray(NUM_INFLUENCES_UE4)

    constructor()

    constructor(Ar: FArchive, numSkelCondition: Boolean) {
        val numSkelInfluences = if (numSkelCondition) MAX_TOTAL_INFLUENCES_UE4 else NUM_INFLUENCES_UE4
        if (numSkelInfluences <= boneIndex.size) {
            repeat(numSkelInfluences) { boneIndex[it] = Ar.readUInt8() }
            repeat(numSkelInfluences) { boneWeight[it] = Ar.readUInt8() }
        } else {
            val boneIndex2 = UByteArray(MAX_TOTAL_INFLUENCES_UE4)
            val boneWeight2 = UByteArray(MAX_TOTAL_INFLUENCES_UE4)
            repeat(numSkelInfluences) { boneIndex2[it] = Ar.readUInt8() }
            repeat(numSkelInfluences) { boneWeight2[it] = Ar.readUInt8() }

            // copy influences to vertex
            repeat(NUM_INFLUENCES_UE4) {
                boneIndex[it] = boneIndex2[it]
                boneWeight[it] = boneWeight2[it]
            }
        }
    }
}