package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.jsonSerializer
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.fungames.jfortniteparse.GSuppressMissingSchemaErrors
import me.fungames.jfortniteparse.GSuppressUnknownPropertyExceptionClasses
import me.fungames.jfortniteparse.exceptions.MissingSchemaException
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.exceptions.UnknownPropertyException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.reader.FExportArchive
import me.fungames.jfortniteparse.ue4.asyncloading2.*
import me.fungames.jfortniteparse.ue4.objects.uobject.*
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
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
    val importedPackages: Lazy<List<IoPackage>>
    override val exportsLazy: List<Lazy<UExport>>

    constructor(uasset: ByteArray,
                packageId: FPackageId,
                storeEntry: FPackageStoreEntry,
                globalPackageStore: FPackageStore,
                provider: FileProvider,
                game: Ue4Version = provider.game) : super("", provider, game) {
        this.packageId = packageId
        this.globalPackageStore = globalPackageStore
        val Ar = FAssetArchive(uasset, provider, fileName)
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

        // Import map
        Ar.seek(summary.importMapOffset)
        val importMapSize = summary.exportMapOffset - summary.importMapOffset
        val importCount = importMapSize / 8
        importMap = Array(importCount) { FPackageObjectIndex(Ar) }

        // Export map
        Ar.seek(summary.exportMapOffset)
        val exportCount = storeEntry.exportCount
        exportMap = Array(exportCount) { FExportMapEntry(Ar) }
        exportsLazy = (arrayOfNulls<Lazy<UExport>>(exportCount) as Array<Lazy<UExport>>).toMutableList()

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
                        val index = export.classIndex
                        if (index.isNull()) {
                            throw ParserException("Could not find class name for $objectName")
                        }
                        var resolvedClassIndex = resolveObjectIndex(index)
                        val classIndexObject = resolvedClassIndex
                        while (resolvedClassIndex?.getSuper()?.also { resolvedClassIndex = it } != null); // TODO band aid until BlueprintGeneratedClass support is added
                        val obj = constructExport((resolvedClassIndex?.getName() ?: NAME_None).text)
                        obj.export2 = export
                        obj.exportType = (classIndexObject?.getName() ?: NAME_None).text
                        obj.name = objectName.text
                        obj.owner = this
                        val sb = StringBuilder(".").append(objectName.text)
                        var outerObject = resolveObjectIndex(export.outerIndex)
                        while (outerObject != null) {
                            sb.insert(0, '.').append(outerObject.getName().text)
                            outerObject = outerObject.getOuter()
                        }
                        sb.insert(0, diskPackageName.text)
                        obj.pathName = sb.toString()
                        obj.flags = export.objectFlags

                        // Serialize
                        val Ar = FExportArchive(ByteBuffer.wrap(uasset), this)
                        Ar.useUnversionedPropertySerialization = (packageFlags and EPackageFlags.PKG_UnversionedProperties.value) != 0
                        Ar.uassetSize = summary.cookedHeaderSize.toInt() - allExportDataOffset
                        Ar.seek(localExportDataOffset)
                        try {
                            obj.deserialize(Ar, Ar.pos() + export.cookedSerialSize.toInt())
                        } catch (e: Throwable) {
                            if (e is MissingSchemaException && !GSuppressMissingSchemaErrors) {
                                LOG_STREAMING.warn(e.message)
                            } else if (e is UnknownPropertyException && obj.javaClass.simpleName.unprefix() in GSuppressUnknownPropertyExceptionClasses) {
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
            index.isScriptImport() -> return globalPackageStore.importStore.scriptObjectEntriesMap[index]?.let { ResolvedScriptObject(it, this@IoPackage) }
            index.isPackageImport() -> for (pkg in importedPackages.value) {
                pkg.exportMap.forEachIndexed { exportIndex, exportMapEntry ->
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
    }

    class ResolvedExportObject(exportIndex: Int, pkg: IoPackage) : ResolvedObject(pkg) {
        val exportMapEntry = pkg.exportMap[exportIndex]
        val exportObject = pkg.exportsLazy[exportIndex]
        override fun getName() = pkg.nameMap.getName(exportMapEntry.objectName)
        override fun getOuter() = pkg.resolveObjectIndex(exportMapEntry.outerIndex)
        override fun getSuper() = pkg.resolveObjectIndex(exportMapEntry.superIndex)
    }

    class ResolvedScriptObject(val scriptImport: FScriptObjectEntry, pkg: IoPackage) : ResolvedObject(pkg) {
        override fun getName() = scriptImport.objectName.toName()
        override fun getOuter() = pkg.resolveObjectIndex(scriptImport.outerIndex)
    }

    override fun loadObjectGeneric(index: FPackageIndex?) = when {
        index == null || index.isNull() -> null
        index.isExport() -> exportsLazy.getOrNull(index.toExport())?.value
        else -> importMap.getOrNull(index.toImport())?.let { (resolveObjectIndex(it, false) as? ResolvedExportObject)?.exportObject?.value }
    }

    override fun findExport(objectName: String, className: String?): UExport? {
        val exportIndex = exportMap.indexOfFirst {
            nameMap.getName(it.objectName).text.equals(objectName, true) && (className == null || resolveObjectIndex(it.classIndex)?.getName()?.text == className)
        }
        return if (exportIndex != -1) exportsLazy[exportIndex].value else null
    }

    fun getImportObject(index: FPackageIndex?) = if (index != null && index.isImport()) importMap[index.toImport()] else null

    fun getExportObject(index: FPackageIndex?) = if (index != null && index.isExport()) exportMap[index.toExport()] else null

    fun dumpHeaderToJson(): JsonObject {
        val gson = gson.newBuilder().registerTypeAdapter(jsonSerializer<FMappedName> { JsonPrimitive(nameMap.tryGetName(it.src)?.text) }).create()
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