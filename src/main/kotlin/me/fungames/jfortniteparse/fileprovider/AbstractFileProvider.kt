package me.fungames.jfortniteparse.fileprovider

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider.Companion.logger
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.pak.GameFile

@Suppress("EXPERIMENTAL_API_USAGE")
abstract class AbstractFileProvider : FileProvider {

    override fun loadGameFile(file: GameFile): Package? {
        if (!file.isUE4Package() || !file.hasUexp())
            return null
        val uasset = saveGameFile(file)
        val uexp = saveGameFile(file.uexp)
        val ubulk = if (file.hasUbulk()) saveGameFile(file.ubulk!!) else null
        return try {
            Package(uasset, uexp, ubulk, file.path, this, game).apply { applyLocres(defaultLocres) }
        } catch (e : Exception) {
            logger.error("Failed to load package ${file.path}", e)
            null
        }
    }

    override fun loadGameFile(filePath: String): Package? {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return loadGameFile(gameFile)
        if (!path.endsWith(".uasset"))
            return null
        val uasset = saveGameFile(path) ?: return null
        val uexp = saveGameFile(path.substringBeforeLast(".uasset") + ".uexp") ?: return null
        val ubulk = saveGameFile(path.substringBeforeLast(".uasset") + ".ubulk")
        return try {
            Package(uasset, uexp, ubulk, path, this, game).apply { applyLocres(defaultLocres) }
        } catch (e : ParserException) {
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
        } catch (e : ParserException) {
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
        } catch (e : Exception) {
            logger.error("Failed to load locres ${file.path}", e)
            null
        }
    }

    override fun savePackage(filePath: String): Map<String, ByteArray> {
        val path = fixPath(filePath)
        val gameFile = findGameFile(path)
        if (gameFile != null)
            return savePackage(gameFile)
        val map = mutableMapOf<String, ByteArray>()
        if (path.endsWith(".uasset")) {
            val uasset = saveGameFile(path) ?: return map
            map[path] = uasset
            val uexpPath = path.substringBeforeLast(".uasset") + ".uexp"
            val uexp = saveGameFile(uexpPath) ?: return map
            map[uexpPath] = uexp
            val ubulkPath = path.substringBeforeLast(".uasset") + ".ubulk"
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