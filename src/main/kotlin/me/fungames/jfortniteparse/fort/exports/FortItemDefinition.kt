package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.fort.enums.EFortRarity
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath

@ExperimentalUnsignedTypes
open class FortItemDefinition : UObject {
    var ItemVariants = arrayOf<FortCosmeticVariant>()
    var Rarity = EFortRarity.Uncommon
    var DisplayName: FText? = null
    var ShortDescription: FText? = null
        get() =
            if (field == null && exportType == "AthenaItemWrapDefinition")
                FText("Fort.Cosmetics", "ItemWrapShortDescription", "Wrap")
            else field
    var Description: FText? = null
    var SearchTags: FText? = null
    var GameplayTags: FGameplayTagContainer? = null
    var SmallPreviewImage: FSoftObjectPath? = null
    var LargePreviewImage: FSoftObjectPath? = null
    var DisplayAssetPath: FSoftObjectPath? = null
    var Series: FPackageIndex? = null

    val set: FName?
        get() = GameplayTags?.getValue("Cosmetics.Set")

    val source: FName?
        get() = GameplayTags?.getValue("Cosmetics.Source")

    val userFacingFlags: FName?
        get() = GameplayTags?.getValue("Cosmetics.UserFacingFlags")

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)
}