package me.fungames.jfortniteparse.valorant.exports

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.UScriptMap
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

class CharacterUIData : UObject() {
    var FullPortrait: FPackageIndex? = null
    var BustPortrait: FPackageIndex? = null
    var DisplayIconSmall: FPackageIndex? = null
    var DisplayIcon: FPackageIndex? = null
    var Abilities: UScriptMap? = null // Map<ECharacterAbilitySlot, FPackageIndex<CharacterAbilityUIData>>
    var WwiseStateName: FName? = null
    var DisplayName: FText? = null
    var Description: FText? = null
}