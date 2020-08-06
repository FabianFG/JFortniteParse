package me.fungames.jfortniteparse.valorant.exports

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath

@ExperimentalUnsignedTypes
class CharacterRoleDataAsset : UObject {
    var UIData: FSoftObjectPath? = null
    var Uuid: FGuid? = null

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)
}