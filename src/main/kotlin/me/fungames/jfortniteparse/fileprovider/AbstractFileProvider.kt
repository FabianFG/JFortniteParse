package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.PakPackage
import me.fungames.jfortniteparse.ue4.asyncloading2.EAsyncLoadingResult
import me.fungames.jfortniteparse.ue4.asyncloading2.FAsyncLoadingThread2
import me.fungames.jfortniteparse.ue4.io.FIoDispatcher
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.registry.AssetRegistry
import me.fungames.jfortniteparse.util.await
import java.util.concurrent.CompletableFuture

abstract class AbstractFileProvider : FileProvider() {
    val asyncLoadingThread by lazy {
        FAsyncLoadingThread2(FIoDispatcher.get()).apply {
            provider = this@AbstractFileProvider
            initializeLoading()
            startThread()
        }
    }

    override fun loadGameFile(file: GameFile): Package? {
        if (!file.isUE4Package() || !file.hasUexp())
            return null
        val uasset = saveGameFile(file)
        val uexp = saveGameFile(file.uexp)
        val ubulk = if (file.hasUbulk()) saveGameFile(file.ubulk!!) else null
        return try {
            PakPackage(uasset, uexp, ubulk, file.path, this, game)
        } catch (e: Exception) {
            logger.error("Failed to load package ${file.path}", e)
            null
        }
    }

    override fun findGameFile(filePath: String): GameFile? {
        val path = fixPath(filePath)
        return files[path]
    }

    override fun loadGameFile(filePath: String): Package? {
        val path = fixPath(filePath)
        // try load from PAKs
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return loadGameFile(gameFile)
        // try load from IoStore
        if (FIoDispatcher.isInitialized()) {
            val event = CompletableFuture<Package>()
            asyncLoadingThread.loadPackage(compactFilePath(filePath)) { packageName, loadedPackage, result ->
                if (result == EAsyncLoadingResult.Succeeded) {
                    event.complete(loadedPackage)
                } else {
                    event.completeExceptionally(ParserException("FAsyncLoadingThread2::LoadPackage failed with result $result"))
                }
            }
            return try {
                event.await()
            } catch (e: Throwable) {
                logger.error("Failed to load package $path from IoStore", e)
                null
            }
        }
        // try load from file system
        if (!path.endsWith(".uasset") && !path.endsWith(".umap"))
            return null
        val uasset = saveGameFile(path) ?: return null
        val uexp = saveGameFile(path.substringBeforeLast(".") + ".uexp") ?: return null
        val ubulk = saveGameFile(path.substringBeforeLast(".") + ".ubulk")
        return try {
            PakPackage(uasset, uexp, ubulk, path, this, game)
        } catch (e: ParserException) {
            logger.error("Failed to load package $path", e)
            null
        }
    }

    override fun loadLocres(filePath: String): Locres? {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return loadLocres(gameFile)
        if (!path.endsWith(".locres"))
            return null
        val locres = saveGameFile(path) ?: return null
        return try {
            Locres(locres, path, getLocresLanguageByPath(filePath))
        } catch (e: ParserException) {
            logger.error("Failed to load locres $path", e)
            null
        }
    }

    override fun loadLocres(file: GameFile): Locres? {
        if (!file.isLocres())
            return null
        val locres = saveGameFile(file)
        return try {
            Locres(locres, file.path, getLocresLanguageByPath(file.path))
        } catch (e: Exception) {
            logger.error("Failed to load locres ${file.path}", e)
            null
        }
    }

    override fun loadAssetRegistry(filePath: String): AssetRegistry? {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return loadAssetRegistry(gameFile)
        if (!path.endsWith(".bin"))
            return null
        val locres = saveGameFile(path) ?: return null
        return try {
            AssetRegistry(locres, path)
        } catch (e: ParserException) {
            logger.error("Failed to load asset registry $path", e)
            null
        }
    }

    override fun loadAssetRegistry(file: GameFile): AssetRegistry? {
        if (!file.isAssetRegistry())
            return null
        val locres = saveGameFile(file)
        return try {
            AssetRegistry(locres, file.path)
        } catch (e: Exception) {
            logger.error("Failed to load asset registry ${file.path}", e)
            null
        }
    }

    override fun savePackage(filePath: String): Map<String, ByteArray> {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return savePackage(gameFile)
        val map = mutableMapOf<String, ByteArray>()
        if (path.endsWith(".uasset") || path.endsWith(".umap")) {
            val uasset = saveGameFile(path) ?: return map
            map[path] = uasset
            val uexpPath = path.substringBeforeLast(".") + ".uexp"
            val uexp = saveGameFile(uexpPath) ?: return map
            map[uexpPath] = uexp
            val ubulkPath = path.substringBeforeLast(".") + ".ubulk"
            val ubulk = saveGameFile(ubulkPath) ?: return map
            map[ubulkPath] = ubulk
        } else {
            val data = saveGameFile(path) ?: return map
            map[path] = data
        }
        return map
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
}