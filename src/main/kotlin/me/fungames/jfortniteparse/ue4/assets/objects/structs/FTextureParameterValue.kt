package me.fungames.jfortniteparse.ue4.assets.objects.structs

import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.assets.util.StructFieldName
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FName

@ExperimentalUnsignedTypes
@StructFallbackClass
class FTextureParameterValue(
    @StructFieldName("ParameterName")
    val parameterName : FName?,
    @StructFieldName("ParameterInfo")
    val parameterInfo : FMaterialParameterInfo,
    @StructFieldName("ParameterValue")
    val parameterValue: UTexture2D?
) {
    fun getName() = parameterName?.text ?: parameterInfo.name.text
}