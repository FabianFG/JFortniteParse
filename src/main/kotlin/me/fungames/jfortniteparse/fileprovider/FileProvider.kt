@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.locres.FnLanguage
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectImport
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.registry.AssetRegistry
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import mu.KotlinLogging

abstract class FileProvider {
    companion object {
        val logger = KotlinLogging.logger("FileProvider")
    }

    abstract var game : Ue4Version
    protected abstract val files : MutableMap<String, GameFile>

    open fun files() : Map<String, GameFile> = files

    /**
     * @return the name of the game that is loaded by the provider
     */
    open fun getGameName() = files.keys.firstOrNull { it.substringBefore('/').endsWith("game") }?.substringBefore("game") ?: ""

    /**
     * Searches for a gamefile by its path
     * @param filePath the path to search for
     * @return the game file or null if it wasn't found
     */
    abstract fun findGameFile(filePath : String) : GameFile?

    /**
     * Searches for the game file and then load its contained package
     * @param filePath the path to search for
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    @Throws(ParserException::class)
    abstract fun loadGameFile(filePath : String) : Package?

    /**
     * Loads a UE4 Package
     * @param file the game file to load
     * @return the parsed package or null if the file was not an ue4 package (.uasset)
     */
    @Throws(ParserException::class)
    abstract fun loadGameFile(file: GameFile): Package?

    // Load object by object path string
    inline fun <reified T> loadObject(objectPath: String?): T? {
        if (objectPath == null) return null
        val loaded = loadObject(objectPath) ?: return null
        return if (loaded is T) loaded else null
    }

    fun loadObject(objectPath: String?): UExport? {
        if (objectPath == null) return null
        var packagePath = objectPath
        val objectName: String
        val dotIndex = packagePath.indexOf('.')
        if (dotIndex == -1) { // use the package name as object name
            objectName = packagePath.substringAfterLast('/')
        } else { // packagePath.objectName
            objectName = packagePath.substring(dotIndex + 1)
            packagePath = packagePath.substring(0, dotIndex)
        }
        val pkg = loadGameFile(packagePath) // TODO allow reading umaps via this route, currently fixPath() only appends .uasset
        if (pkg != null) {
            val export = pkg.exportMap.firstOrNull { it.objectName.text.equals(objectName,true) }
            if (export != null) return export.exportObject.value
            else logger.warn { "Couldn't find object in external package" }
        } else logger.warn { "Failed to load SoftObjectPath" }
        return null
    }

    // Load object by FSoftObjectPath
    inline fun <reified T> loadObject(softObjectPath: FSoftObjectPath?): T? {
        if (softObjectPath == null) return null
        val loaded = loadObject(softObjectPath) ?: return null
        return if (loaded is T) loaded else null
    }

    fun loadObject(softObjectPath: FSoftObjectPath?) = softObjectPath?.run { loadObject(softObjectPath.assetPathName.text) }

    // Load object by FPackageIndex
    inline fun <reified T> loadObject(index: FPackageIndex?): T? {
        if (index == null) return null
        val loaded = loadObject(index) ?: return null
        return if (loaded is T) loaded else null
    }

    fun loadObject(index: FPackageIndex?): UExport? {
        if (index == null || index.index == 0) return null
        val import = index.importObject
        if (import != null) return loadImport(import)
        val export = index.exportObject
        if (export != null) return export.exportObject.value
        return null
    }

    // Load object by FObjectImport
    inline fun <reified T> loadImport(import: FObjectImport?): T? {
        if (import == null) return null
        val loaded = loadImport(import) ?: return null
        return if (loaded is T) loaded else null
    }

    fun loadImport(import: FObjectImport?): UExport? {
        //The needed export is located in another asset, try to load it
        if (import == null || import.outerIndex.importObject == null) return null
        val pkg = loadGameFile(import.outerIndex.importObject!!.objectName.text)
        if (pkg != null) {
            val export = pkg.exportMap.firstOrNull { it.classIndex.name == import.className.text && it.objectName.text == import.objectName.text }
            if (export != null) return export.exportObject.value
            else logger.warn { "Couldn't find object in external package" }
        } else logger.warn { "Failed to load referenced import" }
        return null
    }

    /**
     * Searches for the game file and then load its contained locres
     * @param filePath the path to search for
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    @Throws(ParserException::class)
    abstract fun loadLocres(filePath: String): Locres?

    /**
     * Loads a UE4 Locres file
     * @param file the game file to load
     * @return the parsed locres or null if the file was not an ue4 locres (.locres)
     */
    @Throws(ParserException::class)
    abstract fun loadLocres(file: GameFile): Locres?

    /**
     * Searches for the game file and then loads a UE4 AssetRegistry file
     * @param filePath the path to search for
     * @return the parsed asset registry
     */
    abstract fun loadAssetRegistry(filePath: String) : AssetRegistry?

    /**
     * Loads a UE4 AssetRegistry file
     * @param file the game file to load
     * @return the parsed asset registry
     */
    abstract fun loadAssetRegistry(file: GameFile) : AssetRegistry?

    open fun getLocresLanguageByPath(filePath: String) = FnLanguage.valueOfLanguageCode(filePath.split("Localization/(.*?)/".toRegex())[1].takeWhile { it != '/' })

    open fun loadLocres(ln : FnLanguage) : Locres? {
        val files = files.values
            .filter { it.path.startsWith("${getGameName()}Game/Content/Localization", ignoreCase = true) && it.path.contains("/${ln.languageCode}/", ignoreCase = true) && it.path.endsWith(".locres") }
        if (files.isEmpty()) return null
        var first : Locres? = null
        files.forEach { file ->
            runCatching {
            val f = first
            if (f == null) {
                first = loadLocres(file)
            } else {
                loadLocres(file)?.mergeInto(f)
            }
        }.onFailure { logger.warn(it) { "Failed to locres file ${file.getName()}" } } }
        return first
    }

    /**
     * Searches for the game file and then saves all parts of this package
     * @param filePath the path to search for
     * @return a map with the files name as key and data as value
     */
    abstract fun savePackage(filePath: String): Map<String, ByteArray>

    /**
     * Saves all parts of this package
     * @param file the game file to save
     * @return a map with the files name as key and data as value
     */
    abstract fun savePackage(file: GameFile): Map<String, ByteArray>

    /**
     * Searches for the game file and then saves the it
     * @param filePath the game file to save
     * @return the files data
     */
    abstract fun saveGameFile(filePath : String) : ByteArray?

    /**
     * Saves the game file
     * @param file the game file to save
     * @return the files data
     */
    abstract fun saveGameFile(file: GameFile) : ByteArray

    /**
     * @param filePath the file path to be fixed
     * @return the file path translated into the correct format
     */
    open fun fixPath(filePath: String): String {
        var path = filePath.toLowerCase()
        path = path.replace('\\', '/')
        if (path.startsWith('/'))
            path = path.substring(1)
        val lastPart = path.substringAfterLast('/')
        //Not needed anymore, this was only needed for FSoftObjectPaths. The last part of the file name is now removed by them
        if (lastPart.contains('.') && lastPart.substringBefore('.') == lastPart.substringAfter('.'))
            path = path.substringBeforeLast('/') + "/" + lastPart.substringBefore('.')
        if (!path.endsWith('/') && !path.substringAfterLast('/').contains('.'))
            path += ".uasset"
        if (path.startsWith("game/")) {
            val gameName = getGameName()
            path = when {
                path.startsWith("game/content/") -> path.replaceFirst("game/content/", gameName + "game/content/")
                path.startsWith("game/config/") -> path.replaceFirst("game/config/", gameName + "game/config/")
                path.startsWith("game/plugins") -> path.replaceFirst("game/plugins/", gameName + "game/plugins/")
                // For files like Game/AssetRegistry.bin
                path.startsWith("game/") && path.substringAfter('/').substringBefore('/').contains('.') -> path.replace("game/", "${gameName}game/")
                else -> path.replaceFirst("game/", gameName + "game/content/")
            }
        } else if (path.startsWith("engine/")) {
            path = when {
                path.startsWith("engine/content/") -> path
                path.startsWith("engine/config/") -> path
                path.startsWith("engine/plugins") -> path
                else -> path.replaceFirst("engine/", "engine/content/")
            }
        }
        return path.toLowerCase()
    }
}