package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPackageIndex : UClass {
    var index: Int
    var importMap : List<FObjectImport>
    var exportMap : List<FObjectExport>
    val importObject : FObjectImport?
        get() = when {
            index < 0 -> importMap.getOrNull((index * -1) - 1)
            //index > 0 -> importMap.getOrNull(index - 1) everything above 0 is an export not an import
            else -> null
        }
    val outerImportObject : FObjectImport?
        get() = this.importObject?.outerIndex?.importObject ?: this.importObject

    val exportObject : FObjectExport?
        get() = when {
            index > 0 -> exportMap.getOrNull(index - 1)
            //index < 0 -> exportMap.getOrNull(index - 1) everything below 0 is an import not an export
            else -> null
        }

    val name: String
        get() = importObject?.objectName?.text
            ?: exportObject?.objectName?.text
            ?: index.toString()

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        index = Ar.readInt32()
        super.complete(Ar)
        importMap = Ar.importMap
        exportMap = Ar.exportMap
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(index)
        super.completeWrite(Ar)
    }

    constructor(index: Int, importMap: List<FObjectImport>, exportMap : List<FObjectExport>) {
        this.index = index
        this.importMap = importMap
        this.exportMap = exportMap
    }

    override fun toString() = importObject?.objectName?.text?.let { "Import: $it" }
        ?: exportObject?.objectName?.text?.let { "Export: $it" }
        ?: index.toString()
}