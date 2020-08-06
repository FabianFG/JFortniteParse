package me.fungames.jfortniteparse.fort.objects

import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer

@ExperimentalUnsignedTypes
@StructFallbackClass
class ItemCategory {
    var TagContainer: FGameplayTagContainer? = null
    var CategoryName: FText? = null
    var CategoryBrush: FortMultiSizeBrush? = null
}