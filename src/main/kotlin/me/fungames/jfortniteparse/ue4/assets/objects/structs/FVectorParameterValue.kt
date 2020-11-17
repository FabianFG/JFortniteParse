package me.fungames.jfortniteparse.ue4.assets.objects.structs

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@ExperimentalUnsignedTypes
@UStruct
class FVectorParameterValue(
    val ParameterName: FName?,
    val ParameterInfo: FMaterialParameterInfo,
    val ParameterValue: FLinearColor?
) {
    fun getName() = ParameterName?.text ?: ParameterInfo.Name.text
}