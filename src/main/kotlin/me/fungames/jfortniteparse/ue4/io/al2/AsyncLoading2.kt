@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

typealias FSourceToLocalizedPackageIdMap = Array<Pair<FPackageId, FPackageId>>
typealias FCulturePackageMap = Map<String, FSourceToLocalizedPackageIdMap>

var GIsInitialLoad = true
val GUObjectArray = FUObjectArray()

fun GFindExistingScriptImport(
    globalImportIndex: FPackageObjectIndex,
    scriptObjects: MutableMap<FPackageObjectIndex, UObject?>,
    scriptObjectEntriesMap: Map<FPackageObjectIndex, FScriptObjectEntry>): UObject? =
    scriptObjects.getOrPut(globalImportIndex) {
        val entry = scriptObjectEntriesMap[globalImportIndex]
        check(entry != null)
        var obj: UObject?
        if (entry.outerIndex.isNull()) {
            obj = staticFindObjectFast(Package::class.java, null, entry.objectName.toName(), true)
        } else {
            val outer = GFindExistingScriptImport(entry.outerIndex, scriptObjects, scriptObjectEntriesMap)
            obj = scriptObjects[globalImportIndex] ?: throw AssertionError()
            if (outer != null) {
                obj = staticFindObjectFast(UObject::class.java, outer, entry.objectName.toName(), false, true)
            }
        }
        obj
    }

fun staticFindObjectFast(clazz: Class<*>, outer: UObject?, name: FName, exactClass: Boolean, anyPackage: Boolean = false): UObject? {
    TODO("Not yet implemented")
}