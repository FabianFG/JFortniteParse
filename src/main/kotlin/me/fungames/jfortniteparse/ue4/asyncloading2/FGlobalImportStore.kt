package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.assets.exports.UObject

class FGlobalImportStore {
    val scriptObjects = mutableMapOf<FPackageObjectIndex, UObject?>()
    val publicExportObjects = mutableMapOf<FPackageObjectIndex, FPublicExport>()
    val objectIndexToPublicExport = mutableMapOf<Int, FPackageObjectIndex>()

    // Temporary initial load data
    val scriptObjectEntries = mutableListOf<FScriptObjectEntry>()
    val scriptObjectEntriesMap = mutableMapOf<FPackageObjectIndex, FScriptObjectEntry>()

    fun getPublicExportObject(globalIndex: FPackageObjectIndex): UObject? {
        check(globalIndex.isPackageImport())
        return publicExportObjects[globalIndex]?.obj?.also {
            //check(it != null && !it.isUnreachable()) { it?.fullName ?: "null" }
        }
    }

    fun findScriptImportObjectFromIndex(globalImportIndex: FPackageObjectIndex): UObject? {
        check(scriptObjectEntries.isNotEmpty())
        return GFindExistingScriptImport(globalImportIndex, scriptObjects, scriptObjectEntriesMap)
    }

    fun findOrGetImportObject(globalIndex: FPackageObjectIndex): UObject? {
        check(globalIndex.isImport())
        return if (globalIndex.isScriptImport()) {
            if (GIsInitialLoad) {
                findScriptImportObjectFromIndex(globalIndex)
            } else {
                scriptObjects[globalIndex]
            }
        } else {
            getPublicExportObject(globalIndex)
        }
    }

    fun storeGlobalObject(packageId: FPackageId, globalIndex: FPackageObjectIndex, obj: UObject) {
        check(globalIndex.isPackageImport())
        val objectIndex = GUObjectArray.objectToIndex(obj)
        publicExportObjects[globalIndex] = FPublicExport(obj, packageId)
        objectIndexToPublicExport[objectIndex] = globalIndex
    }
}