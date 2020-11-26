package me.fungames.jfortniteparse.fileprovider

import kotlinx.coroutines.*
import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.exceptions.InvalidAesKeyException
import me.fungames.jfortniteparse.ue4.io.FIoDispatcher
import me.fungames.jfortniteparse.ue4.io.FIoStatusException
import me.fungames.jfortniteparse.ue4.io.FIoStoreEnvironment
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.pak.PakFileReader
import me.fungames.jfortniteparse.ue4.pak.reader.FPakFileArchive
import me.fungames.jfortniteparse.util.printAesKey
import java.util.concurrent.atomic.AtomicInteger

abstract class PakFileProvider : AbstractFileProvider(), CoroutineScope {
    private val job = Job()
    override val coroutineContext = job + Dispatchers.IO

    protected abstract val unloadedPaks: MutableList<PakFileReader>
    protected abstract val mountedPaks: MutableList<PakFileReader>
    protected abstract val requiredKeys: MutableList<FGuid>
    protected abstract val keys: MutableMap<FGuid, ByteArray>
    protected val mountListeners = mutableListOf<PakMountListener>()
    open fun keys(): Map<FGuid, ByteArray> = keys
    open fun keysStr(): Map<FGuid, String> = keys.mapValues { it.value.printAesKey() }
    open fun requiredKeys(): List<FGuid> = requiredKeys
    open fun unloadedPaks(): List<PakFileReader> = unloadedPaks
    open fun mountedPaks(): List<PakFileReader> = mountedPaks
    open fun submitKey(guid: FGuid, key: String) = submitKeysStr(mapOf(guid to key))
    open fun submitKeysStr(keys: Map<FGuid, String>) = submitKeys(keys.mapValues { Aes.parseKey(it.value) })
    open fun submitKey(guid: FGuid, key: ByteArray) = submitKeys(mapOf(guid to key))
    open fun submitKeys(keys: Map<FGuid, ByteArray>) = runBlocking { submitKeysAsync(keys).await() }

    open fun unloadedPaksByGuid(guid: FGuid) = unloadedPaks.filter { it.pakInfo.encryptionKeyGuid == guid }

    open fun submitKeysAsync(newKeys: Map<FGuid, ByteArray>): Deferred<Int> {
        val countNewMounts = AtomicInteger()
        val tasks = mutableListOf<Deferred<Result<PakFileReader>>>()
        for ((guid, key) in newKeys) {
            if (guid !in requiredKeys)
                continue
            for (reader in unloadedPaksByGuid(guid)) tasks += async {
                runCatching {
                    reader.aesKey = key
                    mount(reader)
                    unloadedPaks.remove(reader)
                    countNewMounts.getAndIncrement()
                    reader
                }.onFailure {
                    if (it !is InvalidAesKeyException)
                        logger.warn(it) { "Uncaught exception while loading pak file ${reader.fileName.substringAfterLast('/')}" }
                }
            }
        }
        return async {
            tasks.awaitAll().forEach {
                val reader = it.getOrNull()
                val key = reader?.aesKey
                if (reader != null && key != null) {
                    requiredKeys.remove(reader.pakInfo.encryptionKeyGuid)
                    keys[reader.pakInfo.encryptionKeyGuid] = key
                }
            }
            countNewMounts.get()
        }
    }

    protected fun mount(reader: PakFileReader) {
        reader.readIndex()
        reader.files.associateByTo(files) { it.path.toLowerCase() }
        mountedPaks.add(reader)

        if (FIoDispatcher.isInitialized() && reader.Ar is FPakFileArchive) {
            val ioStoreEnvironment = FIoStoreEnvironment(reader.Ar.file.path.substringBeforeLast('.'))
            val encryptionKeyGuid = reader.pakInfo.encryptionKeyGuid
            try {
                FIoDispatcher.get().mount(ioStoreEnvironment, encryptionKeyGuid, reader.aesKey)
                PakFileReader.logger.info("Mounted IoStore environment \"{}\"", ioStoreEnvironment.path)
            } catch (e: FIoStatusException) {
                PakFileReader.logger.warn("Failed to mount IoStore environment \"{}\" [{}]", ioStoreEnvironment.path, e.message)
            }
        }

        mountListeners.forEach { it.onMount(reader) }
    }

    override fun saveGameFile(filePath: String): ByteArray? {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        return gameFile?.let { saveGameFile(it) }
    }

    override fun saveGameFile(file: GameFile): ByteArray {
        val reader = mountedPaks.firstOrNull { it.fileName == file.pakFileName } ?: throw IllegalArgumentException("Couldn't find any possible pak file readers")
        return reader.extract(file)
    }

    fun addOnMountListener(listener: PakMountListener) {
        mountListeners.add(listener)
    }

    fun removeOnMountListener(listener: PakMountListener) {
        mountListeners.remove(listener)
    }

    fun interface PakMountListener {
        fun onMount(reader: PakFileReader)
    }
}