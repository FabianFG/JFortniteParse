package me.fungames.jfortniteparse.fileprovider

import kotlinx.coroutines.*
import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.exceptions.InvalidAesKeyException
import me.fungames.jfortniteparse.ue4.assets.IoPackage
import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.versions.GAME_UE5_BASE
import me.fungames.jfortniteparse.ue4.vfs.AbstractAesVfsReader
import me.fungames.jfortniteparse.util.printAesKey
import java.util.concurrent.atomic.AtomicInteger

abstract class PakFileProvider : AbstractFileProvider(), CoroutineScope {
    private val job = Job()
    override val coroutineContext = job + Dispatchers.IO

    protected abstract val unloadedPaks: MutableList<AbstractAesVfsReader>
    protected abstract val mountedPaks: MutableList<AbstractAesVfsReader>
    protected abstract val requiredKeys: MutableList<FGuid>
    protected abstract val keys: MutableMap<FGuid, ByteArray>
    protected val mountListeners = mutableListOf<PakMountListener>()
    open val globalPackageStore = lazy { FPackageStore(this) }
    open fun keys(): Map<FGuid, ByteArray> = keys
    fun keysStr(): Map<FGuid, String> = keys.mapValues { it.value.printAesKey() }
    open fun requiredKeys(): List<FGuid> = requiredKeys
    open fun unloadedPaks(): List<AbstractAesVfsReader> = unloadedPaks
    open fun mountedPaks(): List<AbstractAesVfsReader> = mountedPaks
    fun submitKey(guid: FGuid, key: String) = submitKeysStr(mapOf(guid to key))
    fun submitKeysStr(keys: Map<FGuid, String>) = submitKeys(keys.mapValues { Aes.parseKey(it.value) })
    fun submitKey(guid: FGuid, key: ByteArray) = submitKeys(mapOf(guid to key))
    open fun submitKeys(keys: Map<FGuid, ByteArray>) = runBlocking { submitKeysAsync(keys).await() }

    open fun unloadedPaksByGuid(guid: FGuid) = unloadedPaks.filter { it.encryptionKeyGuid == guid }

    open fun submitKeysAsync(newKeys: Map<FGuid, ByteArray>): Deferred<Int> {
        val countNewMounts = AtomicInteger()
        val tasks = mutableListOf<Deferred<Result<AbstractAesVfsReader>>>()
        for ((guid, key) in newKeys) {
            if (guid !in requiredKeys)
                continue
            for (reader in unloadedPaksByGuid(guid)) tasks += async {
                runCatching {
                    reader.aesKey = key
                    keys[guid] = key
                    mount(reader)
                    unloadedPaks.remove(reader)
                    requiredKeys.remove(guid)
                    countNewMounts.getAndIncrement()
                    reader
                }.onFailure {
                    if (it is InvalidAesKeyException)
                        keys.remove(guid)
                    else
                        logger.warn(it) { "Uncaught exception while loading pak file ${reader.name.substringAfterLast('/')}" }
                }
            }
        }
        return async {
            tasks.awaitAll()
            countNewMounts.get()
        }
    }

    protected open fun mount(reader: AbstractAesVfsReader) {
        reader.readIndex().associateByTo(files) { it.path.toLowerCase() }
        mountedPaks.add(reader)
        mountListeners.forEach { it.onMount(reader) }
    }

    override fun loadGameFile(packageId: FPackageId): IoPackage? = runCatching {
        val storeEntry = globalPackageStore.value.findStoreEntry(packageId)
            ?: return null//throw NotFoundException("The package to load does not exist on disk or in the loader")
        val chunkType = if (game >= GAME_UE5_BASE) EIoChunkType5.ExportBundleData else EIoChunkType.ExportBundleData
        val ioBuffer = saveChunk(FIoChunkId(packageId.value(), 0u, chunkType))
        return IoPackage(ioBuffer, packageId, storeEntry, globalPackageStore.value, this, versions)
    }.onFailure { logger.error(it) { "Failed to load package with id 0x%016X".format(packageId.value().toLong()) } }.getOrNull()

    override fun saveGameFile(filePath: String): ByteArray? {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        return gameFile?.let { saveGameFile(it) }
    }

    override fun saveGameFile(file: GameFile): ByteArray {
        if (file.ioChunkId != null && file.ioStoreReader != null)
            return file.ioStoreReader.read(file.ioChunkId)
        val reader = mountedPaks.firstOrNull { it.name == file.pakFileName } ?: throw IllegalArgumentException("Couldn't find any possible pak file readers")
        return reader.extract(file)
    }

    override fun saveChunk(chunkId: FIoChunkId): ByteArray {
        for (reader in mountedPaks) {
            if (reader !is FIoStoreReaderImpl) continue
            try {
                return reader.read(chunkId)
            } catch (e: FIoStatusException) {
                if (e.status.errorCode != EIoErrorCode.NotFound) {
                    throw e
                }
            }
        }
        throw IllegalArgumentException("Couldn't find any possible I/O store readers")
    }

    fun addOnMountListener(listener: PakMountListener) {
        mountListeners.add(listener)
    }

    fun removeOnMountListener(listener: PakMountListener) {
        mountListeners.remove(listener)
    }

    fun interface PakMountListener {
        fun onMount(reader: AbstractAesVfsReader)
    }
}