package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.pak.PakFileReader
import mu.KotlinLogging
import java.io.File

@Suppress("EXPERIMENTAL_API_USAGE")
class DefaultFileProvider(val folder : File) : FileProvider {

    companion object {
        val logger = KotlinLogging.logger("JFortniteParse")
    }

    private val localFiles = mutableMapOf<String, File>()
    private val files = mutableMapOf<String, GameFile>()
    private val unloadedPaks = mutableListOf<PakFileReader>()
    private val requiredKeys = mutableListOf<FGuid>()
    private val mountedPaks = mutableListOf<PakFileReader>()


    init {
        scanFiles(folder)
    }

    override fun getGameName() = files.keys.firstOrNull { it.substringBefore('/').endsWith("Game") }?.substringBefore("Game") ?: ""


    private fun scanFiles(folder : File) {
        val folderFiles = folder.listFiles()!!
        folderFiles.forEach {
            if (it.isDirectory)
                scanFiles(it)
            else if (it.isFile && it.extension == "pak") {
                try {
                    val reader = PakFileReader(it)
                    if (!reader.isEncrypted()) {
                        reader.readIndex()
                        reader.files.associateByTo(files, {file -> file.path})
                        mountedPaks.add(reader)
                    } else {
                        unloadedPaks.add(reader)
                        if(!requiredKeys.contains(reader.pakInfo.encryptionKeyGuid))
                            requiredKeys.add(reader.pakInfo.encryptionKeyGuid)
                    }
                } catch (e : ParserException) {
                    logger.error { e.message }
                }
            }
            else if (it.isFile) {
                    var gamePath = it.absolutePath.substringAfter(this.folder.absolutePath)
                    if (gamePath.startsWith('\\') || gamePath.startsWith('/'))
                        gamePath = gamePath.substring(1)
                    gamePath = gamePath.replace('\\', '/')
                    localFiles[gamePath] = it
            }
        }
    }

    fun requiredKeys() : List<FGuid> = requiredKeys
    fun submitKey(guid : FGuid, key : String) = submitKeys(mapOf(guid to key))
    fun submitKeys(keys : Map<FGuid, String>): Int {
        var countNewMounts = 0
        keys.forEach { (guid, key) ->
            if (requiredKeys.contains(guid)) {
                val oldCountMounts = countNewMounts
                getUnloadedPakFilesByGuid(guid).forEach {
                    if (it.testAesKey(key)) {
                        it.aesKey = key
                        it.readIndex()
                        it.files.associateByTo(files, {file -> file.path})
                        unloadedPaks.remove(it)
                        mountedPaks.add(it)
                        countNewMounts++
                    }
                }
                if (countNewMounts > oldCountMounts)
                    requiredKeys.remove(guid)
            }
        }
        return countNewMounts
    }

    private fun getUnloadedPakFilesByGuid(guid: FGuid) = unloadedPaks.filter { it.pakInfo.encryptionKeyGuid == guid }

    override fun findGameFile(filePath: String): GameFile? {
        val path = fixPath(filePath)
        return files[path]
    }

    override fun loadGameFile(filePath: String): Package? {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return loadGameFile(gameFile)
        if (!path.endsWith(".uasset"))
            return null
        val uasset = saveGameFile(path) ?: return null
        val uexp = saveGameFile(path.substringBeforeLast(".uasset") + ".uexp") ?: return null
        val ubulk = saveGameFile(path.substringBeforeLast(".uasset") + ".ubulk")
        return try {
            Package(uasset, uexp, ubulk, path)
        } catch (e : ParserException) {
            logger.error("Failed to load package $path", e)
            null
        }

    }

    override fun savePackage(filePath: String): Map<String, ByteArray> {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return savePackage(gameFile)
        val map = mutableMapOf<String, ByteArray>()
        if (path.endsWith(".uasset")) {
            val uasset = saveGameFile(path) ?: return map
            map[path] = uasset
            val uexpPath = path.substringBeforeLast(".uasset") + ".uexp"
            val uexp = saveGameFile(uexpPath) ?: return map
            map[uexpPath] = uexp
            val ubulkPath = path.substringBeforeLast(".uasset") + ".ubulk"
            val ubulk = saveGameFile(ubulkPath) ?: return map
            map[ubulkPath] = ubulk
        } else {
            val data = saveGameFile(path) ?: return map
            map[path] = data
        }
        return map
    }

    override fun saveGameFile(filePath: String): ByteArray? {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return saveGameFile(gameFile)
        var file = localFiles[path]
        if (file == null) {
            val justName = path.substringAfterLast('/')
            file = localFiles[justName]
        }
        if (file == null) {
            if (path.startsWith("Game/"))
                file = localFiles.filterKeys {
                    if (it.contains("Game/"))
                        it.substringAfter("Game/") == path.substringAfter("Game/")
                    else
                        false
                }.values.firstOrNull()
        }
        return file?.readBytes()
    }

    override fun loadGameFile(file: GameFile): Package? {
        if (!file.isUE4Package() || !file.hasUexp())
            return null
        val uasset = saveGameFile(file)
        val uexp = saveGameFile(file.uexp)
        val ubulk = if (file.hasUbulk()) saveGameFile(file.ubulk!!) else null
        return try {
            Package(uasset, uexp, ubulk, file.path)
        } catch (e : Exception) {
            logger.error("Failed to load package ${file.path}", e)
            null
        }
    }

    override fun savePackage(file: GameFile): Map<String, ByteArray> {
        val map = mutableMapOf<String, ByteArray>()
        if (!file.isUE4Package() || !file.hasUexp()) {
            val data = saveGameFile(file)
            map[file.path] = data
        } else {
            val uasset = saveGameFile(file)
            map[file.path] = uasset
            val uexp = saveGameFile(file.uexp)
            map[file.uexp.path] = uexp
            val ubulk = if (file.hasUbulk()) saveGameFile(file.ubulk!!) else null
            if (ubulk != null)
                map[file.ubulk!!.path] = ubulk
        }
        return map
    }

    override fun saveGameFile(file: GameFile): ByteArray {
        val reader = mountedPaks.filter { it.fileName == file.pakFileName }.firstOrNull() ?: throw IllegalArgumentException("Couldn't find any possible pak file readers")
        return reader.extract(file)
    }
}