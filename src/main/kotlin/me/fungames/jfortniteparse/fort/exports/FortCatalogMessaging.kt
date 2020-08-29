package me.fungames.jfortniteparse.fort.exports

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.UScriptMap
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
class FortCatalogMessaging(exportObject: FObjectExport) : UObject(exportObject) {
    lateinit var banners: Map<String, FText>
    lateinit var storeToastHeader: Map<String, FText>
    lateinit var storeToastBody: Map<String, FText>

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        banners = get<UScriptMap>("Banners").mapData.mapKeys { it.key.getTagTypeValueLegacy() as String }.mapValues { it.value.getTagTypeValueLegacy() as FText }
        storeToastHeader = get<UScriptMap>("StoreToast_Header").mapData.mapKeys { it.key.getTagTypeValueLegacy() as String }.mapValues { it.value.getTagTypeValueLegacy() as FText }
        storeToastBody = get<UScriptMap>("StoreToast_Body").mapData.mapKeys { it.key.getTagTypeValueLegacy() as String }.mapValues { it.value.getTagTypeValueLegacy() as FText }
    }
}