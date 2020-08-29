package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.converters.CMaterialParams
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
abstract class UUnrealMaterial : UObject {
    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)

    abstract fun getParams(params: CMaterialParams)

    fun isTextureCube() = false

    open fun appendReferencedTextures(outTextures: MutableList<UUnrealMaterial>, onlyRendered: Boolean) {
        val params = CMaterialParams()
        getParams(params)
        params.appendAllTextures(outTextures)
    }
}