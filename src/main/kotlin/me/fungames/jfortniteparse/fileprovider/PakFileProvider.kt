package me.fungames.jfortniteparse.fileprovider

import kotlinx.coroutines.*
import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.exceptions.InvalidAesKeyException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.pak.PakFileReader
import me.fungames.jfortniteparse.util.printAesKey
import me.fungames.jfortniteparse.util.printHexBinary
import java.util.concurrent.atomic.AtomicInteger

@Suppress("EXPERIMENTAL_API_USAGE")
abstract class PakFileProvider : AbstractFileProvider(), CoroutineScope {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.IO

    protected abstract val unloadedPaks : MutableList<PakFileReader>
    protected abstract val mountedPaks : MutableList<PakFileReader>
    protected abstract val requiredKeys : MutableList<FGuid>
    protected abstract val keys : MutableMap<FGuid, ByteArray>
    open fun keys() : Map<FGuid, ByteArray> = keys
    open fun keysStr() : Map<FGuid, String> = keys.mapValues { it.value.printAesKey() }
    open fun requiredKeys() : List<FGuid> = requiredKeys
    open fun unloadedPaks() : List<PakFileReader> = unloadedPaks
    open fun mountedPaks() : List<PakFileReader> = mountedPaks
    open fun submitKey(guid : FGuid, key : String) = submitKeysStr(mapOf(guid to key))
    open fun submitKeysStr(keys : Map<FGuid, String>) = submitKeys(keys.mapValues { Aes.parseKey(it.value) })
    open fun submitKey(guid : FGuid, key : ByteArray) = submitKeys(mapOf(guid to key))
    open fun submitKeys(keys : Map<FGuid, ByteArray>) = runBlocking { submitKeysAsync(keys).await() }

    open fun unloadedPaksByGuid(guid: FGuid) = unloadedPaks.filter { it.pakInfo.encryptionKeyGuid == guid }

    open fun submitKeysAsync(newKeys: Map<FGuid, ByteArray>) : Deferred<Int> {
        val countNewMounts = AtomicInteger()
        val tasks = mutableListOf<Deferred<PakFileReader?>>()
        newKeys.forEach { (guid, key) ->
            if (requiredKeys.contains(guid)) {
                unloadedPaksByGuid(guid).forEach { reader ->
                    tasks.add(async { runCatching {
                        reader.aesKey = key
                        reader.readIndex()
                        reader.files.associateByTo(files, {file -> file.path.toLowerCase()})
                        unloadedPaks.remove(reader)
                        mountedPaks.add(reader)
                        countNewMounts.getAndIncrement()
                        reader
                    }.onFailure {
                        if (it !is InvalidAesKeyException)
                            logger.warn(it) { "Uncaught exception while loading pak file ${reader.fileName.substringAfterLast('/')}" }
                    }.getOrNull() })
                }
            }
        }
        return async {
            tasks.awaitAll().forEach {
                val key = it?.aesKey
                if (it != null && key != null) {
                    requiredKeys.remove(it.pakInfo.encryptionKeyGuid)
                    keys[it.pakInfo.encryptionKeyGuid] = key
                }
            }
            countNewMounts.get()
        }
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
}