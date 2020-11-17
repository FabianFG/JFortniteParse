package me.fungames.jfortniteparse.ue4.assets.objects.structs

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@UStruct
class FScalarParameterValue(
    val ParameterName: FName?,
    val ParameterValue: Float,
    val ParameterInfo: FMaterialParameterInfo
) {
    fun getName() = ParameterName?.text ?: ParameterInfo.Name.text
}