package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive

class FClothingSectionData {
    var assetGuid: FGuid
    var assetLodIndex: Int

    constructor(Ar: FArchive) {
        assetGuid = FGuid(Ar)
        assetLodIndex = Ar.readInt32()
    }

    constructor(assetGuid: FGuid, assetLodIndex: Int) {
        this.assetGuid = assetGuid
        this.assetLodIndex = assetLodIndex
    }
}