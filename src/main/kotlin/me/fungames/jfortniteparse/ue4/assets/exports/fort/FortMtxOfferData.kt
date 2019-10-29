package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.FPackageIndex
import me.fungames.jfortniteparse.ue4.assets.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UEExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FortMtxOfferData : UEExport {
    override var baseObject: UObject
    var detailsImage : FPackageIndex
    var tileImage : FPackageIndex

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        detailsImage = baseObject.get<FStructFallback>("DetailsImage").get("ResourceObject")
        tileImage = baseObject.get<FStructFallback>("TileImage").get("ResourceObject")
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        super.completeWrite(Ar)
    }
}