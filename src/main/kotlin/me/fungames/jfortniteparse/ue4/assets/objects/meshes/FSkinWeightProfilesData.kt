package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FSkinWeightProfilesData {
    var overrideData: Map<FName, FRuntimeSkinWeightProfileData>

    constructor(Ar: FArchive) {
        overrideData = Ar.readTMap { Ar.readFName() to FRuntimeSkinWeightProfileData(Ar) }
    }
}