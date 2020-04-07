package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FNavAgentSelectorCustomization : UClass {
    var supportedDesc : FText

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        supportedDesc = FText(Ar)
        super.complete(Ar)
    }

    constructor(supportedDesc: FText) {
        this.supportedDesc = supportedDesc
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        supportedDesc.serialize(Ar)
        super.completeWrite(Ar)
    }

}