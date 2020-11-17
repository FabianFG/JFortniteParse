package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.gameplaytags.FGameplayTag
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import java.awt.image.BufferedImage

@ExperimentalUnsignedTypes
data class CosmeticVariant(var startUnlocked: Boolean, var isDefault: Boolean,
                           var hideIfNotOwned: Boolean, var customizationVariantTag: FName,
                           var variantName: FText?, var previewImage: FSoftObjectPath?) {
    var previewIcon: BufferedImage? = null
}

@ExperimentalUnsignedTypes
open class FortCosmeticVariant : UObject {
    @JvmField var VariantChannelTag: FGameplayTag? = null
    @JvmField var VariantChannelName: FText? = null
    @JvmField var ActiveVariantTag: FGameplayTag? = null
    var variants: MutableList<CosmeticVariant> = mutableListOf()

    constructor() : super()
    constructor(exportObject: FObjectExport) : super(exportObject)

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        /*val searchTag = when (export!!.classIndex.name) {
            "FortCosmeticCharacterPartVariant" -> "PartOptions"
            "FortCosmeticMaterialVariant" -> "MaterialOptions"
            "FortCosmeticParticleVariant" -> "ParticleOptions"
            else -> export!!.classIndex.name
                .replace("FortCosmetic", "")
                .replace("Variant", "")
        }
        variants = mutableListOf()
        val options = getOrNull<UScriptArray>(searchTag) ?: return
        options.data.filterIsInstance<FStructFallback>().forEach {
            val startUnlocked = it.getOrDefault("bStartUnlocked", false)
            val isDefault = it.getOrDefault("bIsDefault", false)
            val hideIfNotOwned = it.getOrDefault("bHideIfNotOwned", false)
            val customizationVariantTag = it.get<FStructFallback>("CustomizationVariantTag").get<FName>("TagName")
            val variantName = it.getOrNull<FText>("VariantName")
            val previewImage = it.getOrNull<FSoftObjectPath>("PreviewImage")
            variants.add(CosmeticVariant(startUnlocked, isDefault, hideIfNotOwned, customizationVariantTag, variantName, previewImage))
        }*/
    }
}