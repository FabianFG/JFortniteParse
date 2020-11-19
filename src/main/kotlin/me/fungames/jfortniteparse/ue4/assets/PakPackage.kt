package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.jsonObject
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.fort.exports.*
import me.fungames.jfortniteparse.fort.exports.variants.FortCosmeticVariant
import me.fungames.jfortniteparse.ue4.assets.JsonSerializer.toJson
import me.fungames.jfortniteparse.ue4.assets.exports.*
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterial
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInstanceConstant
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.uobject.*
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import me.fungames.jfortniteparse.valorant.exports.*
import java.io.File
import java.io.OutputStream
import java.nio.ByteBuffer

class PakPackage(uasset: ByteBuffer,
                 uexp: ByteBuffer,
                 ubulk: ByteBuffer? = null,
                 fileName: String,
                 provider: FileProvider? = null,
                 game: Ue4Version = provider?.game ?: Ue4Version.GAME_UE4_LATEST) : Package(fileName, provider, game) {
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

    override val exports: List<UExport>
        get() = exportMap.map { it.exportObject.value }

    init {
        val uassetAr = FAssetArchive(uasset, provider, fileName)
        val uexpAr = FAssetArchive(uexp, provider, fileName)
        val ubulkAr = if (ubulk != null) FAssetArchive(ubulk, provider, fileName) else null
        uassetAr.game = game.game
        uassetAr.ver = game.version
        uexpAr.game = game.game
        uexpAr.ver = game.version
        ubulkAr?.game = game.game
        ubulkAr?.ver = game.version

        nameMap = mutableListOf()
        importMap = mutableListOf()
        exportMap = mutableListOf()
        uassetAr.owner = this

        info = FPackageFileSummary(uassetAr)
        if (info.tag != packageMagic)
            throw ParserException("Invalid uasset magic, ${info.tag} != $packageMagic")


        uassetAr.seek(this.info.nameOffset)
        for (i in 0 until info.nameCount)
            nameMap.add(FNameEntry(uassetAr))


        uassetAr.seek(this.info.importOffset)
        for (i in 0 until info.importCount)
            importMap.add(FObjectImport(uassetAr))

        uassetAr.seek(this.info.exportOffset)
        for (i in 0 until info.exportCount)
            exportMap.add(FObjectExport(uassetAr))

        //Setup uexp reader
        uexpAr.owner = this
        uexpAr.uassetSize = info.totalHeaderSize
        uexpAr.bulkDataStartOffset = info.bulkDataStartOffset

        //If attached also setup the ubulk reader
        if (ubulkAr != null) {
            ubulkAr.uassetSize = info.totalHeaderSize
            ubulkAr.uexpSize = exportMap.sumBy { it.serialSize.toInt() }
            ubulkAr.owner = this
            uexpAr.addPayload(PayloadType.UBULK, ubulkAr)
        }

        exportMap.forEach { it.exportObject = lazy {
        /*exports = mutableListOf()
        exportMap.forEach { exportsLazy[it] = lazy {*/
            val origPos = uexpAr.pos()
            val exportType = it.classIndex.run { when {
                index > 0 -> getExportObject()!!.superIndex.getResource()?.objectName
                index < 0 -> getImportObject()!!.objectName
                else -> null
            } } ?: throw ParserException("Can't get class name")
            uexpAr.seekRelative(it.serialOffset.toInt())
            val validPos = (uexpAr.pos() + it.serialSize).toInt()
            val export = constructExport(exportType.text)
            export.export = it
            export.exportType = it.classIndex.getResource()!!.objectName.text
            export.name = it.objectName.text
            export.owner = this
            export.readGuid = true
            export.deserialize(uexpAr, validPos)
            if (validPos != uexpAr.pos())
                logger.warn("Did not read $exportType correctly, ${validPos - uexpAr.pos()} bytes remaining")
            else
                logger.debug("Successfully read $exportType at ${uexpAr.toNormalPos(it.serialOffset.toInt())} with size ${it.serialSize}")
            uexpAr.seek(origPos)
            export
        } }
        /*exportsLazy.values.forEach {
            val value = it.value
            if (!exports.contains(value))
                exports.add(value)
        }*/
        logger.info("Successfully parsed package: $fileName")
    }

    fun constructExport(exportType: String) = when (exportType) {
        // UE generic export classes
        // "BlueprintGeneratedClass" -> UBlueprintGeneratedClass(it)
        "CurveTable" -> UCurveTable()
        "DataTable" -> UDataTable()
        "Level" -> ULevel()
        "Material" -> UMaterial()
        "MaterialInstanceConstant" -> UMaterialInstanceConstant()
        "SoundWave" -> USoundWave()
        "Texture2D" -> UTexture2D()
        "StaticMesh" -> UStaticMesh()
        "StringTable" -> UStringTable()
        // Valorant specific classes
        "CharacterAbilityUIData" -> CharacterAbilityUIData()
        "BaseCharacterPrimaryDataAsset_C",
        "CharacterDataAsset" -> CharacterDataAsset()
        "CharacterRoleDataAsset" -> CharacterRoleDataAsset()
        "CharacterRoleUIData" -> CharacterRoleUIData()
        "CharacterUIData" -> CharacterUIData()
        // Fortnite specific classes
        "AthenaCharacterItemDefinition" -> AthenaCharacterItemDefinition()
        "AthenaEmojiItemDefinition" -> AthenaEmojiItemDefinition()
        "AthenaPickaxeItemDefinition" -> AthenaPickaxeItemDefinition()
        "CatalogMessaging" -> FortCatalogMessaging()
        "FortItemCategory" -> FortItemCategory()
        "FortItemSeriesDefinition" -> FortItemSeriesDefinition()
        "FortMtxOfferData" -> FortMtxOfferData()
        "AthenaItemWrapDefinition",
        "FortAbilityKit",
        "FortBannerTokenType",
        "FortDailyRewardScheduleTokenDefinition",
        "FortHeroType",
        "FortTokenType",
        "FortVariantTokenType",
        "FortWorkerType" -> FortItemDefinition()
        else -> {
            if (exportType.contains("ItemDefinition")) {
                FortItemDefinition()
            } else if (exportType.startsWith("FortCosmetic") && exportType.endsWith("Variant")) {
                FortCosmeticVariant()
            } else
                UObject()
        }
    }

    // Load object by FPackageIndex
    override fun loadObjectGeneric(index: FPackageIndex?): UExport? {
        if (index == null || index.isNull()) return null
        val import = index.getImportObject()
        if (import != null) return loadImport(import)
        val export = index.getExportObject()
        if (export != null) return export.exportObject.value
        return null
    }

    // Load object by FObjectImport
    inline fun <reified T> loadImport(import: FObjectImport?): T? {
        if (import == null) return null
        val loaded = loadImport(import) ?: return null
        return if (loaded is T) loaded else null
    }

    fun loadImport(import: FObjectImport?): UExport? {
        //The needed export is located in another asset, try to load it
        if (import == null || import.outerIndex.getImportObject() == null) return null
        check(provider != null) { "Loading an import requires a file provider" }
        val pkg = provider.loadGameFile(import.outerIndex.getImportObject()!!.objectName.text)
        if (pkg != null) return pkg.findExport(import.objectName.text, import.className.text)
        else FileProvider.logger.warn { "Failed to load referenced import" }
        return null
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

    fun toJson(locres: Locres? = null) = gson.toJson(jsonObject(
        "import_map" to gson.toJsonTree(importMap),
        "export_map" to gson.toJsonTree(exportMap),
        "export_properties" to gson.toJsonTree(exports.map {
            it.takeIf { it is UObject }?.toJson(gson, locres)
        })
    ))

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
        if(info.nameCount != nameMap.size)
            throw ParserException("Invalid name count, summary says ${info.nameCount} names but name map is ${nameMap.size} entries long")
        nameMap.forEach { it.serialize(uassetWriter) }
        val importMapOffset = uassetWriter.pos()
        if(info.importCount != importMap.size)
            throw ParserException("Invalid import count, summary says ${info.importCount} imports but import map is ${importMap.size} entries long")
        importMap.forEach { it.serialize(uassetWriter) }
        val exportMapOffset = uassetWriter.pos()
        if(info.exportCount != exportMap.size)
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
        if(nameMapPadding > 0)
            uassetWriter.write(ByteArray(nameMapPadding))
        if(info.nameCount != nameMap.size)
            throw ParserException("Invalid name count, summary says ${info.nameCount} names but name map is ${nameMap.size} entries long")
        nameMap.forEach { it.serialize(uassetWriter) }

        val importMapPadding = info.importOffset - uassetWriter.pos()
        if(importMapPadding > 0)
            uassetWriter.write(ByteArray(importMapPadding))
        if(info.importCount != importMap.size)
            throw ParserException("Invalid import count, summary says ${info.importCount} imports but import map is ${importMap.size} entries long")
        importMap.forEach { it.serialize(uassetWriter) }

        val exportMapPadding = info.exportOffset - uassetWriter.pos()
        if(exportMapPadding > 0)
            uassetWriter.write(ByteArray(exportMapPadding))
        if(info.exportCount != exportMap.size)
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