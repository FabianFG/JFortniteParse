package me.fungames.jfortniteparse.fort.objects

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@ExperimentalUnsignedTypes
@UStruct
class ItemCategoryMappingData {
    var CategoryType: FName? = null // EFortItemType
    var CategoryName: FText? = null
}