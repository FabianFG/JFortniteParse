package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.jsonSerializer
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.fungames.jfortniteparse.GSuppressMissingSchemaErrors
import me.fungames.jfortniteparse.LOG_STREAMING
import me.fungames.jfortniteparse.exceptions.MissingSchemaException
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.assets.exports.UStruct
import me.fungames.jfortniteparse.ue4.assets.reader.FExportArchive
import me.fungames.jfortniteparse.ue4.asyncloading2.*
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.uobject.*
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import me.fungames.jfortniteparse.util.get
import java.nio.ByteBuffer

/**
 * Linker for I/O Store packages
 */
class IoPackage : Package {
    val packageId: FPackageId
    val globalPackageStore: FPackageStore
    val summary: FPackageSummary
    val nameMap: FNameMap
    val importMap: Array<FPackageObjectIndex>
    val exportMap: Array<FExportMapEntry>
    val exportBundleHeaders: Array<FExportBundleHeader>
    val exportBundleEntries: Array<FExportBundleEntry>
    val graphData: Array<FImportedPackage>
    val importedPackages: Lazy<List<IoPackage?>>
    override val exportsLazy: List<Lazy<UObject>>
    var bulkDataStartOffset = 0

    constructor(uasset: ByteArray,
                packageId: FPackageId,
                storeEntry: FPackageStoreEntry,
                globalPackageStore: FPackageStore,
                provider: FileProvider,
                game: Ue4Version = provider.game) : super("", provider, game) {
        this.packageId = packageId
        this.globalPackageStore = globalPackageStore
        val Ar = FByteArchive(uasset)
        summary = FPackageSummary(Ar)

        // Name map
        nameMap = FNameMap()
        if (summary.nameMapNamesSize > 0) {
            val nameMapNamesData = FByteArchive(ByteBuffer.wrap(uasset, summary.nameMapNamesOffset, summary.nameMapNamesSize))
            val nameMapHashesData = FByteArchive(ByteBuffer.wrap(uasset, summary.nameMapHashesOffset, summary.nameMapHashesSize))
            nameMap.load(nameMapNamesData, nameMapHashesData, FMappedName.EType.Package)
        }

        val diskPackageName = nameMap.getName(summary.name)
        fileName = diskPackageName.text
        packageFlags = summary.packageFlags.toInt()
        name = fileName

        // Import map
        Ar.seek(summary.importMapOffset)
        val importMapSize = summary.exportMapOffset - summary.importMapOffset
        val importCount = importMapSize / 8
        importMap = Array(importCount) { FPackageObjectIndex(Ar) }

        // Export map
        Ar.seek(summary.exportMapOffset)
        val exportCount = storeEntry.exportCount
        exportMap = Array(exportCount) { FExportMapEntry(Ar) }
        exportsLazy = (arrayOfNulls<Lazy<UObject>>(exportCount) as Array<Lazy<UObject>>).toMutableList()

        // Export bundles
        Ar.seek(summary.exportBundlesOffset)
        val exportBundleCount = storeEntry.exportBundleCount
        exportBundleHeaders = Array(exportBundleCount) { FExportBundleHeader(Ar) }
        exportBundleEntries = Array(exportCount * 2) { FExportBundleEntry(Ar) }

        // Graph data
        Ar.seek(summary.graphDataOffset)
        graphData = Ar.readTArray { FImportedPackage(Ar) }

        // Preload dependencies
        importedPackages = lazy { graphData.map { provider.loadGameFile(it.importedPackageId) } }

        // Populate lazy exports
        val allExportDataOffset = summary.graphDataOffset + summary.graphDataSize
        var currentExportDataOffset = allExportDataOffset
        for (exportBundle in exportBundleHeaders) {
            for (i in 0u until exportBundle.entryCount) {
                val entry = exportBundleEntries[exportBundle.firstEntryIndex + i]
                if (entry.commandType == FExportBundleEntry.EExportCommandType.ExportCommandType_Serialize) {
                    val localExportIndex = entry.localExportIndex
                    val export = exportMap[localExportIndex]
                    val localExportDataOffset = currentExportDataOffset
                    exportsLazy[localExportIndex] = lazy {
                        // Create
                        val objectName = nameMap.getName(export.objectName)
                        val obj = constructExport(resolveObjectIndex(export.classIndex)?.getObject()?.value as UStruct?)
                        obj.name = objectName.text
                        obj.outer = (resolveObjectIndex(export.outerIndex) as? ResolvedExportObject)?.exportObject?.value ?: this
                        obj.template = (resolveObjectIndex(export.templateIndex) as? ResolvedExportObject)?.exportObject
                        obj.flags = export.objectFlags.toInt()

                        // Serialize
                        val Ar = FExportArchive(ByteBuffer.wrap(uasset), obj, this)
                        Ar.useUnversionedPropertySerialization = (packageFlags and EPackageFlags.PKG_UnversionedProperties.value) != 0
                        Ar.uassetSize = export.cookedSerialOffset.toInt() - localExportDataOffset
                        Ar.bulkDataStartOffset = bulkDataStartOffset
                        Ar.seek(localExportDataOffset)
                        val validPos = Ar.pos() + export.cookedSerialSize.toInt()
                        try {
                            obj.deserialize(Ar, validPos)
                            if (validPos != Ar.pos()) {
                                LOG_STREAMING.warn { "Did not read ${obj.exportType} correctly, ${validPos - Ar.pos()} bytes remaining (${obj.getPathName()})" }
                            }
                        } catch (e: Throwable) {
                            if (e is MissingSchemaException && !GSuppressMissingSchemaErrors) {
                                LOG_STREAMING.warn(e.message)
                            } else {
                                throw e
                            }
                        }
                        obj
                    }
                    currentExportDataOffset += export.cookedSerialSize.toInt()
                }
            }
        }
        bulkDataStartOffset = currentExportDataOffset
        //logger.info { "Successfully parsed package : $name" }
    }

    class FImportedPackage(Ar: FArchive) {
        val importedPackageId = FPackageId(Ar)
        val externalArcs = Ar.readTArray { FArc(Ar) }
    }

    class FArc(Ar: FArchive) {
        val fromExportBundleIndex = Ar.readInt32()
        val toExportBundleIndex = Ar.readInt32()
    }

    fun resolveObjectIndex(index: FPackageObjectIndex?, throwIfNotFound: Boolean = true): ResolvedObject? {
        if (index == null) {
            return null
        }
        when {
            index.isExport() -> return ResolvedExportObject(index.toExport().toInt(), this@IoPackage)
            index.isScriptImport() -> return globalPackageStore.scriptObjectEntriesMap[index]?.let { ResolvedScriptObject(it, this@IoPackage) }
            index.isPackageImport() -> for (pkg in importedPackages.value) {
                pkg?.exportMap?.forEachIndexed { exportIndex, exportMapEntry ->
                    if (exportMapEntry.globalImportIndex == index) {
                        return ResolvedExportObject(exportIndex, pkg)
                    }
                }
            }
            index.isNull() -> return null
        }
        if (throwIfNotFound) {
            throw ParserException("Missing %s import 0x%016X for package %s".format(
                if (index.isScriptImport()) "script" else "package",
                index.value().toLong(),
                fileName
            ))
        }
        return null
    }

    abstract class ResolvedObject(val pkg: IoPackage) {
        abstract fun getName(): FName
        open fun getOuter(): ResolvedObject? = null
        open fun getSuper(): ResolvedObject? = null
        open fun getObject(): Lazy<out UObject>? = null
    }

    class ResolvedExportObject(exportIndex: Int, pkg: IoPackage) : ResolvedObject(pkg) {
        val exportMapEntry = pkg.exportMap[exportIndex]
        val exportObject = pkg.exportsLazy[exportIndex]
        override fun getName() = pkg.nameMap.getName(exportMapEntry.objectName)
        override fun getOuter() = pkg.resolveObjectIndex(exportMapEntry.outerIndex)
        override fun getSuper() = pkg.resolveObjectIndex(exportMapEntry.superIndex)
        override fun getObject() = exportObject
    }

    class ResolvedScriptObject(val scriptImport: FScriptObjectEntry, pkg: IoPackage) : ResolvedObject(pkg) {
        override fun getName() = scriptImport.objectName.toName()
        override fun getOuter() = pkg.resolveObjectIndex(scriptImport.outerIndex)
        override fun getObject() = lazy {
            val structName = getName()
            var struct = pkg.provider?.mappingsProvider?.getStruct(structName)
            if (struct == null) {
                if (pkg.packageFlags and EPackageFlags.PKG_UnversionedProperties.value != 0) {
                    throw MissingSchemaException("Unknown struct $structName")
                }
                struct = UScriptStruct(structName)
            }
            struct
        }
    }

    override fun <T : UObject> findObject(index: FPackageIndex?) = when {
        index == null || index.isNull() -> null
        index.isExport() -> exportsLazy.getOrNull(index.toExport())
        else -> importMap.getOrNull(index.toImport())?.let { resolveObjectIndex(it, false) }?.getObject()
    } as Lazy<T>?

    override fun findObjectByName(objectName: String, className: String?): Lazy<UObject>? {
        val exportIndex = exportMap.indexOfFirst {
            nameMap.getName(it.objectName).text.equals(objectName, true) && (className == null || resolveObjectIndex(it.classIndex)?.getName()?.text == className)
        }
        return if (exportIndex != -1) exportsLazy[exportIndex] else null
    }

    override fun toJson(context: Gson, locres: Locres?) = jsonObject(
        "import_map" to gson.toJsonTree(importMap),
        "export_map" to gson.toJsonTree(exportMap),
        "export_properties" to gson.toJsonTree(exports.map {
            it.toJson(gson, locres)
        })
    )

    fun findObjectMinimal(index: FPackageIndex?) = when {
        index == null || index.isNull() -> null
        index.isExport() -> ResolvedExportObject(index.toExport(), this)
        else -> importMap.getOrNull(index.toImport())?.let { resolveObjectIndex(it, false) }
    }

    fun dumpHeaderToJson(): JsonObject {
        val gson = gson.newBuilder().registerTypeAdapter(jsonSerializer<FMappedName> { JsonPrimitive(nameMap.getNameOrNull(it.src)?.text) }).create()
        return JsonObject().apply {
            add("summary", gson.toJsonTree(summary))
            add("nameMap", gson.toJsonTree(nameMap))
            add("importMap", gson.toJsonTree(importMap))
            add("exportMap", gson.toJsonTree(exportMap))
            add("exportBundleHeaders", gson.toJsonTree(exportBundleHeaders))
            add("exportBundleEntries", gson.toJsonTree(exportBundleEntries))
            add("graphData", gson.toJsonTree(graphData))
        }
    }
}