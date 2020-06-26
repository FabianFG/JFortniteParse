package me.fungames.jfortniteparse.ue4.assets.objects.structs

import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.assets.util.StructFieldName

@StructFallbackClass
class FScalarParameterValue(
    @StructFieldName("ParameterName")
    val parameterName : FName?,
    @StructFieldName("ParameterValue")
    val parameterValue : Float,
    @StructFieldName("ParameterInfo")
    val parameterInfo : FMaterialParameterInfo
) {
    fun getName() = parameterName?.text ?: parameterInfo.name.text
}