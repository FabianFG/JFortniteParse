package me.fungames.jfortniteparse.ue4.assets.exports.tex

import me.fungames.jfortniteparse.ue4.assets.exports.mats.UUnrealMaterial
import me.fungames.jfortniteparse.ue4.converters.CMaterialParams

@ExperimentalUnsignedTypes
abstract class UTexture : UUnrealMaterial {
    constructor() : super()

    override fun getParams(params: CMaterialParams) {
        //???
    }
}