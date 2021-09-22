package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.IoPackage
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.mappings.ReflectionTypeMappingsProvider
import me.fungames.jfortniteparse.ue4.assets.mappings.TypeMappingsProvider
import me.fungames.jfortniteparse.ue4.io.FIoChunkId
import me.fungames.jfortniteparse.ue4.io.TOC_READ_OPTION_READ_ALL
import me.fungames.jfortniteparse.ue4.locres.FnLanguage
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.registry.AssetRegistry
import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import mu.KotlinLogging

abstract class FileProvider {
    companion object {
        val logger = KotlinLogging.logger("FileProvider")
    }

    abstract var versions: VersionContainer
    var game: Int
        inline get() = versions.game
        set(value) {
            versions.game = value
        }
    var ver: Int
        inline get() = versions.ver
        set(value) {
            versions.ver = value
        }
    var mappingsProvider: TypeMappingsProvider = ReflectionTypeMappingsProvider()
    var ioStoreTocReadOptions = TOC_READ_OPTION_READ_ALL
    protected abstract val files: MutableMap<String, GameFile>

    open fun files(): Map<String, GameFile> = files

    /**
     * @return the name of the game that is loaded by the provider
     */
    open val gameName: String
        get() {
            if (_gameName.isEmpty()) {
                _gameName = files.keys.firstOrNull { !it.substringBefore('/').endsWith("engine") }?.substringBefore('/') ?: ""
            }
            return _gameName
        }
    private var _gameName = ""

    /**
     * Searches for a game file by its path
     * @param filePath the path to search for
     * @return the game file or null if it wasn't found
     */
    abstract fun findGameFile(filePath: String): GameFile?

    /**
     * Searches for the game file and then load its contained package
     * @param filePath the path to search for
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    @Throws(ParserException::class)
    abstract fun loadGameFile(filePath: String): Package?

    /**
     * Loads a UE4 package
     * @param file the game file to load
     * @return the parsed package or null if the file was not an ue4 package (.uasset)
     */
    @Throws(ParserException::class)
    abstract fun loadGameFile(file: GameFile): Package?

    /**
     * Loads a UE4 package from I/O Store by package ID.
     * @param packageId the package ID to load.
     * @return the parsed package
     */
    @Throws(ParserException::class)
    abstract fun loadGameFile(packageId: FPackageId): IoPackage?

    // Load object by object path string
    inline fun <reified T> loadObject(objectPath: String?) = loadObject(objectPath) as? T

    fun loadObject(objectPath: String?): UObject? {
        if (objectPath == null || objectPath == "None") return null
        var packagePath = if (objectPath.endsWith(".uasset") || objectPath.endsWith(".umap")) objectPath.substringBeforeLast('.') else objectPath
        val objectName: String
        val dotIndex = packagePath.indexOf('.')
        if (dotIndex == -1) { // use the package name as object name
            objectName = packagePath.substringAfterLast('/')
        } else { // packagePath.objectName
            objectName = packagePath.substring(dotIndex + 1)
            packagePath = packagePath.substring(0, dotIndex)
        }
        val pkg = loadGameFile(packagePath)
        return pkg?.findObjectByName(objectName)?.value
    }

    // Load object by FSoftObjectPath
    inline fun <reified T> loadObject(softObjectPath: FSoftObjectPath?): T? {
        if (softObjectPath == null) return null
        val loaded = loadObject(softObjectPath) ?: return null
        return loaded as? T
    }

    fun loadObject(softObjectPath: FSoftObjectPath?): UObject? {
        if (softObjectPath == null) return null
        val path = softObjectPath.toString()
        return if (path.isNotEmpty() && path != "None") loadObject(path) else null
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
    abstract fun loadAssetRegistry(filePath: String): AssetRegistry?

    /**
     * Loads a UE4 AssetRegistry file
     * @param file the game file to load
     * @return the parsed asset registry
     */
    abstract fun loadAssetRegistry(file: GameFile): AssetRegistry?

    open fun getLocresLanguageByPath(filePath: String) = FnLanguage.valueOfLanguageCode(filePath.split("Localization/(.*?)/".toRegex())[1].takeWhile { it != '/' })

    open fun loadLocres(ln: FnLanguage): Locres? {
        val files = files.values
            .filter { it.path.startsWith("${gameName}Game/Content/Localization", ignoreCase = true) && it.path.contains("/${ln.languageCode}/", ignoreCase = true) && it.path.endsWith(".locres") }
        if (files.isEmpty()) return null
        var first: Locres? = null
        files.forEach { file ->
            runCatching {
                val f = first
                if (f == null) {
                    first = loadLocres(file)
                } else {
                    loadLocres(file)?.mergeInto(f)
                }
            }.onFailure { logger.warn(it) { "Failed to locres file ${file.getName()}" } }
        }
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
    abstract fun saveGameFile(filePath: String): ByteArray?

    /**
     * Saves the game file
     * @param file the game file to save
     * @return the files data
     */
    abstract fun saveGameFile(file: GameFile): ByteArray

    /**
     * Saves a I/O Store chunk by its ID
     * @param chunkId the chunk ID
     * @return the chunk data
     */
    abstract fun saveChunk(chunkId: FIoChunkId): ByteArray

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
            val gameName = gameName
            path = when {
                path.startsWith("game/content/") -> path.replaceFirst("game/content/", "$gameName/content/")
                path.startsWith("game/config/") -> path.replaceFirst("game/config/", "$gameName/config/")
                path.startsWith("game/plugins") -> path.replaceFirst("game/plugins/", "$gameName/plugins/")
                // For files like Game/AssetRegistry.bin
                // path.startsWith("game/") && path.substringAfter('/').substringBefore('/').contains('.') -> ...
                // ^ didn't work that way for normal assets at root, hacky fix below
                path.contains("assetregistry") || path.endsWith(".uproject") -> path.replace("game/", "$gameName/")
                else -> path.replaceFirst("game/", "$gameName/content/")
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

    // WARNING: This does convert FortniteGame/Plugins/GameFeatures/GameFeatureName/Content/Package into /GameFeatureName/Package
    fun compactFilePath(path: String): String {
        if (path[0] == '/') {
            return path
        }
        if (path.startsWith("Engine/Content")) { // -> /Engine
            return "/Engine" + path.substring("Engine/Content".length)
        }
        if (path.startsWith("Engine/Plugins")) { // -> /Plugins
            return path.substring("Engine".length)
        }
        val delim = path.indexOf("/Content/")
        return if (delim == -1) {
            path
        } else { // GameName/Content -> /Game
            "/Game" + path.substring(delim + "/Content".length)
        }
    }
}