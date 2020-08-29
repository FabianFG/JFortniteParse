package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.fort.objects.FortColorPalette
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath

@ExperimentalUnsignedTypes
class FortItemSeriesDefinition : UObject {
    var DisplayName: FText? = null
    var Colors: FortColorPalette? = null
    var BackgroundTexture: FSoftObjectPath? = null
    var ItemCardMaterial: FSoftObjectPath? = null
    var BackgroundMaterial: FSoftObjectPath? = null

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)
}