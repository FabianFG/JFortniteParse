package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.versions.FRenderingObjectVersion

class FStaticMaterial {
    var materialInterface: Lazy<UMaterialInterface>?
    var materialSlotName: FName
    var uvChannelData: FMeshUVChannelInfo? = null

    constructor(Ar: FAssetArchive) {
        materialInterface = Ar.readObject()
        materialSlotName = Ar.readFName()
        if (FRenderingObjectVersion.get(Ar) >= FRenderingObjectVersion.TextureStreamingMeshUVChannelData)
            uvChannelData = FMeshUVChannelInfo(Ar)
    }
}