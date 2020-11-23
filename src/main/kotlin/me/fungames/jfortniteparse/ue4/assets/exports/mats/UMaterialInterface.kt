package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.enums.EMobileSpecularMask
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.converters.CMaterialParams
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@OnlyAnnotated
open class UMaterialInterface : UObject(), UUnrealMaterial {
    @JvmField @UProperty var SubsurfaceProfile: FPackageIndex? = null /*SubsurfaceProfile*/
    @JvmField @UProperty var LightmassSettings: FLightmassMaterialInterfaceSettings? = null
    @JvmField @UProperty var TextureStreamingData: List<FMaterialTextureInfo>? = null
    @JvmField @UProperty var AssetUserData: List<FPackageIndex /*AssetUserData*/>? = null

    //I think those aren't used in UE4 but who knows
    var FlattenedTexture: UTexture? = null
    var MobileBaseTexture: UTexture? = null
    var MobileNormalTexture: UTexture? = null
    var bUseMobileSpecular: Boolean = false
    var MobileSpecularPower: Float = 16.0f
    var MobileSpecularMask: EMobileSpecularMask = EMobileSpecularMask.MSM_Constant
    var MobileMaskTexture: UTexture? = null

    override fun getParams(params: CMaterialParams) {
        if (FlattenedTexture != null) params.diffuse = FlattenedTexture
        if (MobileBaseTexture != null) params.diffuse = FlattenedTexture
        if (MobileNormalTexture != null) params.normal = MobileNormalTexture
        if (MobileMaskTexture != null) params.opacity = MobileMaskTexture
        params.useMobileSpecular = bUseMobileSpecular
        params.mobileSpecularPower = MobileSpecularPower
        params.mobileSpecularMask = MobileSpecularMask
    }

    override fun name() = name

    @UStruct
    class FLightmassMaterialInterfaceSettings {
        @JvmField var EmissiveBoost: Float? = null
        @JvmField var DiffuseBoost: Float? = null
        @JvmField var ExportResolutionScale: Float? = null
        @JvmField var bCastShadowAsMasked: Boolean? = null
        @JvmField var bOverrideCastShadowAsMasked: Boolean? = null
        @JvmField var bOverrideEmissiveBoost: Boolean? = null
        @JvmField var bOverrideDiffuseBoost: Boolean? = null
        @JvmField var bOverrideExportResolutionScale: Boolean? = null
    }

    @UStruct
    class FMaterialTextureInfo {
        @JvmField var SamplingScale: Float? = null
        @JvmField var UVChannelIndex: Int? = null
        @JvmField var TextureName: FName? = null
    }
}