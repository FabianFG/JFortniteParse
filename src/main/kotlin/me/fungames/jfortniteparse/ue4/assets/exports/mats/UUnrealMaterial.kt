package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.converters.ue4.CMaterialParams
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport

@ExperimentalUnsignedTypes
abstract class UUnrealMaterial(export : FObjectExport) : UExport(export) {

    abstract fun getParams(params: CMaterialParams)

    fun isTextureCube() = false

    open fun appendReferencedTextures(outTextures : MutableList<UUnrealMaterial>, onlyRendered : Boolean) {
        val params = CMaterialParams()
        getParams(params)
        params.appendAllTextures(outTextures)
    }
}