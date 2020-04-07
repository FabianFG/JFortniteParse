package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@Suppress("EXPERIMENTAL_API_USAGE")
class FSoftObjectPath : UClass {
    var assetPathName: FName
    var subPathString: String

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        assetPathName = Ar.readFName()
        subPathString = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(assetPathName)
        Ar.writeString(subPathString)
        super.completeWrite(Ar)
    }

    constructor(assetPathName: FName, subPathString: String) {
        this.assetPathName = assetPathName
        this.subPathString = subPathString
    }

    override fun toString() = "$assetPathName$subPathString"
}