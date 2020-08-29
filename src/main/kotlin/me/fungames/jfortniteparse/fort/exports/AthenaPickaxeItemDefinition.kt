package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@ExperimentalUnsignedTypes
class AthenaPickaxeItemDefinition : FortItemDefinition {
    var WeaponDefinition: FPackageIndex? = null // FortWeaponItemDefinition

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)
}