package me.fungames.jfortniteparse.ue4.objects.engine.editorframework

import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive

class UAssetImportData : UObject() {
    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        if (Ar.ver >= 464 /*VER_UE4_ASSET_IMPORT_DATA_AS_JSON*/) {
            if (!Ar.isFilterEditorOnly) {
                val json = Ar.readString()
            }
        }
        super.deserialize(Ar, validPos)
    }
}