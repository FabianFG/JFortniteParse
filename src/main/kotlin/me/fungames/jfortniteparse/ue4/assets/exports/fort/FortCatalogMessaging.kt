package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.UScriptMap
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
class FortCatalogMessaging : UExport {
    override var baseObject: UObject
    var banners : Map<String, FText>
    var storeToastHeader : Map<String, FText>
    var storeToastBody : Map<String, FText>

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        banners = baseObject.get<UScriptMap>("Banners").mapData.mapKeys { it.key.getTagTypeValueLegacy() as String }.mapValues { it.value.getTagTypeValueLegacy() as FText }
        storeToastHeader = baseObject.get<UScriptMap>("StoreToast_Header").mapData.mapKeys { it.key.getTagTypeValueLegacy() as String }.mapValues { it.value.getTagTypeValueLegacy() as FText }
        storeToastBody = baseObject.get<UScriptMap>("StoreToast_Body").mapData.mapKeys { it.key.getTagTypeValueLegacy() as String }.mapValues { it.value.getTagTypeValueLegacy() as FText }
        super.complete(Ar)
    }

    override fun applyLocres(locres: Locres?) {
        banners.forEach { (_, text) ->  text.applyLocres(locres)}
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}