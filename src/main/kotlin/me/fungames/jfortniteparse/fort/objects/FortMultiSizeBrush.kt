package me.fungames.jfortniteparse.fort.objects

import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateBrush

@ExperimentalUnsignedTypes
@StructFallbackClass
class FortMultiSizeBrush {
    var Brush_XXS: FSlateBrush? = null
    var Brush_XS: FSlateBrush? = null
    var Brush_S: FSlateBrush? = null
    var Brush_M: FSlateBrush? = null
    var Brush_L: FSlateBrush? = null
    var Brush_XL: FSlateBrush? = null
}