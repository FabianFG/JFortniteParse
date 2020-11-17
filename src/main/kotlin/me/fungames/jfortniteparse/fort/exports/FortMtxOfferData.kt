package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor
import me.fungames.jfortniteparse.ue4.objects.slatecore.styling.FSlateBrush
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
class FortMtxOfferData : UObject {
    var DisplayName: FText? = null
    var ShortDescription: FText? = null
    var DisclaimerText: FText? = null
    var TileImage: FSlateBrush? = null
    var DetailsImage: FSlateBrush? = null
    var DetailsAttributes: Array<FortMtxDetailsAttribute>? = null
    var Gradient: FortMtxGradient? = null
    var Background: FLinearColor? = null
    var UpsellPrimaryColor: FLinearColor? = null
    var UpsellSecondaryColor: FLinearColor? = null
    var UpsellTextColor: FLinearColor? = null
    var DisplaySize: EFortMtxOfferDisplaySize? = null

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)
}

@ExperimentalUnsignedTypes
@UStruct
class FortMtxDetailsAttribute {
    var Name: FText? = null
    var Value: FText? = null
}

@ExperimentalUnsignedTypes
@UStruct
class FortMtxGradient {
    var Start: FLinearColor? = null
    var Stop: FLinearColor? = null
}

enum class EFortMtxOfferDisplaySize { Small, Medium, Large }