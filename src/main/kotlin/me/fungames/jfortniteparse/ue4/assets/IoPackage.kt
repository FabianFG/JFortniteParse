package me.fungames.jfortniteparse.ue4.assets

import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.asyncloading2.FExportObject
import me.fungames.jfortniteparse.ue4.asyncloading2.FNameMap
import me.fungames.jfortniteparse.ue4.asyncloading2.FPackageImportStore
import me.fungames.jfortniteparse.ue4.asyncloading2.FPackageObjectIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.versions.Ue4Version

/**
 * Linker for I/O Store packages
 */
class IoPackage(val importStore: FPackageImportStore,
                val nameMap: FNameMap,
                val exportMap: Array<FExportObject>,
                fileName: String,
                provider: FileProvider? = null,
                game: Ue4Version = provider?.game ?: Ue4Version.GAME_UE4_LATEST) : Package(fileName, provider, game) {

    override val exports: List<UExport>
        get() = exportMap.map { it.exportObject ?: UObject() }
        //get() = exportMap.map { it.exportObject!! }

    override fun loadObjectGeneric(index: FPackageIndex?): UExport? {
        if (index == null || index.isNull()) {
            return null
        } else if (index.isExport()) {
            val exportIndex = index.toExport()
            if (exportIndex < exportMap.size) {
                return exportMap[exportIndex].exportObject
            } /*else {
                handleBadExportIndex(exportIndex)
            }*/
        } else {
            if (importStore.isValidLocalImportIndex(index)) {
                /*val obj = importStore.findOrGetImportObjectFromLocalIndex(index)

                if (obj == null) {
                    asyncPackageLogVerbose(Level.DEBUG, importStore.desc,
                        "FExportArchive: Object", "Import index ${index.toImport()} is null")
                } else {
                    handleBadImportIndex(index.toImport())
                }

                return obj*/
                return loadImport(index.getImportObject())
            }
        }
        return null
    }

    fun loadImport(import: FPackageObjectIndex?): UExport? {
        val entry = import?.findFromGlobal() ?: return null
        check(provider != null) { "Loading an import requires a file provider" }
        val pkg = provider.loadGameFile(entry.outerIndex.findFromGlobal()!!.objectName.toName().toString())
        if (pkg != null) return pkg.findExport(entry.objectName.toName().toString(), entry.cdoClassIndex.findFromGlobal()?.objectName?.toName().toString())
        else FileProvider.logger.warn { "Failed to load referenced import" }
        return null
    }

    // region FPackageIndex methods
    fun FPackageIndex.getImportObject() = if (isImport()) importStore.importMap[toImport()] else null

    fun FPackageIndex.getOuterImportObject(): FPackageObjectIndex? {
        val importObject = getImportObject()
        return importObject?.findFromGlobal()?.outerIndex ?: importObject
    }

    fun FPackageIndex.getExportObject() = if (isExport()) exportMap[toExport()] else null

    fun FPackageObjectIndex.findFromGlobal() = if (isNull()) null else importStore.globalImportStore.scriptObjectEntriesMap[this]
    // endregion
}