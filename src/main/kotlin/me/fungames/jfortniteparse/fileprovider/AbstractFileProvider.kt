package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.PakPackage
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.registry.AssetRegistry

abstract class AbstractFileProvider : FileProvider() {
    protected var globalDataLoaded = false

    override fun loadGameFile(file: GameFile): Package? = runCatching {
        file.ioPackageId?.let { return loadGameFile(it) }
        if (!file.isUE4Package() || !file.hasUexp())
            throw IllegalArgumentException("The provided file is not a package file")
        val uasset = saveGameFile(file)
        val uexp = saveGameFile(file.uexp)
        val ubulk = if (file.hasUbulk()) saveGameFile(file.ubulk!!) else null
        return PakPackage(uasset, uexp, ubulk, file.path, this, game)
    }.onFailure { logger.error(it) { "Failed to load package ${file.path}" } }.getOrNull()

    override fun findGameFile(filePath: String): GameFile? {
        val path = fixPath(filePath)
        return files[path]
    }

    override fun loadGameFile(filePath: String): Package? = runCatching {
        val path = fixPath(filePath)
        // try load from PAKs
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return loadGameFile(gameFile)
        // try load from IoStore
        if (globalDataLoaded) {
            val name = compactFilePath(filePath)
            val packageId = FPackageId.fromName(FName(name))
            try {
                val ioFile = loadGameFile(packageId)
                if (ioFile != null)
                    return ioFile
            } catch (e: ParserException) {
                logger.error(e) { "Failed to load package $path" }
            }
        }
        // try load from file system
        if (!path.endsWith(".uasset") && !path.endsWith(".umap"))
            return null//throw NotFoundException("Path does not end with .uasset or .umap, or the package was not found")
        val uasset = saveGameFile(path)
            ?: return null//throw NotFoundException("uasset/umap not found")
        val uexp = saveGameFile(path.substringBeforeLast(".") + ".uexp")
            ?: return null//throw NotFoundException("uexp not found")
        val ubulk = saveGameFile(path.substringBeforeLast(".") + ".ubulk")
        return PakPackage(uasset, uexp, ubulk, path, this, game)
    }.onFailure { logger.error(it) { "Failed to load package $filePath" } }.getOrNull()

    override fun loadLocres(filePath: String): Locres? = runCatching {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return loadLocres(gameFile)
        if (!path.endsWith(".locres"))
            return null
        val locres = saveGameFile(path) ?: return null
        return Locres(locres, path, getLocresLanguageByPath(filePath))
    }.onFailure { logger.error("Failed to load locres $filePath", it) }.getOrNull()

    override fun loadLocres(file: GameFile): Locres? = runCatching {
        if (!file.isLocres())
            return null
        val locres = saveGameFile(file)
        return Locres(locres, file.path, getLocresLanguageByPath(file.path))
    }.onFailure { logger.error("Failed to load locres ${file.path}", it) }.getOrNull()

    override fun loadAssetRegistry(filePath: String): AssetRegistry? = runCatching {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return loadAssetRegistry(gameFile)
        if (!path.endsWith(".bin"))
            return null
        val locres = saveGameFile(path) ?: return null
        return AssetRegistry(locres, path)
    }.onFailure { logger.error("Failed to load asset registry $filePath", it) }.getOrNull()

    override fun loadAssetRegistry(file: GameFile): AssetRegistry? = runCatching {
        if (!file.isAssetRegistry())
            return null
        val locres = saveGameFile(file)
        return AssetRegistry(locres, file.path)
    }.onFailure { logger.error("Failed to load asset registry ${file.path}", it) }.getOrNull()

    override fun savePackage(filePath: String): Map<String, ByteArray> {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return savePackage(gameFile)
        val map = mutableMapOf<String, ByteArray>()
        runCatching {
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
        }
        return map
    }

    override fun savePackage(file: GameFile): Map<String, ByteArray> {
        val map = mutableMapOf<String, ByteArray>()
        runCatching {
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
        }
        return map
    }
}