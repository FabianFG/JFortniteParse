package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.fort.objects.ItemCategory
import me.fungames.jfortniteparse.fort.objects.ItemCategoryMappingData
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
class FortItemCategory : UObject {
    var PrimaryCategories: Array<ItemCategoryMappingData>? = null
    var SecondaryCategories: Array<ItemCategory>? = null
    var TertiaryCategories: Array<ItemCategory>? = null

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)
}