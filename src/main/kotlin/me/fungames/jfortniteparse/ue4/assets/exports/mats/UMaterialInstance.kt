package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive

@ExperimentalUnsignedTypes
open class UMaterialInstance(Ar: FAssetArchive, exportObject: FObjectExport) : UMaterialInterface(Ar, exportObject) {

    val parent = Ar.loadObject<UExport>(baseObject.get("Parent")) // TODO Actually use proper type

}