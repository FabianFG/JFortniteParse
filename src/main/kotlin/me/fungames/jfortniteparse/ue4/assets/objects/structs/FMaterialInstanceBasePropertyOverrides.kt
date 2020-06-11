package me.fungames.jfortniteparse.ue4.assets.objects.structs

import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.assets.util.StructFieldName

@StructFallbackClass
class FMaterialInstanceBasePropertyOverrides(
    @StructFieldName("OpacityMaskClipValue")
    val opacityMaskClipValue : Float,
    @field:StructFieldName("DitheredLODTransition")
    val ditheredLodTransition : Boolean
)