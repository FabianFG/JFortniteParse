package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.*
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import java.awt.image.BufferedImage


data class CosmeticVariant(var startUnlocked : Boolean, var isDefault : Boolean,
                           var hideIfNotOwned : Boolean, var customizationVariantTag : FName,
                           var variantName : FText?, var previewImage : FSoftObjectPath?) {
    var previewIcon : BufferedImage? = null
}

@ExperimentalUnsignedTypes
class FortCosmeticVariant : UExport {
    override var baseObject: UObject
    var variantChannelName : FText? = null
    var variantChannelTag : FName? = null
    val variants : MutableList<CosmeticVariant>

    constructor() : super("FortCosmeticVariant") {
        baseObject = UObject(
            mutableListOf(),
            null,
            "ItemDefinition"
        )
        variants = mutableListOf()
    }

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        variantChannelName = baseObject.getOrNull("VariantChannelName")
        variantChannelTag = baseObject.getOrNull<FStructFallback>("VariantChannelTag")?.getOrNull<FName>("TagName")
        val searchTag = when(exportObject.classIndex.importName) {
            "FortCosmeticCharacterPartVariant" -> "PartOptions"
            "FortCosmeticMaterialVariant" -> "MaterialOptions"
            "FortCosmeticParticleVariant" -> "ParticleOptions"
            else -> exportObject.classIndex.importName
                .replace("FortCosmetic", "")
                .replace("Variant", "")
        }
        variants = mutableListOf()
        val options = baseObject.getOrNull<UScriptArray>(searchTag) ?: return
        options.data.filterIsInstance<FStructFallback>().forEach {
            val startUnlocked = it.getOrDefault("bStartUnlocked", false)
            val isDefault = it.getOrDefault("bIsDefault", false)
            val hideIfNotOwned = it.getOrDefault("bHideIfNotOwned", false)
            val customizationVariantTag = it.get<FStructFallback>("CustomizationVariantTag").get<FName>("TagName")
            val variantName = it.getOrNull<FText>("VariantName")
            val previewImage = it.getOrNull<FSoftObjectPath>("PreviewImage")
            variants.add(CosmeticVariant(startUnlocked, isDefault, hideIfNotOwned, customizationVariantTag, variantName, previewImage))
        }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }

    override fun applyLocres(locres : Locres?) {
        variantChannelName?.applyLocres(locres)
        variants.forEach {
            it.variantName?.applyLocres(locres)
        }
    }
}