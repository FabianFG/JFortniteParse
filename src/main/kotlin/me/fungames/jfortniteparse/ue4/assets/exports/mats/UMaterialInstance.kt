package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.ue4.assets.objects.structs.FMaterialInstanceBasePropertyOverrides
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
open class UMaterialInstance : UMaterialInterface {
    val Parent: UUnrealMaterial? = null
    val BasePropertyOverrides: FMaterialInstanceBasePropertyOverrides? = null

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)
}