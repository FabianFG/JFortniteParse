package me.fungames.jfortniteparse.ue4.assets.objects.structs

import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.assets.util.StructFieldName
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FName

@StructFallbackClass
class FMaterialParameterInfo(
    @StructFieldName("Name")
    val name : FName
)