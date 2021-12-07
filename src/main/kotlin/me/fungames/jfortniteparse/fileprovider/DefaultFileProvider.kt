package me.fungames.jfortniteparse.fileprovider

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.mappings.ReflectionTypeMappingsProvider
import me.fungames.jfortniteparse.ue4.assets.mappings.TypeMappingsProvider
import me.fungames.jfortniteparse.ue4.io.FIoStoreReaderImpl
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.pak.PakFileReader
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import me.fungames.jfortniteparse.ue4.vfs.AbstractAesVfsReader
import java.io.Closeable
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

open class DefaultFileProvider : PakFileProvider, Closeable {
    val folder: File
    final override var versions: VersionContainer
    private val localFiles = mutableMapOf<String, File>()
    override val files = ConcurrentHashMap<String, GameFile>()
    override val unloadedPaks = CopyOnWriteArrayList<AbstractAesVfsReader>()
    override val requiredKeys = CopyOnWriteArrayList<FGuid>()
    override val keys = ConcurrentHashMap<FGuid, ByteArray>()
    override val mountedPaks = CopyOnWriteArrayList<AbstractAesVfsReader>()

    @JvmOverloads
    constructor(folder: File, versions: VersionContainer = VersionContainer.DEFAULT) {
        this.folder = folder
        this.versions = versions
    }

    @JvmOverloads
    constructor(folder: File, game: Ue4Version, mappingsProvider: TypeMappingsProvider = ReflectionTypeMappingsProvider()) : this(folder, VersionContainer(game.game)) {
        this.mappingsProvider = mappingsProvider
    }

    fun initialize() {
        val initialMountTasks = mutableListOf<Deferred<Boolean>>()
        scanFiles(folder, initialMountTasks)
        runBlocking { initialMountTasks.awaitAll() }
    }

    private fun scanFiles(folder: File, initialMountTasks: MutableList<Deferred<Boolean>>) {
        for (file in folder.listFiles() ?: emptyArray()) {
            if (file.isDirectory) {
                scanFiles(file, initialMountTasks)
            } else if (file.isFile) {
                registerFile(file, initialMountTasks)
            }
        }
    }

    private fun registerFile(file: File, initialMountTasks: MutableList<Deferred<Boolean>>) {
        val ext = file.extension.toLowerCase()
        if (ext == "pak") {
            initialMountTasks.add(async {
                try {
                    val reader = PakFileReader(file, versions)
                    if (!reader.isEncrypted()) {
                        mount(reader)
                        true
                    } else {
                        unloadedPaks.add(reader)
                        requiredKeys.addIfAbsent(reader.encryptionKeyGuid)
                        false
                    }
                } catch (e: ParserException) {
                    logger.error { e.message }
                    false
                }
            })
        } else if (ext == "utoc") {
            initialMountTasks.add(async {
                val path = file.path.substringBeforeLast('.')
                try {
                    val reader = FIoStoreReaderImpl(path, ioStoreTocReadOptions, versions)
                    if (!reader.isEncrypted()) {
                        mount(reader)
                        if (reader.name == "global") {
                            globalDataLoaded = true
                        }
                        if (globalPackageStore.isInitialized()) {
                            globalPackageStore.value.onContainerMounted(reader)
                        }
                        true
                    } else {
                        unloadedPaks.add(reader)
                        requiredKeys.addIfAbsent(reader.encryptionKeyGuid)
                        false
                    }
                } catch (e: ParserException) {
                    logger.error("Failed to mount IoStore environment \"{}\" [{}]", path, e.message)
                    false
                }
            })
        } else {
            var gamePath = file.absolutePath.substringAfter(folder.absolutePath)
            if (gamePath.startsWith('\\') || gamePath.startsWith('/'))
                gamePath = gamePath.substring(1)
            gamePath = gamePath.replace('\\', '/')
            localFiles[gamePath.toLowerCase()] = file
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
        keys.clear()
        requiredKeys.clear()
        globalDataLoaded = false
    }
}