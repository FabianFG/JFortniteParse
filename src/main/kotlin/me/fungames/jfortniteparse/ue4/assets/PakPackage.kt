package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UStruct
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.uobject.*
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import java.io.File
import java.io.OutputStream
import java.nio.ByteBuffer

class PakPackage(
    uasset: ByteBuffer,
    uexp: ByteBuffer,
    ubulk: ByteBuffer? = null,
    fileName: String,
    provider: FileProvider? = null,
    game: Ue4Version = provider?.game ?: Ue4Version.GAME_UE4_LATEST,
) : Package(fileName, provider, game) {
    companion object {
        val packageMagic = 0x9E2A83C1u
    }

    constructor(uasset: ByteArray, uexp: ByteArray, ubulk: ByteArray? = null, name: String, provider: FileProvider? = null, game: Ue4Version = Ue4Version.GAME_UE4_LATEST) :
        this(ByteBuffer.wrap(uasset), ByteBuffer.wrap(uexp), ubulk?.let { ByteBuffer.wrap(it) }, name, provider, game)

    constructor(uasset: File, uexp: File, ubulk: File?, provider: FileProvider? = null, game: Ue4Version = provider?.game ?: Ue4Version.GAME_UE4_LATEST) : this(
        uasset.readBytes(), uexp.readBytes(), ubulk?.readBytes(),
        uasset.nameWithoutExtension, provider, game
    )

    val info: FPackageFileSummary
    val nameMap: MutableList<FNameEntry>
    val importMap: MutableList<FObjectImport>
    val exportMap: MutableList<FObjectExport>

    override val exportsLazy: List<Lazy<UObject>>
        get() = exportMap.map { it.exportObject }

    init {
        name = provider?.compactFilePath(fileName)?.substringBeforeLast('.') ?: fileName
        val uassetAr = FAssetArchive(uasset, provider, fileName)
        val uexpAr = FAssetArchive(uexp, provider, fileName)
        val ubulkAr = ubulk?.let { FAssetArchive(it, provider, fileName) }
        uassetAr.game = game.game
        uassetAr.ver = game.version
        uassetAr.owner = this
        uexpAr.game = game.game
        uexpAr.ver = game.version
        uexpAr.owner = this
        ubulkAr?.game = game.game
        ubulkAr?.ver = game.version
        ubulkAr?.owner = this

        nameMap = mutableListOf()
        importMap = mutableListOf()
        exportMap = mutableListOf()

        info = FPackageFileSummary(uassetAr)
        if (info.tag != packageMagic)
            throw ParserException("Invalid uasset magic, ${info.tag} != $packageMagic")

        packageFlags = info.packageFlags.toInt()

        uassetAr.seek(info.nameOffset)
        for (i in 0 until info.nameCount)
            nameMap.add(FNameEntry(uassetAr))

        uassetAr.seek(info.importOffset)
        for (i in 0 until info.importCount)
            importMap.add(FObjectImport(uassetAr))

        uassetAr.seek(info.exportOffset)
        for (i in 0 until info.exportCount)
            exportMap.add(FObjectExport(uassetAr))

        //Setup uexp reader
        uexpAr.uassetSize = info.totalHeaderSize
        uexpAr.bulkDataStartOffset = info.bulkDataStartOffset
        uexpAr.useUnversionedPropertySerialization = (packageFlags and EPackageFlags.PKG_UnversionedProperties.value) != 0

        //If attached also setup the ubulk reader
        if (ubulkAr != null) {
            ubulkAr.uassetSize = info.totalHeaderSize
            ubulkAr.uexpSize = exportMap.sumBy { it.serialSize.toInt() }
            uexpAr.addPayload(PayloadType.UBULK, ubulkAr)
        }

        exportMap.forEach { export ->
            export.exportObject = lazy {
                val origPos = uexpAr.pos()
                val exportType = export.classIndex.run {
                    when {
                        isExport() -> exportMap[toExport()].superIndex
                        isImport() -> this
                        else -> null
                    }
                }?.getImportObject()?.objectName ?: throw ParserException("Could not find class name for ${export.objectName}")
                uexpAr.seekRelative(export.serialOffset.toInt())
                val validPos = (uexpAr.pos() + export.serialSize).toInt()
                val obj = constructExport(export.classIndex.load<UStruct>())
                obj.export = export
                obj.name = export.objectName.text
                obj.outer = export.outerIndex.load() ?: this
                obj.deserialize(uexpAr, validPos)
                if (validPos != uexpAr.pos()) {
                    logger.warn("Did not read $exportType correctly, ${validPos - uexpAr.pos()} bytes remaining")
                } else {
                    logger.debug("Successfully read $exportType at ${uexpAr.toNormalPos(export.serialOffset.toInt())} with size ${export.serialSize}")
                }
                uexpAr.seek(origPos)
                obj
            }
        }

        logger.info { "Successfully parsed package : $name" }
    }

    // Load object by FPackageIndex
    override fun <T : UObject> findObject(index: FPackageIndex?) = when {
        index == null || index.isNull() -> null
        index.isImport() -> findImport(importMap.getOrNull(index.toImport()))
        index.isExport() -> exportMap.getOrNull(index.toExport())?.exportObject
        else -> null
    } as Lazy<T>?

    // Load object by FObjectImport
    inline fun <reified T> loadImport(import: FObjectImport?): T? {
        if (import == null) return null
        val loaded = findImport(import)?.value ?: return null
        return if (loaded is T) loaded else null
    }

    fun findImport(import: FObjectImport?): Lazy<UObject>? {
        if (import == null) return null
        if (import.classPackage.text.startsWith("/Script/")) {
            return lazy { provider!!.mappingsProvider.getStruct(import.objectName) }
        }
        //The needed export is located in another asset, try to load it
        if (import.outerIndex.getImportObject() == null) return null
        check(provider != null) { "Loading an import requires a file provider" }
        val pkg = import.outerIndex.getImportObject()?.run { provider.loadGameFile(objectName.text) }
        if (pkg != null) return pkg.findObjectByName(import.objectName.text, import.className.text)
        else FileProvider.logger.warn { "Failed to load referenced import" }
        return null
    }

    override fun findObjectByName(objectName: String, className: String?): Lazy<UObject>? {
        val export = exportMap.firstOrNull {
            it.objectName.text.equals(objectName, true) && (className == null || it.classIndex.getImportObject()?.objectName?.text == className)
        }
        return export?.exportObject
    }

    // region FPackageIndex methods
    fun FPackageIndex.getImportObject() = if (isImport()) importMap[toImport()] else null

    fun FPackageIndex.getOuterImportObject(): FObjectImport? {
        val importObject = getImportObject()
        return importObject?.outerIndex?.getImportObject() ?: importObject
    }

    fun FPackageIndex.getExportObject() = if (isExport()) exportMap[toExport()] else null

    fun FPackageIndex.getResource() = getImportObject() ?: getExportObject()
    // endregion

    override fun toJson(context: Gson, locres: Locres?) = jsonObject(
        "import_map" to gson.toJsonTree(importMap),
        "export_map" to gson.toJsonTree(exportMap),
        "export_properties" to gson.toJsonTree(exports.map {
            it.takeIf { it is UObject }?.toJson(gson, locres)
        })
    )

    //Not really efficient because the uasset gets serialized twice but this is the only way to calculate the new header size
    private fun updateHeader() {
        val uassetWriter = FByteArchiveWriter()
        uassetWriter.game = game.game
        uassetWriter.ver = game.version
        uassetWriter.nameMap = nameMap
        uassetWriter.importMap = importMap
        uassetWriter.exportMap = exportMap
        info.serialize(uassetWriter)
        val nameMapOffset = uassetWriter.pos()
        if (info.nameCount != nameMap.size)
            throw ParserException("Invalid name count, summary says ${info.nameCount} names but name map is ${nameMap.size} entries long")
        nameMap.forEach { it.serialize(uassetWriter) }
        val importMapOffset = uassetWriter.pos()
        if (info.importCount != importMap.size)
            throw ParserException("Invalid import count, summary says ${info.importCount} imports but import map is ${importMap.size} entries long")
        importMap.forEach { it.serialize(uassetWriter) }
        val exportMapOffset = uassetWriter.pos()
        if (info.exportCount != exportMap.size)
            throw ParserException("Invalid export count, summary says ${info.exportCount} exports but export map is ${exportMap.size} entries long")
        exportMap.forEach { it.serialize(uassetWriter) }
        info.totalHeaderSize = uassetWriter.pos()
        info.nameOffset = nameMapOffset
        info.importOffset = importMapOffset
        info.exportOffset = exportMapOffset
    }

    fun write(uassetOutputStream: OutputStream, uexpOutputStream: OutputStream, ubulkOutputStream: OutputStream?) {
        updateHeader()
        val uexpWriter = writer(uexpOutputStream)
        uexpWriter.game = game.game
        uexpWriter.ver = game.version
        uexpWriter.uassetSize = info.totalHeaderSize
        exports.forEach {
            val beginPos = uexpWriter.relativePos()
            it.serialize(uexpWriter)
            val finalPos = uexpWriter.relativePos()
            it.export?.serialOffset = beginPos.toLong()
            it.export?.serialSize = (finalPos - beginPos).toLong()
        }
        uexpWriter.writeUInt32(packageMagic)
        val uassetWriter = writer(uassetOutputStream)
        uassetWriter.game = game.game
        uassetWriter.ver = game.version
        info.serialize(uassetWriter)
        val nameMapPadding = info.nameOffset - uassetWriter.pos()
        if (nameMapPadding > 0)
            uassetWriter.write(ByteArray(nameMapPadding))
        if (info.nameCount != nameMap.size)
            throw ParserException("Invalid name count, summary says ${info.nameCount} names but name map is ${nameMap.size} entries long")
        nameMap.forEach { it.serialize(uassetWriter) }

        val importMapPadding = info.importOffset - uassetWriter.pos()
        if (importMapPadding > 0)
            uassetWriter.write(ByteArray(importMapPadding))
        if (info.importCount != importMap.size)
            throw ParserException("Invalid import count, summary says ${info.importCount} imports but import map is ${importMap.size} entries long")
        importMap.forEach { it.serialize(uassetWriter) }

        val exportMapPadding = info.exportOffset - uassetWriter.pos()
        if (exportMapPadding > 0)
            uassetWriter.write(ByteArray(exportMapPadding))
        if (info.exportCount != exportMap.size)
            throw ParserException("Invalid export count, summary says ${info.exportCount} exports but export map is ${exportMap.size} entries long")
        exportMap.forEach { it.serialize(uassetWriter) }
        ubulkOutputStream?.close()
    }

    fun write(uasset: File, uexp: File, ubulk: File?) {
        val uassetOut = uasset.outputStream()
        val uexpOut = uexp.outputStream()
        val ubulkOut = ubulk?.outputStream()
        write(uassetOut, uexpOut, ubulkOut)
        uassetOut.close()
        uexpOut.close()
        ubulkOut?.close()
    }

    fun writer(outputStream: OutputStream) = FAssetArchiveWriter(outputStream).also {
        it.nameMap = nameMap
        it.importMap = importMap
        it.exportMap = exportMap
    }
}