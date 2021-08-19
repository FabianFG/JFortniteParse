package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson
import me.fungames.jfortniteparse.GFatalObjectSerializationErrors
import me.fungames.jfortniteparse.LOG_STREAMING
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UEnum
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.uobject.*
import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import java.io.File
import java.io.OutputStream
import java.nio.ByteBuffer

class PakPackage(
    uasset: ByteBuffer,
    uexp: ByteBuffer? = null,
    ubulk: ByteBuffer? = null,
    fileName: String,
    provider: FileProvider? = null,
    versions: VersionContainer = provider?.versions ?: VersionContainer.DEFAULT
) : Package(fileName, provider, versions) {
    companion object {
        val packageMagic = 0x9E2A83C1u
    }

    constructor(uasset: ByteArray, uexp: ByteArray? = null, ubulk: ByteArray? = null, name: String, provider: FileProvider? = null, versions: VersionContainer = VersionContainer.DEFAULT) :
        this(ByteBuffer.wrap(uasset), uexp?.let { ByteBuffer.wrap(it) }, ubulk?.let { ByteBuffer.wrap(it) }, name, provider, versions)

    constructor(uasset: File, uexp: File, ubulk: File?, provider: FileProvider? = null, versions: VersionContainer = provider?.versions ?: VersionContainer.DEFAULT) : this(
        uasset.readBytes(), uexp.readBytes(), ubulk?.readBytes(),
        uasset.nameWithoutExtension, provider, versions
    )

    val info: FPackageFileSummary
    val nameMap: MutableList<String>
    val importMap: MutableList<FObjectImport>
    val exportMap: MutableList<FObjectExport>

    override val exportsLazy: List<Lazy<UObject>>
        get() = exportMap.map { it.exportObject }

    init {
        name = provider?.compactFilePath(fileName)?.substringBeforeLast('.') ?: fileName
        val uassetAr = FAssetArchive(uasset, provider, fileName)
        val uexpAr = if (uexp != null) FAssetArchive(uexp, provider, fileName) else uassetAr
        val ubulkAr = ubulk?.let { FAssetArchive(it, provider, fileName) }
        uassetAr.versions = versions
        uassetAr.owner = this
        uexpAr.versions = versions
        uexpAr.owner = this
        ubulkAr?.versions = versions
        ubulkAr?.owner = this

        info = FPackageFileSummary(uassetAr)
        if (info.tag != packageMagic)
            throw ParserException("Invalid uasset magic, ${info.tag} != $packageMagic")

        val ver = info.fileVersionUE4
        if (ver > 0) { // TODO IMPORTANT
            uassetAr.ver = ver
            uexpAr.ver = ver
            ubulkAr?.ver = ver
        }
        packageFlags = info.packageFlags.toInt()

        uassetAr.seek(info.nameOffset)
        nameMap = MutableList(info.nameCount) {
            val name = uassetAr.readString()
            uassetAr.skip(4) // skip nonCasePreservingHash (uint16) and casePreservingHash (uint16)
            name
        }

        uassetAr.seek(info.importOffset)
        importMap = MutableList(info.importCount) { FObjectImport(uassetAr) }

        uassetAr.seek(info.exportOffset)
        exportMap = MutableList(info.exportCount) { FObjectExport(uassetAr) }

        //Setup uexp reader
        if (uexp != null) {
            uexpAr.uassetSize = info.totalHeaderSize
        }
        uexpAr.bulkDataStartOffset = info.bulkDataStartOffset
        uexpAr.useUnversionedPropertySerialization = (packageFlags and EPackageFlags.PKG_UnversionedProperties.value) != 0

        //If attached also setup the ubulk reader
        if (ubulkAr != null) {
            ubulkAr.uassetSize = info.totalHeaderSize
            ubulkAr.uexpSize = exportMap.sumOf { it.serialSize.toInt() }
            uexpAr.addPayload(PayloadType.UBULK, ubulkAr)
        }

        exportMap.forEach { export ->
            export.exportObject = lazy {
                val obj = constructExport(export.classIndex.load())
                obj.export = export
                obj.name = export.objectName.text
                obj.outer = export.outerIndex.load() ?: this
                //obj.template = findObject(export.templateIndex)
                obj.flags = export.objectFlags.toInt()

                val uexpAr = uexpAr.clone()
                uexpAr.seekRelative(export.serialOffset.toInt())
                val validPos = (uexpAr.pos() + export.serialSize).toInt()
                try {
                    obj.deserialize(uexpAr, validPos)
                    if (validPos != uexpAr.pos()) {
                        LOG_STREAMING.warn { "Did not read ${obj.exportType} correctly, ${validPos - uexpAr.pos()} bytes remaining (${obj.getPathName()})" }
                    } else {
                        LOG_STREAMING.debug { "Successfully read ${obj.exportType} at ${uexpAr.toNormalPos(export.serialOffset.toInt())} with size ${export.serialSize}" }
                    }
                } catch (e: Throwable) {
                    if (GFatalObjectSerializationErrors) {
                        throw e
                    } else {
                        LOG_STREAMING.error(e) { "Could not read ${obj.exportType} correctly" }
                    }
                }
                obj
            }
        }

        //logger.info { "Successfully parsed package : $name" }
    }

    // region Object resolvers
    override fun <T : UObject> findObject(index: FPackageIndex?) = when {
        index == null || index.isNull() -> null
        index.isImport() -> resolveImport(index).getObject()
        else -> exportMap.getOrNull(index.toExport())?.exportObject
    } as Lazy<T>?

    override fun findObjectByName(objectName: String, className: String?): Lazy<UObject>? {
        val export = exportMap.firstOrNull {
            it.objectName.text.equals(objectName, true) && (className == null || it.classIndex.name.text == className)
        }
        return export?.exportObject
    }

    override fun findObjectMinimal(index: FPackageIndex?) = when {
        index == null || index.isNull() -> null
        index.isImport() -> resolveImport(index)
        else -> ResolvedExportObject(index.toExport(), this)
    }

    fun resolveImport(importIndex: FPackageIndex): ResolvedObject {
        val import = importMap[importIndex.toImport()]
        var outerMostIndex = importIndex
        var outerMostImport: FObjectImport
        while (true) {
            outerMostImport = importMap[outerMostIndex.toImport()]
            if (outerMostImport.outerIndex.isNull())
                break
            outerMostIndex = outerMostImport.outerIndex
        }

        outerMostImport = importMap[outerMostIndex.toImport()]
        // We don't support loading script packages, so just return a fallback
        if (outerMostImport.objectName.text.startsWith("/Script/")) {
            return ResolvedImportObject(import, this)
        }

        val importPackage = provider?.loadGameFile(outerMostImport.objectName.text) as? PakPackage
        if (importPackage == null) {
            LOG_STREAMING.warn("Missing native package ({}) for import of {} in {}.", outerMostImport.objectName, import.objectName, name)
            return ResolvedImportObject(import, this)
        }

        var outer: String? = null
        if (outerMostIndex != import.outerIndex && import.outerIndex.isImport()) {
            //var outerImport = importMap[import.outerIndex.toImport()]
            outer = resolveImport(import.outerIndex).getPathName()
            /*if (outer == null) {
                LOG_STREAMING.warn("Missing outer for import of ({}): {} in {} was not found, but the package exists.", name, outerImport.objectName, importPackage.getFullName())
                return ResolvedImportObject(import, this)
            }*/
        }

        for ((i, export) in importPackage.exportMap.withIndex()) {
            if (export.objectName != import.objectName)
                continue
            val thisOuter = importPackage.findObjectMinimal(export.outerIndex)
            if (thisOuter?.getPathName() == outer)
                return ResolvedExportObject(i, importPackage)
        }

        LOG_STREAMING.warn("Missing import of ({}): {} in {} was not found, but the package exists.", name, import.objectName, importPackage.getFullName())
        return ResolvedImportObject(import, this)
    }

    private class ResolvedExportObject(exportIndex: Int, pkg: PakPackage) : ResolvedObject(pkg, exportIndex) {
        val export = pkg.exportMap[exportIndex]
        override fun getName() = export.objectName
        override fun getOuter() = pkg.findObjectMinimal(export.outerIndex) ?: ResolvedLoadedObject(pkg)
        override fun getClazz() = pkg.findObjectMinimal(export.classIndex)
        override fun getSuper() = pkg.findObjectMinimal(export.superIndex)
        override fun getObject() = export.exportObject
    }

    /** Fallback if we cannot resolve the export in another package */
    private class ResolvedImportObject(val import: FObjectImport, pkg: PakPackage) : ResolvedObject(pkg) {
        override fun getName() = import.objectName
        override fun getOuter() = pkg.findObjectMinimal(import.outerIndex)
        override fun getClazz() = ResolvedLoadedObject(UScriptStruct(getName()))
        override fun getObject() = lazy {
            val name = getName()
            val struct = pkg.provider?.mappingsProvider?.getStruct(name)
            if (struct != null) {
                struct
            } else {
                val enumValues = pkg.provider?.mappingsProvider?.getEnum(name)
                if (enumValues != null) {
                    val enum = UEnum()
                    enum.name = name.text
                    enum.names = Array(enumValues.size) { FName("$name::${enumValues[it]}") to it.toLong() }
                    enum
                } else null
            }
        }
    }
    // endregion

    override fun toJson(context: Gson, locres: Locres?) = jsonObject(
        "import_map" to gson.toJsonTree(importMap),
        "export_map" to gson.toJsonTree(exportMap),
        "export_properties" to gson.toJsonTree(exports.map {
            it.toJson(gson, locres)
        })
    )

    //Not really efficient because the uasset gets serialized twice but this is the only way to calculate the new header size
    private fun updateHeader() {
        val uassetWriter = FByteArchiveWriter()
        uassetWriter.versions = versions
        uassetWriter.nameMap = nameMap
        uassetWriter.importMap = importMap
        uassetWriter.exportMap = exportMap
        info.serialize(uassetWriter)
        val nameMapOffset = uassetWriter.pos()
        if (info.nameCount != nameMap.size)
            throw ParserException("Invalid name count, summary says ${info.nameCount} names but name map is ${nameMap.size} entries long")
        nameMap.forEach {
            uassetWriter.writeString(it)
            uassetWriter.writeUInt16(0u)
            uassetWriter.writeUInt16(0u)
        }
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
        uexpWriter.versions = versions
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
        uassetWriter.versions = versions
        info.serialize(uassetWriter)
        val nameMapPadding = info.nameOffset - uassetWriter.pos()
        if (nameMapPadding > 0)
            uassetWriter.write(ByteArray(nameMapPadding))
        if (info.nameCount != nameMap.size)
            throw ParserException("Invalid name count, summary says ${info.nameCount} names but name map is ${nameMap.size} entries long")
        nameMap.forEach {
            uassetWriter.writeString(it)
            uassetWriter.writeUInt16(0u)
            uassetWriter.writeUInt16(0u)
        }

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