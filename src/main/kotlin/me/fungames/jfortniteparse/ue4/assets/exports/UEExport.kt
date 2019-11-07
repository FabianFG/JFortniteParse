package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.assets.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.UObject
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres

@ExperimentalUnsignedTypes
abstract class UEExport(val exportType: String) : UEClass() {
    abstract var baseObject : UObject
    abstract fun serialize(Ar: FAssetArchiveWriter)
    var export : FObjectExport? = null

    constructor(exportObject : FObjectExport) : this(exportObject.classIndex.importName) {
        export = exportObject
    }

    open fun applyLocres(locres : Locres?) {}
}