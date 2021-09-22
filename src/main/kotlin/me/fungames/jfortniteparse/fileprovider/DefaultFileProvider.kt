package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.mappings.ReflectionTypeMappingsProvider
import me.fungames.jfortniteparse.ue4.assets.mappings.TypeMappingsProvider
import me.fungames.jfortniteparse.ue4.io.FIoStoreReaderImpl
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.pak.PakFileReader
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import java.io.Closeable
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

open class DefaultFileProvider : PakFileProvider, Closeable {
    val folder: File
    final override var versions: VersionContainer
    private val localFiles = mutableMapOf<String, File>()
    override val files = ConcurrentHashMap<String, GameFile>()
    override val unloadedPaks = CopyOnWriteArrayList<PakFileReader>()
    override val requiredKeys = CopyOnWriteArrayList<FGuid>()
    override val keys = ConcurrentHashMap<FGuid, ByteArray>()
    override val mountedPaks = CopyOnWriteArrayList<PakFileReader>()
    override val mountedIoStoreReaders = CopyOnWriteArrayList<FIoStoreReaderImpl>()

    @JvmOverloads
    constructor(folder: File, versions: VersionContainer = VersionContainer.DEFAULT) {
        this.folder = folder
        this.versions = versions
        scanFiles(folder)
    }

    @JvmOverloads
    constructor(folder: File, game: Ue4Version, mappingsProvider: TypeMappingsProvider = ReflectionTypeMappingsProvider()) : this(folder, VersionContainer(game.game)) {
        this.mappingsProvider = mappingsProvider
    }

    private fun scanFiles(folder: File) {
        if (game >= GAME_UE4(26) && !globalDataLoaded && folder.name == "Paks") {
            val globalTocFile = File(folder, "global.utoc")
            if (globalTocFile.exists()) {
                loadGlobalData(globalTocFile)
            }
        }
        for (file in folder.listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                scanFiles(file)
            } else if (file.isFile) {
                if (file.extension.toLowerCase() == "pak") {
                    try {
                        val reader = PakFileReader(file, versions)
                        if (!reader.isEncrypted()) {
                            mount(reader)
                        } else {
                            unloadedPaks.add(reader)
                            requiredKeys.addIfAbsent(reader.pakInfo.encryptionKeyGuid)
                        }
                    } catch (e: ParserException) {
                        logger.error { e.message }
                    }
                } else {
                    var gamePath = file.absolutePath.substringAfter(this.folder.absolutePath)
                    if (gamePath.startsWith('\\') || gamePath.startsWith('/'))
                        gamePath = gamePath.substring(1)
                    gamePath = gamePath.replace('\\', '/')
                    localFiles[gamePath.toLowerCase()] = file
                }
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
        if (file == null && path.startsWith("Game/", ignoreCase = true)) {
            file = localFiles.filterKeys {
                if (it.contains("Game/", ignoreCase = true))
                    it.substringAfter("game/") == path.substringAfter("game/")
                else
                    false
            }.values.firstOrNull()
        }
        return file?.readBytes()
    }

    override fun close() {
        files.clear()
        unloadedPaks.forEach { it.close() }
        unloadedPaks.clear()
        mountedPaks.forEach { it.close() }
        mountedPaks.clear()
        mountedIoStoreReaders.forEach { it.close() }
        mountedIoStoreReaders.clear()
        keys.clear()
        requiredKeys.clear()
        globalDataLoaded = false
    }
}