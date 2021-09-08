package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FScriptInterface {
    var `object`: FPackageIndex

    constructor(Ar: FAssetArchive) {
        `object` = FPackageIndex(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        `object`.serialize(Ar)
    }

    constructor(`object`: FPackageIndex = FPackageIndex(0)) {
        this.`object` = `object`
    }
}