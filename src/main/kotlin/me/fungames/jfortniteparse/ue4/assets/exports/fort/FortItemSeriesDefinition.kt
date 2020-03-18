package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.*
import me.fungames.jfortniteparse.ue4.assets.exports.UEExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres

@ExperimentalUnsignedTypes
class FortItemSeriesDefinition : UEExport {
    override var baseObject: UObject

    var displayName : FText? = null
    var colors : Map<String, FLinearColor>
    var backgroundTexture : FSoftObjectPath? = null

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        displayName = baseObject.getOrNull("DisplayName")
        colors = baseObject.get<FStructFallback>("Colors").properties.associateBy { it.name.text }.mapValues { it.value.getTagTypeValue() as FLinearColor }
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