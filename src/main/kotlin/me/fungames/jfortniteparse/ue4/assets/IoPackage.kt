package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson
import me.fungames.jfortniteparse.GFatalObjectSerializationErrors
import me.fungames.jfortniteparse.LOG_STREAMING
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UEnum
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UStruct
import me.fungames.jfortniteparse.ue4.assets.reader.FExportArchive
import me.fungames.jfortniteparse.ue4.asyncloading2.*
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.uobject.EPackageFlags
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FMappedName
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FNameMap
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE5_BASE
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import me.fungames.jfortniteparse.util.get
import java.nio.ByteBuffer


/**
 * Linker for I/O Store packages
 */
class IoPackage : Package {
    val packageId: FPackageId
    val globalPackageStore: FPackageStore
    val nameMap: FNameMap
    val importMap: Array<FPackageObjectIndex>
    val exportMap: Array<FExportMapEntry>
    val exportBundleHeaders: Array<FExportBundleHeader>
    val exportBundleEntries: Array<FExportBundleEntry>
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
        Ar.game = game.game

        val importedPackageIds: Array<FPackageId>
        val allExportDataOffset: Int

        if (game.game >= GAME_UE5_BASE) {
            val summary = FPackageSummary5(Ar)

            // Name map
            nameMap = FNameMap()
            nameMap.load(Ar, FMappedName.EType.Package)

            val diskPackageName = nameMap.getName(summary.name)
            fileName = diskPackageName.text
            packageFlags = summary.packageFlags.toInt()
            name = fileName

            // Import map
            Ar.seek(summary.importMapOffset)
            val importCount = (summary.exportMapOffset - summary.importMapOffset) / 8
            importMap = Array(importCount) { FPackageObjectIndex(Ar) }

            // Export map
            Ar.seek(summary.exportMapOffset)
            val exportCount = storeEntry.exportCount //(summary.exportBundleEntriesOffset - summary.exportMapOffset) / 72
            exportMap = Array(exportCount) { FExportMapEntry(Ar) }
            exportsLazy = (arrayOfNulls<Lazy<UObject>>(exportCount) as Array<Lazy<UObject>>).toMutableList()

            // Export bundle entries
            Ar.seek(summary.exportBundleEntriesOffset)
            exportBundleEntries = Array(exportCount * 2) { FExportBundleEntry(Ar) }

            // Export bundle headers
            Ar.seek(summary.graphDataOffset)
            exportBundleHeaders = Array(storeEntry.exportBundleCount) { FExportBundleHeader(Ar) }

            allExportDataOffset = summary.headerSize.toInt()
        } else {
            val summary = FPackageSummary(Ar)

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
            val importCount = (summary.exportMapOffset - summary.importMapOffset) / 8
            importMap = Array(importCount) { FPackageObjectIndex(Ar) }

            // Export map
            Ar.seek(summary.exportMapOffset)
            val exportCount = storeEntry.exportCount //(summary.exportBundlesOffset - summary.exportMapOffset) / 72
            exportMap = Array(exportCount) { FExportMapEntry(Ar) }
            exportsLazy = (arrayOfNulls<Lazy<UObject>>(exportCount) as Array<Lazy<UObject>>).toMutableList()

            // Export bundles
            Ar.seek(summary.exportBundlesOffset)
            exportBundleHeaders = Array(storeEntry.exportBundleCount) { FExportBundleHeader(Ar) }
            exportBundleEntries = Array(exportCount * 2) { FExportBundleEntry(Ar) }

            allExportDataOffset = summary.graphDataOffset + summary.graphDataSize
        }

        // Preload dependencies
        importedPackageIds = storeEntry.importedPackages
        importedPackages = lazy { importedPackageIds.map { provider.loadGameFile(it) } }

        // Populate lazy exports
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
                            } else {
                                LOG_STREAMING.debug { "Successfully read ${obj.exportType} at $localExportDataOffset with size ${export.cookedSerialSize}" }
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
                    currentExportDataOffset += export.cookedSerialSize.toInt()
                }
            }
        }
        bulkDataStartOffset = currentExportDataOffset
        //logger.info { "Successfully parsed package : $name" }
    }

    fun resolveObjectIndex(index: FPackageObjectIndex?): ResolvedObject? {
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
        LOG_STREAMING.warn("Missing %s import 0x%016X for package %s".format(
            if (index.isScriptImport()) "script" else "package",
            index.value().toLong(),
            fileName
        ))
        return null
    }

    abstract class ResolvedObject(val pkg: IoPackage) {
        abstract fun getName(): FName
        open fun getOuter(): ResolvedObject? = null
        open fun getClazz(): ResolvedObject? = null
        open fun getSuper(): ResolvedObject? = null
        open fun getObject(): Lazy<UObject?>? = null

        @JvmOverloads
        fun getFullName(stopOuter: ResolvedObject? = null, includeClassPackage: Boolean = false): String {
            val result = StringBuilder(128)
            getFullName(stopOuter, result, includeClassPackage)
            return result.toString()
        }

        fun getFullName(stopOuter: ResolvedObject?, resultString: StringBuilder, includeClassPackage: Boolean = false) {
            if (includeClassPackage) {
                resultString.append(getClazz()?.getPathName())
            } else {
                resultString.append(getClazz()?.getName())
            }
            resultString.append(' ')
            getPathName(stopOuter, resultString)
        }

        @JvmOverloads
        fun getPathName(stopOuter: ResolvedObject? = null): String {
            val result = StringBuilder()
            getPathName(stopOuter, result)
            return result.toString()
        }

        fun getPathName(stopOuter: ResolvedObject?, resultString: StringBuilder) {
            if (this != stopOuter) {
                val objOuter = getOuter()
                if (objOuter != null && objOuter != stopOuter) {
                    objOuter.getPathName(stopOuter, resultString)

                    // SUBOBJECT_DELIMITER_CHAR is used to indicate that this object's outer is not a UPackage
                    if (objOuter.getOuter()?.getClazz()?.getName()?.text == "Package") {
                        resultString.append(':')
                    } else {
                        resultString.append('.')
                    }
                }
                resultString.append(getName())
            } else {
                resultString.append("None")
            }
        }
    }

    class ResolvedExportObject(exportIndex: Int, pkg: IoPackage) : ResolvedObject(pkg) {
        val exportMapEntry = pkg.exportMap[exportIndex]
        val exportObject = pkg.exportsLazy[exportIndex]
        override fun getName() = pkg.nameMap.getName(exportMapEntry.objectName)
        override fun getOuter() = pkg.resolveObjectIndex(exportMapEntry.outerIndex) ?: ResolvedLoadedObject(pkg)
        override fun getClazz() = pkg.resolveObjectIndex(exportMapEntry.classIndex)
        override fun getSuper() = pkg.resolveObjectIndex(exportMapEntry.superIndex)
        override fun getObject() = exportObject
    }

    class ResolvedScriptObject(val scriptImport: FScriptObjectEntry, pkg: IoPackage) : ResolvedObject(pkg) {
        override fun getName() = scriptImport.objectName.toName()
        override fun getOuter() = pkg?.resolveObjectIndex(scriptImport.outerIndex)
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

    override fun <T : UObject> findObject(index: FPackageIndex?) = when {
        index == null || index.isNull() -> null
        index.isExport() -> exportsLazy.getOrNull(index.toExport())
        else -> importMap.getOrNull(index.toImport())?.let { resolveObjectIndex(it) }?.getObject()
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
        else -> importMap.getOrNull(index.toImport())?.let { resolveObjectIndex(it) }
    }

    /*fun dumpHeaderToJson(): JsonObject {
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
    }*/
}