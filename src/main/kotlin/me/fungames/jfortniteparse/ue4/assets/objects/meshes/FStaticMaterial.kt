package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.versions.FRenderingObjectVersion

@ExperimentalUnsignedTypes
class FStaticMaterial : UClass {

    var materialInterface : UMaterialInterface?
    var materialSlotName : FName
    var uvChannelData : FMeshUVChannelInfo? = null

    constructor(Ar : FAssetArchive) {
        super.init(Ar)
        materialInterface = FPackageIndex(Ar).run { Ar.provider?.loadObject<UMaterialInterface>(this) }
        materialSlotName = Ar.readFName()
        if (FRenderingObjectVersion.get(Ar) >= FRenderingObjectVersion.TextureStreamingMeshUVChannelData)
            uvChannelData = FMeshUVChannelInfo(Ar)
        super.complete(Ar)
    }
}