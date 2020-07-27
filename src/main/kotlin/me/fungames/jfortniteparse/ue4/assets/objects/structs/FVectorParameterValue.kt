package me.fungames.jfortniteparse.ue4.assets.objects.structs

import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.assets.util.StructFieldName
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor

@ExperimentalUnsignedTypes
@StructFallbackClass
class FVectorParameterValue(
    @StructFieldName("ParameterName")
    val parameterName : FName?,
    @StructFieldName("ParameterInfo")
    val parameterInfo : FMaterialParameterInfo,
    @StructFieldName("ParameterValue")
    val parameterValue: FLinearColor?
) {
    fun getName() = parameterName?.text ?: parameterInfo.name.text
}