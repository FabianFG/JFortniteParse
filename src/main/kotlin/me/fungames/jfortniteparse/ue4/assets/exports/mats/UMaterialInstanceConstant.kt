package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.structs.FScalarParameterValue
import me.fungames.jfortniteparse.ue4.assets.objects.structs.FTextureParameterValue
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive

@ExperimentalUnsignedTypes
class UMaterialInstanceConstant(Ar: FAssetArchive, exportObject: FObjectExport) : UMaterialInstance(Ar, exportObject) {

    val scalarParameterValues = baseObject.get<Array<FScalarParameterValue>>("ScalarParameterValues", Ar)
    val textureParameterValues = baseObject.get<Array<FTextureParameterValue>>("TextureParameterValues", Ar)
    val vectorParameterValues = baseObject.getOrNull<Array<FTextureParameterValue>>("VectorParameterValues", Ar)
}