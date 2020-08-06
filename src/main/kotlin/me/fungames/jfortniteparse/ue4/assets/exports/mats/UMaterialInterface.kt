package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.enums.EMobileSpecularMask
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.converters.CMaterialParams
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
open class UMaterialInterface : UUnrealMaterial {
    //I think those aren't used in UE4 but who knows
    var FlattenedTexture: UTexture? = null
    var MobileBaseTexture: UTexture? = null
    var MobileNormalTexture: UTexture? = null
    var bUseMobileSpecular: Boolean = false
    var MobileSpecularPower: Float = 16.0f
    var MobileSpecularMask: EMobileSpecularMask = EMobileSpecularMask.MSM_Constant
    var MobileMaskTexture: UTexture? = null

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)

    override fun getParams(params: CMaterialParams) {
        if (FlattenedTexture != null) params.diffuse = FlattenedTexture
        if (MobileBaseTexture != null) params.diffuse = FlattenedTexture
        if (MobileNormalTexture != null) params.normal = MobileNormalTexture
        if (MobileMaskTexture != null) params.opacity = MobileMaskTexture
        params.useMobileSpecular = bUseMobileSpecular
        params.mobileSpecularPower = MobileSpecularPower
        params.mobileSpecularMask = MobileSpecularMask
    }
}