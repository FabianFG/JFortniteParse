package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.core.math.FLinearColor
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FSoftObjectPath
import java.awt.Color

@ExperimentalUnsignedTypes
class FortItemSeriesDefinition : UExport {
    override var baseObject: UObject

    var displayName : FText? = null
    var colors : MutableMap<String, Color>
    var backgroundTexture : FSoftObjectPath? = null

    constructor() : super("FortItemSeriesDefinition") {
        baseObject = UObject(
            mutableListOf(),
            null,
            "FortItemSeriesDefinition"
        )
        colors = mutableMapOf()
    }

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        displayName = baseObject.getOrNull("DisplayName")
        colors = baseObject.get<FStructFallback>("Colors").properties.associateBy { it.name.text }.mapValues { (it.value.getTagTypeValueLegacy() as FLinearColor).toColor() }.toMutableMap()
        backgroundTexture = baseObject.getOrNull("BackgroundTexture")
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }

    override fun applyLocres(locres : Locres?) {
        displayName?.applyLocres(locres)
    }
}