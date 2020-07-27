package me.fungames.jfortniteparse.ue4.objects.engine.animation

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class FSmartName : UClass {
    var displayName: FName

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        displayName = Ar.readFName()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(displayName)
        super.completeWrite(Ar)
    }

    constructor(displayName: FName) {
        this.displayName = displayName
    }
}