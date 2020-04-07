package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPackageIndex : UClass {
    var index: Int
    var importMap : List<FObjectImport>
    val importObject : FObjectImport?
        get() = when {
            index < 0 -> importMap.getOrNull((index * -1) - 1)
            index > 0 -> importMap.getOrNull(index - 1)
            else -> null
        }
    val outerImportObject : FObjectImport?
        get() = this.importObject?.outerIndex?.importObject ?: this.importObject

    val importName: String
        get() = importObject?.objectName?.text ?: index.toString()

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        index = Ar.readInt32()
        super.complete(Ar)
        importMap = Ar.importMap
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(index)
        super.completeWrite(Ar)
    }

    constructor(index: Int, importMap: List<FObjectImport>) {
        this.index = index
        this.importMap = importMap
    }

    private fun getPackage(index: Int, importMap: List<FObjectImport>): FObjectImport? {
        return when {
            index < 0 -> importMap.getOrNull((index * -1) - 1)
            index > 0 -> importMap.getOrNull(index - 1)
            else -> null
        }
    }

    override fun toString() = importObject?.toString() ?: "Invalid Import"
}