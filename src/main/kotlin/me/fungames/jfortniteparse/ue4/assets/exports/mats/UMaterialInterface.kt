package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres

@ExperimentalUnsignedTypes
open class UMaterialInterface : UExport {
    final override var baseObject: UObject
    //I think those aren't used in UE4 but who knows
    var flattenedTexture : UTexture2D?
    var mobileBaseTexture : UTexture2D?
    var mobileNormalTexture : UTexture2D?
    var useMobileSpecular : Boolean
    var mobileSpecularPower : Float
    var mobileSpecularMask : FName?
    var mobileMaskTexture : UTexture2D?

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        flattenedTexture = baseObject.getOrNull("FlattenedTexture", Ar)
        mobileBaseTexture = baseObject.getOrNull("MobileBaseTexture", Ar)
        mobileNormalTexture = baseObject.getOrNull("MobileNormalTexture", Ar)
        useMobileSpecular = baseObject.getOrNull("bUseMobileSpecular") ?: false
        mobileSpecularPower = baseObject.getOrNull("MobileSpecularPower") ?: 0.0f
        mobileSpecularMask = baseObject.getOrNull("MobileSpecularMask")
        mobileMaskTexture = baseObject.getOrNull("MobileMaskTexture", Ar)
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}