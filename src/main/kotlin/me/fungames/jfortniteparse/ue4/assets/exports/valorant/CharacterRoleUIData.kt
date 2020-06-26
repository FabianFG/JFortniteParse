package me.fungames.jfortniteparse.ue4.assets.exports.valorant

import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.FPackageIndex
import me.fungames.jfortniteparse.ue4.assets.objects.FText
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class CharacterRoleUIData : UExport {
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

    constructor(
        exportType: String,
        baseObject: UObject,
        displayName: FText,
        description: FText,
        displayIcon: FPackageIndex?
    ) : super(exportType) {
        this.baseObject = baseObject
        this.displayName = displayName
        this.description = description
        this.displayIcon = displayIcon
    }


    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}