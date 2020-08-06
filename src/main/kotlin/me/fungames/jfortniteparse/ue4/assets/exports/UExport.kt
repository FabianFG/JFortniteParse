package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport

@ExperimentalUnsignedTypes
abstract class UExport(var exportType: String) : UClass() {
    abstract fun deserialize(Ar: FAssetArchive, validPos: Int)
    abstract fun serialize(Ar: FAssetArchiveWriter)
    var export: FObjectExport? = null
    var name = exportType
    var owner: Package? = null

    constructor(exportObject: FObjectExport) : this(exportObject.classIndex.name) {
        export = exportObject
        name = exportObject.objectName.text
    }

    override fun toString() = name
}