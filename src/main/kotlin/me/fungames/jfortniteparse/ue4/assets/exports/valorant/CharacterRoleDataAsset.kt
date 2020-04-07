package me.fungames.jfortniteparse.ue4.assets.exports.valorant

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.FPackageIndex
import me.fungames.jfortniteparse.ue4.assets.objects.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.assets.objects.FText
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class CharacterRoleDataAsset : UExport {
    override var baseObject: UObject
    var uiData : FSoftObjectPath
    var uuid : FGuid

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        uiData = baseObject.get("UIData")
        uuid = baseObject.get("Uuid")
        super.complete(Ar)
    }

    constructor(exportType: String, baseObject: UObject, uiData: FSoftObjectPath, uuid: FGuid) : super(exportType) {
        this.baseObject = baseObject
        this.uiData = uiData
        this.uuid = uuid
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}