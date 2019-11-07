package me.fungames.jfortniteparse.ue4.assets

import FortItemCategory
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UEClass.Companion.logger
import me.fungames.jfortniteparse.ue4.assets.exports.*
import me.fungames.jfortniteparse.ue4.assets.exports.athena.AthenaItemDefinition
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortCosmeticVariant
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortHeroType
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortMtxOfferData
import me.fungames.jfortniteparse.ue4.assets.exports.fort.FortWeaponMeleeItemDefinition
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArrayArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import java.io.File
import java.io.OutputStream

@ExperimentalUnsignedTypes
class Package(uasset : ByteArray, uexp : ByteArray, ubulk : ByteArray? = null, name : String) {

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

    constructor(uasset : File, uexp : File, ubulk : File?) : this(uasset.readBytes(), uexp.readBytes(),
        ubulk?.readBytes(), uasset.nameWithoutExtension)

    private val uassetAr = FAssetArchive(uasset)
    private val uexpAr = FAssetArchive(uexp)
    private val ubulkAr = if (ubulk != null) FAssetArchive(ubulk) else null

    val info : FPackageFileSummary
    val nameMap : MutableList<FNameEntry>
    val importMap : MutableList<FObjectImport>
    val exportMap : MutableList<FObjectExport>

    val exports = mutableListOf<UEExport>()

    init {
        info = FPackageFileSummary(uassetAr)
        if (info.tag != packageMagic)
            throw ParserException("Invalid uasset magic, ${info.tag} != $packageMagic")


        uassetAr.seek(this.info.nameOffset)
        nameMap = mutableListOf()
        uassetAr.nameMap = nameMap
        for (i in 0 until info.nameCount)
            nameMap.add(FNameEntry(uassetAr))


        uassetAr.seek(this.info.importOffset)
        importMap = mutableListOf()
        uassetAr.importMap = importMap
        for (i in 0 until info.importCount)
            importMap.add(FObjectImport(uassetAr))

        uassetAr.seek(this.info.exportOffset)
        exportMap = mutableListOf()
        uassetAr.exportMap = exportMap
        for (i in 0 until info.exportCount)
            exportMap.add(FObjectExport(uassetAr))

        //Setup uexp reader
        uexpAr.nameMap = nameMap
        uexpAr.importMap = importMap
        uexpAr.exportMap = exportMap
        uexpAr.uassetSize = info.totalHeaderSize

        //If attached also setup the ubulk reader
        if (ubulkAr != null) {
            ubulkAr.uassetSize = info.totalHeaderSize
            ubulkAr.uexpSize = exportMap.sumBy { it.serialSize.toInt() }
            uexpAr.addPayload(PayloadType.UBULK, ubulkAr)
        }

        exportMap.forEach {
            val exportType = it.classIndex.importName
            uexpAr.seekRelative(it.serialOffset.toInt())
            val validPos = uexpAr.pos() + it.serialSize
            when (exportType) {
                //UE generic export classes
                "Texture2D" -> exports.add(UTexture2D(uexpAr, it))
                "SoundWave" -> exports.add(USoundWave(uexpAr, it))
                "DataTable" -> exports.add(UDataTable(uexpAr, it))
                "CurveTable" -> exports.add(UCurveTable(uexpAr, it))
                "FortMtxOfferData" -> exports.add(FortMtxOfferData(uexpAr, it))
                "FortHeroType" -> exports.add(FortHeroType(uexpAr, it))
                "FortWeaponMeleeItemDefinition" -> exports.add(FortWeaponMeleeItemDefinition(uexpAr, it))
                "FortItemCategory" -> exports.add(FortItemCategory(uexpAr, it))
                else -> {
                    if (exportType.startsWith("Athena") && exportType.endsWith("ItemDefinition")) {
                        exports.add(AthenaItemDefinition(uexpAr, it))
                    } else if (exportType.startsWith("FortCosmetic") && exportType.endsWith("Variant")) {
                        val variant = FortCosmeticVariant(uexpAr, it)
                        matchItemDefAndVariant(variant)
                        exports.add(variant)
                    } else
                        exports.add(UObject(uexpAr, it))

                }
            }
            if (validPos != uexpAr.pos().toLong())
                logger.warn("Did not read $exportType correctly, ${validPos - uexpAr.pos()} bytes remaining")
            else
                logger.debug("Successfully read $exportType at ${uexpAr.toNormalPos(it.serialOffset.toInt())} with size ${it.serialSize}")
        }
        logger.info("Successfully parsed package : $name")
    }

    private fun matchItemDefAndVariant(variant: FortCosmeticVariant) {
        val itemDef = getExportOfTypeOrNull<AthenaItemDefinition>() ?: return
        itemDef.variants.add(variant)
    }

    /**
     * @return the first export of the given type
     * @throws IllegalArgumentException if there is no export of the given type
     */
    @Throws(IllegalArgumentException::class)
    inline fun <reified T : UEExport> getExportOfType() = getExportsOfType<T>().first()

    /**
     * @return the first export of the given type or null if there is no
     */
    inline fun <reified T : UEExport> getExportOfTypeOrNull() = getExportsOfType<T>().firstOrNull()

    /**
     * @return the all exports of the given type
     */
    inline fun <reified T : UEExport> getExportsOfType() = exports.filterIsInstance<T>()

    fun applyLocres(locres : Locres?) {
        exports.forEach { it.applyLocres(locres) }
    }

    fun toJson() = gson.toJson(this)!!

    //Not really efficient because the uasset gets serialized twice but this is the only way to calculate the new header size
    private fun updateHeader() {
        val uassetWriter = FByteArrayArchiveWriter()
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
        val ubulkOut = if(this.ubulkAr != null) ubulk?.outputStream() else null
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