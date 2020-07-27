package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.locres.FnLanguage
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.registry.AssetRegistry
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import mu.KotlinLogging

@Suppress("EXPERIMENTAL_API_USAGE")
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
     * @param softObjectPath the soft object reference
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    open fun loadGameFile(softObjectPath: FSoftObjectPath): Package? {
        var path = softObjectPath.assetPathName.text
        val lastPart = path.substringAfterLast('/')
        if (lastPart.contains('.'))
           path = path.substringBeforeLast('.')
        return loadGameFile(path)
    }

    /**
     * Searches for the game file and then load its contained package
     * @param pkgIndex the package index
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    open fun loadGameFile(pkgIndex: FPackageIndex) = if (pkgIndex.outerImportObject == null)
        null
    else
        loadGameFile(pkgIndex.outerImportObject!!.objectName.text)

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

    abstract var defaultLocres : Locres?

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