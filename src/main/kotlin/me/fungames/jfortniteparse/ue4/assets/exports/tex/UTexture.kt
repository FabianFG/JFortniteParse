package me.fungames.jfortniteparse.ue4.assets.exports.tex

import me.fungames.jfortniteparse.converters.ue4.CMaterialParams
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UUnrealMaterial
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FObjectExport

@ExperimentalUnsignedTypes
abstract class UTexture(export: FObjectExport) : UUnrealMaterial(export) {
    override fun getParams(params: CMaterialParams) {
        //???
    }
}