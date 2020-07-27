package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.exports.fort.EFortRarity
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortCosmeticVariant
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTagContainer

@ExperimentalUnsignedTypes
open class ItemDefinition : UExport {
    final override var baseObject: UObject

    var heroDefinitionPackage : FPackageIndex? = null
    val usesHeroDefinition : Boolean
        get() = heroDefinitionPackage != null

    var weaponDefinitionPackage : FPackageIndex? = null
    val usesWeaponDefinition : Boolean
        get() = weaponDefinitionPackage != null

    var smallPreviewImage : FSoftObjectPath? = null
    var largePreviewImage : FSoftObjectPath? = null
    val hasIcons : Boolean
        get() = largePreviewImage != null

    var displayAssetPath : FSoftObjectPath? = null
    val usesDisplayAssetPath : Boolean
        get() = displayAssetPath != null

    var rarity : EFortRarity = EFortRarity.Uncommon
    var series : FPackageIndex? = null

    var displayName : FText? = null
    var shortDescription : FText? = null
    var description : FText? = null
    var gameplayTags : FGameplayTagContainer

    val set : FName?
        get() = gameplayTags.getValue("Cosmetics.Set")
    val source : FName?
        get() = gameplayTags.getValue("Cosmetics.Source")
    val userFacingFlags : FName?
        get() = gameplayTags.getValue("Cosmetics.UserFacingFlags")

    var variants = mutableListOf<FortCosmeticVariant>()

    constructor() : super("ItemDefinition") {
        baseObject = UObject(
            mutableListOf(),
            null,
            "ItemDefinition"
        )
        gameplayTags =
            FGameplayTagContainer(mutableListOf())
    }

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        heroDefinitionPackage = baseObject.getOrNull("HeroDefinition")
        weaponDefinitionPackage = baseObject.getOrNull("WeaponDefinition")
        smallPreviewImage = baseObject.getOrNull("SmallPreviewImage")
        largePreviewImage = baseObject.getOrNull("LargePreviewImage") ?: baseObject.getOrNull("SpriteSheet")
        displayAssetPath = baseObject.getOrNull("DisplayAssetPath")
        rarity = baseObject.getOrNull("Rarity") ?: EFortRarity.Uncommon
        series = baseObject.getOrNull("Series")
        displayName = baseObject.getOrNull("DisplayName")
        shortDescription = baseObject.getOrNull("ShortDescription")
        if (shortDescription == null && exportType == "AthenaItemWrapDefinition")
            shortDescription = FText(
                "Fort.Cosmetics",
                "ItemWrapShortDescription",
                "Wrap"
            )
        description = baseObject.getOrNull("Description")
        gameplayTags = baseObject.getOrNull("GameplayTags")?: FGameplayTagContainer(
            mutableListOf()
        )
        variants = baseObject.getOrDefault<Array<FortCosmeticVariant>>("ItemVariants", emptyArray(), Ar).toMutableList()
        super.complete(Ar)
    }
    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }

    override fun applyLocres(locres : Locres?) {
        displayName?.applyLocres(locres)
        shortDescription?.applyLocres(locres)
        description?.applyLocres(locres)
        variants.forEach { it.applyLocres(locres) }
    }

}