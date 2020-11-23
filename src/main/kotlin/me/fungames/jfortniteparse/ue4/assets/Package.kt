package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.versions.Ue4Version

abstract class Package(val fileName: String,
                       val provider: FileProvider? = null,
                       val game: Ue4Version = provider?.game ?: Ue4Version.GAME_UE4_LATEST) : UObject() {
    abstract val exports: List<UExport>
    var packageFlags = 0

    /**
     * @return the first export of the given type
     * @throws IllegalArgumentException if there is no export of the given type
     */
    @Throws(IllegalArgumentException::class)
    inline fun <reified T : UExport> getExportOfType() = getExportsOfType<T>().first()

    /**
     * @return the first export of the given type or null if there is no
     */
    inline fun <reified T : UExport> getExportOfTypeOrNull() = getExportsOfType<T>().firstOrNull()

    /**
     * @return the all exports of the given type
     */
    inline fun <reified T : UExport> getExportsOfType() = exports.filterIsInstance<T>()

    inline fun <reified T> loadObject(index: FPackageIndex?) = index?.let { loadObjectGeneric(it) as? T }
    abstract fun loadObjectGeneric(index: FPackageIndex?): UExport?

    fun findExport(objectName: String, className: String? = null): UExport? {
        if (this is PakPackage) {
            val export = exportMap.firstOrNull {
                it.objectName.text.equals(objectName, true) && (className == null || it.classIndex.getImportObject()?.objectName?.text == className)
            }
            if (export != null) return export.exportObject.value
        } else if (this is IoPackage) {
            val export = exportMap.firstOrNull {
                nameMap.getName(it.exportMapEntry.objectName).text.equals(objectName, true) && (className == null || it.exportMapEntry.classIndex.findScriptObjectEntry()!!.objectName.toName().toString() == className)
            }
            if (export != null) return export.exportObject!! // no lazy exports for IoPackage yet
        }
        FileProvider.logger.warn { "Couldn't find object in external package" }
        return null
    }

    override fun toString() = fileName

    companion object {
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(JsonSerializer.importSerializer)
            .registerTypeAdapter(JsonSerializer.exportSerializer)
            .create()
    }
}