package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_SKINWEIGHT_PROFILE_DATA_LAYOUT_CHANGES

class FSkinWeightOverrideInfo(var influencesOffset: UInt, var numInfluences: UByte) {
    constructor(Ar: FArchive) : this(Ar.readUInt32(), Ar.readUInt8())
}

class FRuntimeSkinWeightProfileData {
    var vertexIndexToInfluenceOffset = emptyMap<UInt, UInt>()
    var overridesInfo = emptyArray<FSkinWeightOverrideInfo>()
    var weights = UShortArray(0)
    var boneIDs = ByteArray(0)
    var boneWeights = ByteArray(0)
    var numWeightsPerVertex: UByte = 0u

    constructor(Ar: FArchive) {
        if (Ar.ver < VER_UE4_SKINWEIGHT_PROFILE_DATA_LAYOUT_CHANGES) {
            overridesInfo = Ar.readTArray { FSkinWeightOverrideInfo(Ar) }
            weights = UShortArray(Ar.readInt32()) { Ar.readUInt16() }
        } else {
            // UE4.26+
            boneIDs = Ar.read(Ar.readInt32())
            boneWeights = Ar.read(Ar.readInt32())
            numWeightsPerVertex = Ar.readUInt8()
        }
        vertexIndexToInfluenceOffset = Ar.readTMap { Ar.readUInt32() to Ar.readUInt32() }
    }
}