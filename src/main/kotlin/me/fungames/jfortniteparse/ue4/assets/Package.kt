package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.UClass.Companion.logger
import me.fungames.jfortniteparse.ue4.assets.exports.*
import me.fungames.jfortniteparse.ue4.assets.exports.ItemDefinition
import me.fungames.jfortniteparse.ue4.assets.exports.fort.*
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterial
import me.fungames.jfortniteparse.ue4.assets.exports.mats.UMaterialInstanceConstant
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import me.fungames.jfortniteparse.ue4.assets.exports.valorant.*
import me.fungames.jfortniteparse.ue4.assets.objects.FNameEntry
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectImport
import me.fungames.jfortniteparse.ue4.assets.objects.FPackageFileSummary
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import java.io.File
import java.io.OutputStream

@ExperimentalUnsignedTypes
class Package(uasset : ByteArray, uexp : ByteArray, ubulk : ByteArray? = null, val name : String, provider: FileProvider? = null, var game : Ue4Version = Ue4Version.GAME_UE4_LATEST) {

    companion object {
        val packageMagic = 0x9E2A83C1u
        val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(JsonSerializer.packageConverter)
            .registerTypeAdapter(JsonSerializer.importSerializer)
            .registerTypeAdapter(JsonSerializer.exportSerializer)
            .registerTypeAdapter(JsonSerializer.uobjectSerializer)
            .create()
    }

    constructor(uasset : File, uexp : File, ubulk : File?, provider: FileProvider? = null, game : Ue4Version = Ue4Version.GAME_UE4_LATEST) : this(uasset.readBytes(), uexp.readBytes(),
        ubulk?.readBytes(), uasset.nameWithoutExtension, provider, game)

    val info : FPackageFileSummary
    val nameMap : MutableList<FNameEntry>
    val importMap : MutableList<FObjectImport>
    val exportMap : MutableList<FObjectExport>

    private val exportsLazy = mutableMapOf<FObjectExport, Lazy<UExport>>()

    val exports : List<UExport>

    init {
        val uassetAr = FAssetArchive(uasset, provider, name)
        val uexpAr = FAssetArchive(uexp, provider, name)
        val ubulkAr = if (ubulk != null) FAssetArchive(ubulk, provider, name) else null
        uassetAr.game = game.game
        uassetAr.ver = game.version
        uexpAr.game = game.game
        uexpAr.ver = game.version
        ubulkAr?.game = game.game
        ubulkAr?.ver = game.version

        nameMap = mutableListOf()
        uassetAr.nameMap = nameMap
        importMap = mutableListOf()
        uassetAr.importMap = importMap
        exportMap = mutableListOf()
        uassetAr.exportMap = exportMap

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
        uexpAr.nameMap = nameMap
        uexpAr.importMap = importMap
        uexpAr.exportMap = exportMap
        uexpAr.exports = exportsLazy
        uexpAr.uassetSize = info.totalHeaderSize
        uexpAr.info = info

        //If attached also setup the ubulk reader
        if (ubulkAr != null) {
            ubulkAr.uassetSize = info.totalHeaderSize
            ubulkAr.uexpSize = exportMap.sumBy { it.serialSize.toInt() }
            ubulkAr.info = info
            ubulkAr.nameMap = nameMap
            ubulkAr.importMap = importMap
            ubulkAr.exportMap = exportMap
            ubulkAr.exports = exportsLazy
            uexpAr.addPayload(PayloadType.UBULK, ubulkAr)
        }

        exports = mutableListOf()
        exportMap.forEach { exportsLazy[it] = lazy {
            val origPos = uexpAr.pos()
            val exportType = it.classIndex.name.substringAfter("Default__")
            uexpAr.seekRelative(it.serialOffset.toInt())
            val validPos = (uexpAr.pos() + it.serialSize).toInt()
            val export = when(exportType) {
                "BlueprintGeneratedClass" -> {
                    val className = it.templateIndex.importObject?.className?.text
                    if (className != null)
                        readExport(uexpAr, className, it, validPos)
                    else {
                        logger.warn { "Couldn't find content class of BlueprintGeneratedClass, attempting normal UObject deserialization" }
                        readExport(uexpAr, exportType, it, validPos)
                    }
                }
                else -> readExport(uexpAr, exportType, it, validPos)
            }
            if (validPos != uexpAr.pos())
                logger.warn("Did not read $exportType correctly, ${validPos - uexpAr.pos()} bytes remaining")
            else
                logger.debug("Successfully read $exportType at ${uexpAr.toNormalPos(it.serialOffset.toInt())} with size ${it.serialSize}")
            uexpAr.seek(origPos)
            export
        } }
        exportsLazy.values.forEach {
            val value = it.value
            if (!exports.contains(value))
                exports.add(value)
        }
        matchValorantCharacterAbilities()
        uassetAr.clearImportCache()
        uexpAr.clearImportCache()
        ubulkAr?.clearImportCache()
        logger.info("Successfully parsed package : $name")
    }

    fun readExport(uexpAr : FAssetArchive, exportType : String, it : FObjectExport, validPos : Int) = when(exportType) {
        //UE generic export classes
        "Texture2D" -> UTexture2D(uexpAr, it)
        "SoundWave" -> USoundWave(uexpAr, it)
        "DataTable" -> UDataTable(uexpAr, it)
        "CurveTable" -> UCurveTable(uexpAr, it)
        "StringTable" -> UStringTable(uexpAr, it)
        "StaticMesh" -> UStaticMesh(uexpAr, it, validPos)
        "Material" -> UMaterial(uexpAr, it, validPos)
        "MaterialInstanceConstant" -> UMaterialInstanceConstant(uexpAr, it)
        //Valorant specific classes
        "CharacterUIData" -> CharacterUIData(uexpAr, it)
        "CharacterAbilityUIData" -> CharacterAbilityUIData(uexpAr, it)
        "BaseCharacterPrimaryDataAsset_C", "CharacterDataAsset" -> CharacterDataAsset(uexpAr, it)
        "CharacterRoleDataAsset" -> CharacterRoleDataAsset(uexpAr, it)
        "CharacterRoleUIData" -> CharacterRoleUIData(uexpAr, it)
        //Fortnite Specific Classes
        "FortMtxOfferData" -> FortMtxOfferData(uexpAr, it)
        "FortItemCategory" -> FortItemCategory(uexpAr, it)
        "CatalogMessaging" -> FortCatalogMessaging(uexpAr, it)
        "FortItemSeriesDefinition" -> FortItemSeriesDefinition(uexpAr, it)
        "AthenaItemWrapDefinition", "FortBannerTokenType",
        "FortVariantTokenType", "FortHeroType",
        "FortTokenType", "FortWorkerType",
        "FortDailyRewardScheduleTokenDefinition",
        "FortAbilityKit" -> ItemDefinition(uexpAr, it)
        else -> {
            if (exportType.contains("ItemDefinition")) {
                ItemDefinition(uexpAr, it)
            } else if (exportType.startsWith("FortCosmetic") && exportType.endsWith("Variant")) {
                FortCosmeticVariant(uexpAr, it)
            } else
                UObject(uexpAr, it)
        }
    }

    private fun matchValorantCharacterAbilities() {
        val uiData = getExportOfTypeOrNull<CharacterUIData>() ?: return
        uiData.abilitiesWithIndex.forEach { slot, i ->
            val export = exports.getOrNull(i - 1)
            if (export != null && export is CharacterAbilityUIData)
                uiData.abilities[slot] = export
        }
    }

    /**
     * @return the first export of the given type
     * @throws IllegalArgumentException if there is no export of the given type
     */
    @Throws(IllegalArgumentException::class)
    inline fun <reified T : UExport> getExportOfType() = getExportsOfType<T>().first()

    /**
     * @return the first export of the given type or null if there is no
     */
    inline fun <reified T : UExport> getExportOfTypeOrNull() = getExportsOfType<T>().firstOrNull()

    /**
     * @return the all exports of the given type
     */
    inline fun <reified T : UExport> getExportsOfType() = exports.filterIsInstance<T>()

    fun applyLocres(locres : Locres?) {
        exports.forEach { it.applyLocres(locres) }
    }

    fun toJson() = gson.toJson(this)!!

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

    fun writer(outputStream: OutputStream) = FAssetArchiveWriter(
        outputStream
    ).apply {
        nameMap = this@Package.nameMap
        importMap = this@Package.importMap
        exportMap = this@Package.exportMap
    }
}