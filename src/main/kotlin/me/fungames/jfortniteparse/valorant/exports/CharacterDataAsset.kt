package me.fungames.jfortniteparse.valorant.exports

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath

class CharacterDataAsset : UObject() {
    var CharacterID: FName? = null // enum CharacterID
    var Character: FSoftObjectPath? = null
    var UIData: FSoftObjectPath? = null
    var Role: FSoftObjectPath? = null
    var CharacterSelectFXC: FSoftObjectPath? = null
    var DeveloperName: FName? = null
    var bIsPlayableCharacter: Boolean = false
    var bAvailableForTest: Boolean = false
    var Uuid: FGuid? = null
    var bBaseContent: Boolean = false
}