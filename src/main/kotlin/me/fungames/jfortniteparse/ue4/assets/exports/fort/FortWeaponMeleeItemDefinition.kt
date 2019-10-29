package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.assets.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UEExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FortWeaponMeleeItemDefinition : UEExport {
    override var baseObject: UObject
    var largePreviewImage : FSoftObjectPath
    var smallPreviewImage : FSoftObjectPath
    // has more properties, just images needed for now

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        largePreviewImage = baseObject.get("LargePreviewImage")
        smallPreviewImage = baseObject.get("SmallPreviewImage")
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}