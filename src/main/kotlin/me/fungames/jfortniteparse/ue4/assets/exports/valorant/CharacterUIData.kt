package me.fungames.jfortniteparse.ue4.assets.exports.valorant

import me.fungames.jfortniteparse.ue4.assets.enums.valorant.ECharacterAbilitySlot
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.UScriptMap
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@ExperimentalUnsignedTypes
class CharacterUIData : UExport {
    override var baseObject: UObject
    var bustPortrait : FPackageIndex
    var fullPortrait : FPackageIndex
    var displayIconSmall : FPackageIndex
    var displayIcon : FPackageIndex
    var abilitiesWithIndex : Map<ECharacterAbilitySlot, Int>
    var abilities = mutableMapOf<ECharacterAbilitySlot, CharacterAbilityUIData>()
    var wwiseStateName : String?
    var displayName : FText
    var description : FText


    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        bustPortrait = baseObject.get("BustPortrait")
        fullPortrait = baseObject.get("FullPortrait")
        displayIconSmall = baseObject.get("DisplayIconSmall")
        displayIcon = baseObject.get("DisplayIcon")
        abilitiesWithIndex = baseObject.get<UScriptMap>("Abilities").mapData
            .mapKeys { ECharacterAbilitySlot.valueOf((it.key.getTagTypeValueLegacy() as FName).text.substringAfter("ECharacterAbilitySlot::")) }
            .mapValues { (it.value.getTagTypeValueLegacy() as FPackageIndex).index }
        wwiseStateName = baseObject.getOrNull<FName>("WwiseStateName")?.text
        displayName = baseObject.get("DisplayName")
        description = baseObject.get("Description")
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}