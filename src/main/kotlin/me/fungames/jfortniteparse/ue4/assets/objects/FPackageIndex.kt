package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPackageIndex : UClass {
    var index: Int
    var owner: Package? = null
    val importObject : FObjectImport?
        get() = when {
            index < 0 -> owner?.importMap?.getOrNull((index * -1) - 1)
            //index > 0 -> importMap.getOrNull(index - 1) everything above 0 is an export not an import
            else -> null
        }
    val outerImportObject : FObjectImport?
        get() = this.importObject?.outerIndex?.importObject ?: this.importObject

    val exportObject : FObjectExport?
        get() = when {
            index > 0 -> owner?.exportMap?.getOrNull(index - 1)
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
        owner = Ar.owner
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(index)
        super.completeWrite(Ar)
    }

    constructor(): this(0)

    constructor(index: Int, owner : Package? = null) {
        this.index = index
    }

    override fun toString() = importObject?.objectName?.text?.let { "Import: $it" }
        ?: exportObject?.objectName?.text?.let { "Export: $it" }
        ?: index.toString()
}