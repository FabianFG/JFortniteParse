package me.fungames.jfortniteparse.ue4.assets.objects.meshes

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInterface
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.versions.FRenderingObjectVersion

@UStruct
class FStaticMaterial {
    @JvmField @UProperty("MaterialInterface")
    var materialInterface: Lazy<UMaterialInterface>? = null
    @JvmField @UProperty("MaterialSlotName")
    var materialSlotName: FName = FName.NAME_None
    @JvmField @UProperty("ImportedMaterialSlotName")
    var importedMaterialSlotName: FName = FName.NAME_None
    @JvmField @UProperty("UVChannelData")
    var uvChannelData: FMeshUVChannelInfo? = null

    constructor()

    constructor(Ar: FAssetArchive) {
        materialInterface = Ar.readObject()
        materialSlotName = Ar.readFName()
        if (FRenderingObjectVersion.get(Ar) >= FRenderingObjectVersion.TextureStreamingMeshUVChannelData)
            uvChannelData = FMeshUVChannelInfo(Ar)
    }
}