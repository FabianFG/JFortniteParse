package me.fungames.jfortniteparse.ue4.assets.objects.structs

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@ExperimentalUnsignedTypes
@UStruct
class FTextureParameterValue(
    val ParameterName: FName?,
    val ParameterInfo: FMaterialParameterInfo,
    val ParameterValue: UTexture2D?
) {
    fun getName() = ParameterName?.text ?: ParameterInfo.Name.text
}