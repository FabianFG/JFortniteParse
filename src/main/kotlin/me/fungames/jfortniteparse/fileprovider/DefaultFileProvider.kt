package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider.Companion.logger
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.pak.PakFileReader
import me.fungames.jfortniteparse.ue4.pak.reader.FPakFileArchive
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import java.io.File

@Suppress("EXPERIMENTAL_API_USAGE")
class DefaultFileProvider(val folder : File, override var game : Ue4Version = Ue4Version.GAME_UE4_LATEST) : AbstractFileProvider() {
    override val files = mutableMapOf<String, GameFile>()
    val localFiles = mutableMapOf<String, File>()
    val unloadedPaks = mutableListOf<PakFileReader>()
    val requiredKeys = mutableListOf<FGuid>()
    val mountedPaks = mutableListOf<PakFileReader>()

    override var defaultLocres : Locres? = null


    init {
        scanFiles(folder)
    }


    private fun scanFiles(folder : File) {
        val folderFiles = folder.listFiles() ?: emptyArray()
        folderFiles.forEach {
            if (it.isDirectory)
                scanFiles(it)
            else if (it.isFile && it.extension == "pak") {
                try {
                    val reader = PakFileReader(it, game.game)
                    if (!reader.isEncrypted()) {
                        reader.readIndex()
                        reader.files.associateByTo(files, {file -> file.path.toLowerCase()})
                        mountedPaks.add(reader)
                    } else {
                        unloadedPaks.add(reader)
                        if(!requiredKeys.contains(reader.pakInfo.encryptionKeyGuid))
                            requiredKeys.add(reader.pakInfo.encryptionKeyGuid)
                    }
                } catch (e : ParserException) {
                    logger.error { e.message }
                }
            } else if (it.isFile) {
                var gamePath = it.absolutePath.substringAfter(this.folder.absolutePath)
                if (gamePath.startsWith('\\') || gamePath.startsWith('/'))
                    gamePath = gamePath.substring(1)
                gamePath = gamePath.replace('\\', '/')
                localFiles[gamePath.toLowerCase()] = it
            }
        }
    }

    override fun requiredKeys() : List<FGuid> = requiredKeys
    override fun submitKeys(keys : Map<FGuid, ByteArray>): Int {
        var countNewMounts = 0
        keys.forEach { (guid, key) ->
            if (requiredKeys.contains(guid)) {
                val oldCountMounts = countNewMounts
                getUnloadedPakFilesByGuid(guid).forEach {
                    if (it.testAesKey(key)) {
                        it.aesKey = key
                        it.readIndex()
                        it.files.associateByTo(files, {file -> file.path.toLowerCase()})
                        unloadedPaks.remove(it)
                        mountedPaks.add(it)
                        countNewMounts++
                    } else logger.warn("The provided encryption key doesn't work with \"" + (if (it.Ar is FPakFileArchive) it.Ar.file else it.fileName) + "\". Skipping.")
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
            if (path.startsWith("Game/", ignoreCase = true))
                file = localFiles.filterKeys {
                    if (it.contains("Game/", ignoreCase = true))
                        it.substringAfter("game/") == path.substringAfter("game/")
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