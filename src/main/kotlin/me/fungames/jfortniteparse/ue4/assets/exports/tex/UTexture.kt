package me.fungames.jfortniteparse.ue4.assets.exports.tex

import me.fungames.jfortniteparse.ue4.assets.exports.mats.UUnrealMaterial
import me.fungames.jfortniteparse.ue4.converters.CMaterialParams
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
abstract class UTexture : UUnrealMaterial {
    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)

    override fun getParams(params: CMaterialParams) {
        //???
    }
}