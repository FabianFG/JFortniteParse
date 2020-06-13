package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.structs.FMaterialInstanceBasePropertyOverrides
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive

@ExperimentalUnsignedTypes
open class UMaterialInstance(Ar: FAssetArchive, exportObject: FObjectExport) : UMaterialInterface(Ar, exportObject) {

    val parent = baseObject.getOrNull<UUnrealMaterial>("Parent", Ar)
    val basePropertyOverrides = baseObject.get<FMaterialInstanceBasePropertyOverrides>("BasePropertyOverrides")
}