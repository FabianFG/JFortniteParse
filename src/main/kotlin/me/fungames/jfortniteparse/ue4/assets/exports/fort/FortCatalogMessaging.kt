package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.*
import me.fungames.jfortniteparse.ue4.assets.exports.UEExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres

@ExperimentalUnsignedTypes
class FortCatalogMessaging : UEExport {
    override var baseObject: UObject
    var banners : Map<String, FText>
    var storeToastHeader : Map<String, FText>
    var storeToastBody : Map<String, FText>

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        banners = baseObject.get<UScriptMap>("Banners").mapData.mapKeys { it.key.getTagTypeValue() as String }.mapValues { it.value.getTagTypeValue() as FText }
        storeToastHeader = baseObject.get<UScriptMap>("StoreToast_Header").mapData.mapKeys { it.key.getTagTypeValue() as String }.mapValues { it.value.getTagTypeValue() as FText }
        storeToastBody = baseObject.get<UScriptMap>("StoreToast_Body").mapData.mapKeys { it.key.getTagTypeValue() as String }.mapValues { it.value.getTagTypeValue() as FText }
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