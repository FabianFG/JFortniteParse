package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@ExperimentalUnsignedTypes
class AthenaCharacterItemDefinition : FortItemDefinition {
    var HeroDefinition: FPackageIndex? = null // FortHeroType
//    var ItemVariantPreviews: Array<FStructFallback>? = null

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)
}