package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.FPackageIndex
import me.fungames.jfortniteparse.ue4.assets.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.locres.FnLanguage
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.pak.GameFile

@Suppress("EXPERIMENTAL_API_USAGE")
interface FileProvider {

    /**
     * @return the name of the game that is loaded by the provider
     */
    fun getGameName() : String
    /**
     * Searches for a gamefile by its path
     * @param filePath the path to search for
     * @return the game file or null if it wasn't found
     */
    fun findGameFile(filePath : String) : GameFile?

    /**
     * Searches for the game file and then load its contained package
     * @param softObjectPath the soft object reference
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    fun loadGameFile(softObjectPath: FSoftObjectPath) = loadGameFile(softObjectPath.assetPathName.text)

    /**
     * Searches for the game file and then load its contained package
     * @param pkgIndex the package index
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    fun loadGameFile(pkgIndex: FPackageIndex) = if (pkgIndex.outerImportObject == null)
        null
    else
        loadGameFile(pkgIndex.outerImportObject!!.objectName.text)

    /**
     * Searches for the game file and then load its contained package
     * @param filePath the path to search for
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    @Throws(ParserException::class)
    fun loadGameFile(filePath : String) : Package?

    /**
     * Loads a UE4 Package
     * @param file the game file to load
     * @return the parsed package or null if the file was not an ue4 package (.uasset)
     */
    @Throws(ParserException::class)
    fun loadGameFile(file : GameFile) : Package?

    /**
     * Searches for the game file and then load its contained locres
     * @param filePath the path to search for
     * @return the parsed package or null if the path was not found or the found game file was not an ue4 package (.uasset)
     */
    @Throws(ParserException::class)
    fun loadLocres(filePath : String) : Locres?

    /**
     * Loads a UE4 Locres file
     * @param file the game file to load
     * @return the parsed locres or null if the file was not an ue4 locres (.locres)
     */
    @Throws(ParserException::class)
    fun loadLocres(file : GameFile) : Locres?

    fun loadLocres(ln : FnLanguage) = loadLocres(ln.path)

    /**
     * Searches for the game file and then saves all parts of this package
     * @param filePath the path to search for
     * @return a map with the files name as key and data as value
     */
    fun savePackage(filePath : String) : Map<String, ByteArray>

    /**
     * Saves all parts of this package
     * @param file the game file to save
     * @return a map with the files name as key and data as value
     */
    fun savePackage(file: GameFile) : Map<String, ByteArray>

    /**
     * Searches for the game file and then saves the it
     * @param filePath the game file to save
     * @return the files data
     */
    fun saveGameFile(filePath : String) : ByteArray?

    /**
     * Saves the game file
     * @param file the game file to save
     * @return the files data
     */
    fun saveGameFile(file: GameFile) : ByteArray

    /**
     * @param filePath the file path to be fixed
     * @return the file path translated into the correct format
     */
    fun fixPath(filePath: String): String {
        var path = filePath
        path = path.replace('\\', '/')
        if (path.startsWith('/'))
            path = path.substring(1)
        val lastPart = path.substringAfterLast('/')
        if (lastPart.contains('.') && lastPart.substringBefore('.') == lastPart.substringAfter('.'))
            path = path.substringBeforeLast('/') + "/" + lastPart.substringBefore('.')
        if (!path.substringAfterLast('/').contains('.'))
            path += ".uasset"
        if (path.startsWith("Game/")) {
            val gameName = getGameName()
            path = when {
                path.startsWith("Game/Content/") -> path.replaceFirst("Game/Content/", gameName + "Game/Content/")
                path.startsWith("Game/Config/") -> path.replaceFirst("Game/Config/", gameName + "Game/Config/")
                path.startsWith("Game/Plugins") -> path.replaceFirst("Game/Plugins/", gameName + "Game/Plugins/")
                else -> path.replaceFirst("Game/", gameName + "Game/Content/")
            }
        }
        return path
    }
}