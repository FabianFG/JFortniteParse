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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@Suppress("EXPERIMENTAL_API_USAGE")
class DefaultFileProvider(val folder : File, override var game : Ue4Version = Ue4Version.GAME_UE4_LATEST) : PakFileProvider() {

    private val localFiles = mutableMapOf<String, File>()
    override val files = ConcurrentHashMap<String, GameFile>()
    override val unloadedPaks = CopyOnWriteArrayList<PakFileReader>()
    override val requiredKeys = CopyOnWriteArrayList<FGuid>()
    override val keys = ConcurrentHashMap<FGuid, ByteArray>()
    override val mountedPaks = CopyOnWriteArrayList<PakFileReader>()

    override var defaultLocres : Locres? = null


    init {
        scanFiles(folder)
    }

    private fun scanFiles(folder : File) {
        val folderFiles = folder.listFiles() ?: emptyArray()
        folderFiles.forEach {
            if (it.isDirectory)
                scanFiles(it)
            else if (it.isFile && it.extension.equals("pak", true)) {
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

    override fun saveGameFile(filePath: String): ByteArray? {
        val res = super.saveGameFile(filePath)
        if (res != null)
            return res
        val path = fixPath(filePath)
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
}