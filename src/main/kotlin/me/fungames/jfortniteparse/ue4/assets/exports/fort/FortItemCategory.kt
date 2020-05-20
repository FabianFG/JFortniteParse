package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.*
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FortItemCategory : UExport {
    override var baseObject: UObject
    //This has way more stuff, but we just need the user facing flags for now
    val userFacingFlags : Map<String, Pair<FText, FPackageIndex>>

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        val tertiary = baseObject.get<UScriptArray>("TertiaryCategories")
        userFacingFlags = mutableMapOf()
        tertiary.contents.forEach { entry ->
            val data = entry.getTagTypeValue() as FStructFallback
            val tags = data.get<FGameplayTagContainer>("TagContainer")
            if (tags.gameplayTags.any { it.text.startsWith("Cosmetics.UserFacingFlags") }) {
                val name = data.get<FText>("CategoryName")
                val brush = data.get<FStructFallback>("CategoryBrush")
                val slateBrush = brush.getOrDefault("Brush_XL",
                    brush.getOrDefault("Brush_L",
                        brush.getOrDefault("Brush_M",
                            brush.getOrDefault("Brush_S",
                                brush.getOrDefault("Brush_XS",
                                    brush.get<FStructFallback>("Brush_XXS"))))))
                val resourceObject = slateBrush.get<FPackageIndex>("ResourceObject")
                val pair = name to resourceObject
                tags.gameplayTags.forEach {
                    val flag = it.text
                    if (flag.startsWith("Cosmetics.UserFacingFlags"))
                        userFacingFlags[flag] = pair
                }
            }
        }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}