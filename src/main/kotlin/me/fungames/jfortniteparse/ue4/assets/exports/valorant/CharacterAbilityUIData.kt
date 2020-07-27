package me.fungames.jfortniteparse.ue4.assets.exports.valorant

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FPackageIndex

@ExperimentalUnsignedTypes
class CharacterAbilityUIData : UExport {
    override var baseObject: UObject
    var displayName : FText
    var description : FText
    var displayIcon : FPackageIndex?

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        displayName = baseObject.get("DisplayName")
        description = baseObject.get("Description")
        displayIcon = baseObject.getOrNull("DisplayIcon")
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}