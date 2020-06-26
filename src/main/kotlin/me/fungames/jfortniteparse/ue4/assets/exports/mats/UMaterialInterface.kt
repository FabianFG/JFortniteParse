package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.converters.ue4.CMaterialParams
import me.fungames.jfortniteparse.ue4.assets.enums.EMobileSpecularMask
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
open class UMaterialInterface : UUnrealMaterial {
    final override var baseObject: UObject
    //I think those aren't used in UE4 but who knows
    var flattenedTexture : UTexture?
    var mobileBaseTexture : UTexture?
    var mobileNormalTexture : UTexture?
    var useMobileSpecular : Boolean
    var mobileSpecularPower : Float
    var mobileSpecularMask : EMobileSpecularMask
    var mobileMaskTexture : UTexture?

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        flattenedTexture = baseObject.getOrNull("FlattenedTexture", Ar)
        mobileBaseTexture = baseObject.getOrNull("MobileBaseTexture", Ar)
        mobileNormalTexture = baseObject.getOrNull("MobileNormalTexture", Ar)
        useMobileSpecular = baseObject.getOrNull("bUseMobileSpecular") ?: false
        mobileSpecularPower = baseObject.getOrNull("MobileSpecularPower") ?: 16.0f
        mobileSpecularMask = baseObject.getOrNull("MobileSpecularMask") ?: EMobileSpecularMask.MSM_Constant
        mobileMaskTexture = baseObject.getOrNull("MobileMaskTexture", Ar)
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }

    override fun getParams(params: CMaterialParams) {
        if (flattenedTexture != null) params.diffuse = flattenedTexture
        if (mobileBaseTexture != null) params.diffuse = flattenedTexture
        if (mobileNormalTexture != null) params.normal = mobileNormalTexture
        if (mobileMaskTexture != null) params.opacity = mobileMaskTexture
        params.useMobileSpecular = useMobileSpecular
        params.mobileSpecularPower = mobileSpecularPower
        params.mobileSpecularMask = mobileSpecularMask
    }
}