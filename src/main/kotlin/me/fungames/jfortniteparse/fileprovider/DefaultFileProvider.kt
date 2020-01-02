package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider.Companion.logger
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.pak.PakFileReader
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.LATEST_SUPPORTED_UE4_VERSION
import mu.KotlinLogging
import java.io.File

@Suppress("EXPERIMENTAL_API_USAGE")
class DefaultFileProvider(val folder : File, override var game : Int = GAME_UE4(LATEST_SUPPORTED_UE4_VERSION)) : AbstractFileProvider() {

    private val localFiles = mutableMapOf<String, File>()
    override val files = mutableMapOf<String, GameFile>()
    private val unloadedPaks = mutableListOf<PakFileReader>()
    private val requiredKeys = mutableListOf<FGuid>()
    private val mountedPaks = mutableListOf<PakFileReader>()

    override var defaultLocres : Locres? = null


    init {
        scanFiles(folder)
    }


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

    override fun requiredKeys() : List<FGuid> = requiredKeys
    override fun submitKeys(keys : Map<FGuid, String>): Int {
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

    override fun saveGameFile(file: GameFile): ByteArray {
        val reader = mountedPaks.firstOrNull { it.fileName == file.pakFileName } ?: throw IllegalArgumentException("Couldn't find any possible pak file readers")
        return reader.extract(file)
    }
}