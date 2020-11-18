package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.IoPackage
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.asyncloading2.EEventLoadNode2.*
import me.fungames.jfortniteparse.ue4.asyncloading2.FAsyncPackage2.EExternalReadAction.ExternalReadAction_Poll
import me.fungames.jfortniteparse.ue4.io.FIoRequest
import me.fungames.jfortniteparse.ue4.objects.uobject.EObjectFlags.*
import me.fungames.jfortniteparse.ue4.objects.uobject.EPackageFlags
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.util.INDEX_NONE
import me.fungames.jfortniteparse.util.get
import org.slf4j.event.Level
import java.nio.ByteBuffer

/**
 * Structure containing intermediate data required for async loading of all exports of a package.
 */
class FAsyncPackage2 {
    /** Basic information associated with this package  */
    internal val desc: FAsyncPackageDesc2
    private val data: FAsyncPackageData
    /** Cached async loading thread object this package was created by */
    internal val asyncLoadingThread: FAsyncLoadingThread2
    /** Package which is going to have its exports and imports loaded */
    private var linkerRoot: Package? = null
    /** Current index into externalReadDependencies array used to spread waiting for external reads over several frames */
    private var externalReadIndex = 0
    /** Current index into deferredClusterObjects array used to spread routing createClusters over several frames */
    private var deferredClusterIndex = 0
    private var asyncPackageLoadingState = EAsyncPackageLoadingState2.NewPackage
    /** True if our load has failed  */
    internal var bLoadHasFailed = false
    /** True if this package was created by this async package */
    private var bCreatedLinkerRoot = false

    /** List of all request handles */
    private val requestIDs = mutableListOf<Int>()
    private val externalReadDependencies = mutableListOf<FExternalReadCallback>()
    /** Call backs called when we finished loading this package */
    private val completionCallbacks = mutableListOf<FCompletionCallback>()

    internal lateinit var ioRequest: FIoRequest
    internal lateinit var ioBuffer: ByteArray
    private var ioBufferOff = 0
    private var currentExportDataPtr = 0uL
    private var allExportDataPtr = 0uL
    internal var exportBundlesSize = 0uL
    private var cookedHeaderSize = 0u
    internal var loadOrder = 0u

    private lateinit var exportMap: Array<FExportMapEntry>
    private val importStore: FPackageImportStore
    private val nameMap = FNameMap()
    var provider: FileProvider? = null

    constructor(
        desc: FAsyncPackageDesc2,
        data: FAsyncPackageData,
        asyncLoadingThread: FAsyncLoadingThread2,
        eventSpecs: List<FAsyncLoadEventSpec>
    ) {
        this.desc = desc
        this.data = data
        this.asyncLoadingThread = asyncLoadingThread
        //this.graphAllocator = graphAllocator
        importStore = FPackageImportStore(asyncLoadingThread.globalPackageStore, desc)
        addRequestID(desc.requestID)

        exportBundlesSize = desc.storeEntry!!.exportBundlesSize
        loadOrder = desc.storeEntry!!.loadOrder

        createNodes(eventSpecs)
    }

    fun addCompletionCallback(callback: FCompletionCallback) {
        // This is to ensure that there is no one trying to subscribe to a already loaded package
        //check(!bLoadHasFinished && !bLoadHasFailed)
        completionCallbacks.add(callback)
    }

    /** Adds new request ID to the existing package */
    fun addRequestID(id: Int) {
        if (id > 0) {
            if (desc.requestID == INDEX_NONE) {
                // For debug readability
                desc.requestID = id
            }
            requestIDs.add(id)
            asyncLoadingThread.addPendingRequest(id)
        }
    }

    fun getLoadedPackage() = if (!bLoadHasFailed) linkerRoot else null

    fun importPackagesRecursive() {
        if (asyncPackageLoadingState >= EAsyncPackageLoadingState2.ImportPackages) {
            return
        }
        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.NewPackage)

        val importedPackageCount = desc.storeEntry!!.importedPackages.size
        if (importedPackageCount <= 0) {
            asyncPackageLoadingState = EAsyncPackageLoadingState2.ImportPackagesDone
            return
        } else {
            asyncPackageLoadingState = EAsyncPackageLoadingState2.ImportPackages
        }

        var importedPackageIndex = 0

        val globalPackageStore = asyncLoadingThread.globalPackageStore
        /*for (importedPackageId in desc.storeEntry!!.importedPackages) {
            val packageRef = globalPackageStore.loadedPackageStore.getOrPut(importedPackageId) { FLoadedPackageRef() }
            if (packageRef.areAllPublicExportsLoaded()) {
                continue
            }

            val importedPackageEntry = globalPackageStore.findStoreEntry(importedPackageId)

            if (importedPackageEntry == null) {
                asyncPackageLog(Level.WARN, desc, "ImportPackages: SkipPackage",
                    "Skipping non mounted imported package with id '0x%016X'".format(importedPackageId.value().toLong()))
                packageRef.setIsMissingPackage()
                continue
            } else if (packageRef.isMissingPackage()) {
                packageRef.clearIsMissingPackage()
            }

            val packageDesc = FAsyncPackageDesc2(INDEX_NONE, importedPackageId, importedPackageEntry)
            val bInserted = BooleanRef()
            val importedPackage = asyncLoadingThread.findOrInsertPackage(packageDesc, bInserted)

            check(importedPackage != null) { "Failed to find or insert imported package with id '0x%016X'".format(importedPackageId.value().toLong()) }

            if (bInserted.element) {
                asyncPackageLog(Level.DEBUG, packageDesc, "ImportPackages: AddPackage",
                    "Start loading imported package.")
            } else {
                asyncPackageLogVerbose(Level.TRACE, packageDesc, "ImportPackages: UpdatePackage",
                    "Imported package is already being loaded.")
            }
            //importedPackage.addRef()
            check(importedPackageIndex == data.importedAsyncPackages.size)
            data.importedAsyncPackages.add(importedPackage)
            ++importedPackageIndex
            if (bInserted.element) {
                importedPackage.importPackagesRecursive()
                importedPackage.startLoading()
            }
        }*/
        asyncPackageLogVerbose(Level.TRACE, desc, "ImportPackages: ImportsDone",
            "All imported packages are now being loaded.")

        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.ImportPackages)
        asyncPackageLoadingState = EAsyncPackageLoadingState2.ImportPackagesDone
    }

    fun startLoading() {
        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.ImportPackagesDone)

        //loadStartTime = ManagementFactory.getRuntimeMXBean().uptime // this is not compatible with Android

        asyncLoadingThread.addBundleIoRequest(this)
        asyncPackageLoadingState = EAsyncPackageLoadingState2.WaitingForIo
    }

    // region Event driven loader specific stuff
    fun eventProcessExportBundle(exportBundleIndex: Int): EAsyncPackageState {
        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.ProcessExportBundles)

        val filterExport = { filterFlags: UByte /*EExportFilterFlags*/ -> false }

        check(exportBundleIndex < data.exportBundleCount)

        if (!bLoadHasFailed) {
            val allExportDataSize = ioBuffer.size.toULong() - (allExportDataPtr - ioBufferOff.toULong())
            val Ar = FExportArchive(
                data = ByteBuffer.wrap(ioBuffer, currentExportDataPtr.toInt(), allExportDataSize.toInt()),
                packageDesc = desc,
                nameMap = nameMap,
                importStore = importStore,
                exports = data.exports,
                exportMap = exportMap,
                externalReadDependencies = externalReadDependencies
            ).also {
                it.useUnversionedPropertySerialization = (linkerRoot!!.packageFlags and EPackageFlags.PKG_UnversionedProperties.value) != 0
                it.owner = linkerRoot!!
                it.uassetSize = (cookedHeaderSize - allExportDataPtr).toInt()

                // FExportArchive special fields
                it.cookedHeaderSize = cookedHeaderSize
            }
            val exportBundle = data.exportBundleHeaders[exportBundleIndex]
            for (i in 0u until exportBundle.entryCount) {
                if (FAsyncLoadingThreadState2.get()!!.isTimeLimitExceeded("Event_ProcessExportBundle")) {
                    return EAsyncPackageState.TimeOut
                }
                val bundleEntry = data.exportBundleEntries[exportBundle.firstEntryIndex + i]
                val exportMapEntry = exportMap[bundleEntry.localExportIndex]
                val export = data.exports[bundleEntry.localExportIndex]
                export.bFiltered = filterExport(exportMapEntry.filterFlags)

                if (bundleEntry.commandType == FExportBundleEntry.EExportCommandType.ExportCommandType_Create) {
                    eventDrivenCreateExport(bundleEntry.localExportIndex)
                } else {
                    check(bundleEntry.commandType == FExportBundleEntry.EExportCommandType.ExportCommandType_Serialize)

                    val cookedSerialSize = exportMapEntry.cookedSerialSize
                    val obj = export.exportObject

                    check(currentExportDataPtr + cookedSerialSize <= (ioBufferOff + ioBuffer.size).toULong())

                    val pos = Ar.pos()
                    check(cookedSerialSize <= (Ar.size() - pos).toULong()) {
                        "Package %s: Expected read size: %d - Remaining archive size: %d"
                            .format(desc.diskPackageName.toString(), cookedSerialSize, Ar.size() - pos)
                    }

                    val bSerialized = eventDrivenSerializeExport(bundleEntry.localExportIndex, Ar)
                    if (!bSerialized) {
                        Ar.skip(cookedSerialSize.toLong())
                    }
                    if (cookedSerialSize != (Ar.pos() - pos).toULong()) {
                        LOG_STREAMING.warn("Package %s: Expected read size: %d - Actual read size: %d"
                            .format(desc.diskPackageName.toString(), cookedSerialSize.toLong(), Ar.pos() - pos))
                        Ar.seek(pos + cookedSerialSize.toInt())
                    }

                    //Ar.exportBufferEnd()

                    check((obj != null && !obj.hasAnyFlags(RF_NeedLoad.value)) || export.bFiltered || export.bExportLoadFailed)

                    currentExportDataPtr += cookedSerialSize
                }
            }
        }

        if (exportBundleIndex + 1 < data.exportBundleCount) {
            getExportBundleNode(ExportBundle_Process, exportBundleIndex + 1).releaseBarrier()
        } else {
            //importStore.importMap.clear()
            ioBuffer = ByteArray(0)

            if (externalReadDependencies.isEmpty()) {
                check(asyncPackageLoadingState == EAsyncPackageLoadingState2.ProcessExportBundles)
                asyncPackageLoadingState = EAsyncPackageLoadingState2.ExportsDone
                getPackageNode(Package_ExportsSerialized).releaseBarrier()
            } else {
                check(asyncPackageLoadingState == EAsyncPackageLoadingState2.ProcessExportBundles)
                asyncPackageLoadingState = EAsyncPackageLoadingState2.WaitingForExternalReads
                asyncLoadingThread.externalReadQueue.push(this)
            }
        }

        if (exportBundleIndex == 0) {
            asyncLoadingThread.bundleIoRequestCompleted(this)
        }

        return EAsyncPackageState.Complete
    }

    fun eventProcessPackageSummary(unused: Int): EAsyncPackageState {
        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.WaitingForIo)
        asyncPackageLoadingState = EAsyncPackageLoadingState2.ProcessPackageSummary

        if (bLoadHasFailed) {
            if (desc.canBeImported()) {
                val packageRef = importStore.globalPackageStore.loadedPackageStore[desc.diskPackageId]!!
                packageRef.setHasFailed()
            }
        } else {
            val packageSummaryData = ioBufferOff
            val packageSummaryAr = FByteArchive(ioBuffer).apply { seek(packageSummaryData) }
            val packageSummary = FPackageSummary(packageSummaryAr)
            val graphData = packageSummaryData + packageSummary.graphDataOffset
            val packageSummarySize = graphData + packageSummary.graphDataSize - packageSummaryData

            if (packageSummary.nameMapNamesSize > 0) {
                val nameMapNamesData = FByteArchive(ByteBuffer.wrap(ioBuffer, packageSummaryData + packageSummary.nameMapNamesOffset, packageSummary.nameMapNamesSize))
                val nameMapHashesData = FByteArchive(ByteBuffer.wrap(ioBuffer, packageSummaryData + packageSummary.nameMapHashesOffset, packageSummary.nameMapHashesSize))
                nameMap.load(nameMapNamesData, nameMapHashesData, FMappedName.EType.Package)
            }

            val packageName = nameMap.getName(packageSummary.name)
            if (packageSummary.sourceName != packageSummary.name) {
                val sourcePackageName = nameMap.getName(packageSummary.sourceName)
                desc.setDiskPackageName(packageName, sourcePackageName)
            } else {
                desc.setDiskPackageName(packageName)
            }

            cookedHeaderSize = packageSummary.cookedHeaderSize
            packageSummaryAr.seek(packageSummaryData + packageSummary.importMapOffset)
            for (i in 0 until (packageSummary.exportMapOffset - packageSummary.importMapOffset) / 8 /*sizeof(FPackageObjectIndex)*/) {
                importStore.importMap.add(FPackageObjectIndex(packageSummaryAr))
            }
            packageSummaryAr.seek(packageSummaryData + packageSummary.exportMapOffset)
            exportMap = Array(data.exportCount) {
                FExportMapEntry(packageSummaryAr).apply { data.exports[it].exportMapEntry = this }
            }

            packageSummaryAr.seek(packageSummaryData + packageSummary.exportBundlesOffset)
            data.exportBundleHeaders = Array(data.exportBundleCount) { FExportBundleHeader(packageSummaryAr) }
            data.exportBundleEntries = Array(data.exportCount * FExportBundleEntry.EExportCommandType.ExportCommandType_Count.ordinal) { FExportBundleEntry(packageSummaryAr) }

            createUPackage(packageSummary)
            packageSummaryAr.seek(graphData)
            setupSerializedArcs(packageSummaryAr)

            allExportDataPtr = (packageSummaryData + packageSummarySize).toULong()
            currentExportDataPtr = allExportDataPtr
        }

        if (GIsInitialLoad) {
            setupScriptDependencies()
        }
        getExportBundleNode(ExportBundle_Process, 0).releaseBarrier()

        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.ProcessPackageSummary)
        asyncPackageLoadingState = EAsyncPackageLoadingState2.ProcessExportBundles
        return EAsyncPackageState.Complete
    }

    fun eventExportsDone(unused: Int): EAsyncPackageState {
        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.ExportsDone)

        if (!bLoadHasFailed && desc.canBeImported()) {
            val packageRef = asyncLoadingThread.globalPackageStore.loadedPackageStore.getOrPut(desc.diskPackageId) { FLoadedPackageRef() }
            packageRef.setAllPublicExportsLoaded()
        }

        asyncPackageLoadingState = EAsyncPackageLoadingState2.PostLoad
        getExportBundleNode(ExportBundle_PostLoad, 0).releaseBarrier()
        return EAsyncPackageState.Complete
    }

    fun eventPostLoadExportBundle(exportBundleIndex: Int): EAsyncPackageState {
        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.PostLoad)
        check(externalReadDependencies.isEmpty())
        check(exportBundleIndex < data.exportBundleCount)

        if (exportBundleIndex + 1 < data.exportBundleCount) {
            getExportBundleNode(ExportBundle_PostLoad, exportBundleIndex + 1).releaseBarrier()
        } else {
            if (linkerRoot != null && !bLoadHasFailed) {
                asyncPackageLog(Level.DEBUG, desc, "AsyncThread: FullyLoaded",
                    "Async loading of package is done, and UPackage is marked as fully loaded.")
                // mimic old loader behavior for now, but this is more correctly also done in FinishUPackage
                // called from ProcessLoadedPackagesFromGameThread just before completion callbacks
                //linkerRoot.markAsFullyLoaded()
            }

            check(asyncPackageLoadingState == EAsyncPackageLoadingState2.PostLoad)
            asyncPackageLoadingState = EAsyncPackageLoadingState2.DeferredPostLoad
            getExportBundleNode(ExportBundle_DeferredPostLoad, 0).releaseBarrier()
        }

        return EAsyncPackageState.Complete
    }

    fun eventDeferredPostLoadExportBundle(exportBundleIndex: Int): EAsyncPackageState {
        if (exportBundleIndex + 1 < data.exportBundleCount) {
            getExportBundleNode(ExportBundle_DeferredPostLoad, exportBundleIndex + 1).releaseBarrier()
        } else {
            check(asyncPackageLoadingState == EAsyncPackageLoadingState2.DeferredPostLoad)
            asyncPackageLoadingState = EAsyncPackageLoadingState2.DeferredPostLoadDone
            //asyncLoadingThread.loadedPackagesToProcess.add(this)
            // region Callback code from FAsyncLoadingThread2::ProcessLoadedPackagesFromGameThread
            // Call external callbacks
            val loadingResult = if (bLoadHasFailed) EAsyncLoadingResult.Failed else EAsyncLoadingResult.Succeeded
            callCompletionCallbacks(loadingResult)
            // We don't need the package anymore
            //check(asyncPackageLoadingState == EAsyncPackageLoadingState2.Finalize)
            //if (bHasClusterObjects) {
            //    asyncPackageLoadingState = EAsyncPackageLoadingState2.CreateClusters
            //} else {
            asyncPackageLoadingState = EAsyncPackageLoadingState2.Complete
            //}
            // endregion
        }

        return EAsyncPackageState.Complete
    }

    fun eventDrivenCreateExport(localExportIndex: Int) {
        val export = exportMap[localExportIndex]
        val exportObject = data.exports[localExportIndex]
        var obj = exportObject.exportObject
        check(obj == null)

        val objectName = nameMap.getName(export.objectName)

        if (exportObject.bFiltered || exportObject.bExportLoadFailed) {
            if (exportObject.bExportLoadFailed) {
                asyncPackageLog(Level.WARN, desc, "CreateExport", "Skipped failed export $objectName")
            } else {
                asyncPackageLogVerbose(Level.DEBUG, desc, "CreateExport", "Skipped filtered export $objectName")
            }
            return
        }

        var bIsCompletelyLoaded = false
        /*val loadClass = if (export.classIndex.isNull()) null UClass() else castEventDrivenIndexToObject<UClass>(export.classIndex, true)
        val thisParent = if (export.outerIndex.isNull()) null linkerRoot else eventDrivenIndexToObject(export.outerIndex, false)

        if (loadClass == null) {
            asyncPackageLog(Level.ERROR, desc, "CreateExport", "Could not find class object for $objectName")
            exportObject.bExportLoadFailed = true
            return
        }
        if (thisParent == null) {
            asyncPackageLog(Level.ERROR, desc, "CreateExport", "Could not find outer object for $objectName")
            exportObject.bExportLoadFailed = true
            return
        }*/
        //check(thisParent !is UObjectRedirector)
        if (!export.superIndex.isNull()) {
            exportObject.superObject = eventDrivenIndexToObject(export.superIndex, false)
            if (exportObject.superObject == null) {
                asyncPackageLog(Level.ERROR, desc, "CreateExport", "Could not find SuperStruct object for $objectName")
                exportObject.bExportLoadFailed = true
                return
            }
        }
        // Find the Archetype object for the one we are loading.
        check(!export.templateIndex.isNull())
        exportObject.templateObject = eventDrivenIndexToObject(export.templateIndex, true)
        if (exportObject.templateObject == null) {
            asyncPackageLog(Level.ERROR, desc, "CreateExport", "Could not find template object for $objectName")
            exportObject.bExportLoadFailed = true
            return
        }

        // region Own implementation of object creation code
        val trigger = when {
            export.classIndex.isExport() -> export.superIndex
            export.classIndex.isImport() -> export.classIndex
            else -> {
                asyncPackageLog(Level.ERROR, desc, "CreateExport", "Could not find class name for $objectName")
                exportObject.bExportLoadFailed = true
                return
            }
        }
        obj = importStore.findOrGetImportObject(trigger)!!.apply {
            name = objectName.toString()
            owner = linkerRoot
            pathName = desc.diskPackageName.toString() + '.' + name
            flags = flags or export.objectFlags.toInt()
        }
        exportObject.exportObject = obj
        // endregion

        // If this object was allocated but never loaded (components created by a constructor, CDOs, etc) make sure it gets loaded
        // Do this for all subobjects created in the native constructor.
        val objectFlags = obj.flags
        bIsCompletelyLoaded = (objectFlags and RF_LoadCompleted.value) != 0
        if (!bIsCompletelyLoaded) {
            check((objectFlags and (RF_NeedLoad.value or RF_WasLoaded.value)) == 0) // If export exist but is not completed, it is expected to have been created from a native constructor and not from EventDrivenCreateExport, but who knows...?
            if ((objectFlags and RF_ClassDefaultObject.value) != 0) {
                // never call PostLoadSubobjects on class default objects, this matches the behavior of the old linker where
                // StaticAllocateObject prevents setting of RF_NeedPostLoad and RF_NeedPostLoadSubobjects, but FLinkerLoad::Preload
                // assigns RF_NeedPostLoad for blueprint CDOs:
                obj.flags = obj.flags or (RF_NeedLoad.value or RF_NeedPostLoad.value or RF_WasLoaded.value)
            } else {
                obj.flags = obj.flags or (RF_NeedLoad.value or RF_NeedPostLoad.value or RF_NeedPostLoadSubobjects.value or RF_WasLoaded.value)
            }
        }

        check(obj != null)

        if (desc.canBeImported() && !export.globalImportIndex.isNull()) {
            check(obj.hasAnyFlags(RF_Public.value))
            importStore.storeGlobalObject(desc.diskPackageId, export.globalImportIndex, obj)

            asyncPackageLogVerbose(Level.TRACE, desc, "CreateExport",
                "Created public export %s. Tracked as 0x%016X".format(obj.pathName, export.globalImportIndex.value().toLong()))
        } else {
            asyncPackageLogVerbose(Level.TRACE, desc, "CreateExport", "Created %s export %s. Not tracked.".format(
                if (obj.hasAnyFlags(RF_Public.value)) "public" else "private", obj.pathName))
        }
    }

    fun eventDrivenSerializeExport(localExportIndex: Int, Ar: FExportArchive): Boolean {
        val export = exportMap[localExportIndex]
        val exportObject = data.exports[localExportIndex]
        val obj = exportObject.exportObject
        check(obj != null || (exportObject.bFiltered || exportObject.bExportLoadFailed))

        if ((exportObject.bFiltered || exportObject.bExportLoadFailed) || !(obj != null && obj.hasAnyFlags(RF_NeedLoad.value))) {
            if (exportObject.bExportLoadFailed) {
                asyncPackageLog(Level.WARN, desc, "SerializeExport",
                    "Skipped failed export " + nameMap.getName(export.objectName))
            } else if (exportObject.bFiltered) {
                asyncPackageLogVerbose(Level.DEBUG, desc, "SerializeExport",
                    "Skipped filtered export " + nameMap.getName(export.objectName))
            } else {
                asyncPackageLogVerbose(Level.TRACE, desc, "SerializeExport",
                    "Skipped already serialized export " + nameMap.getName(export.objectName))
            }
            return false
        }

        // cache archetype
        // prevents GetArchetype from hitting the expensive GetArchetypeFromRequiredInfoImpl
        check(exportObject.templateObject != null)
        //cacheArchetypeForObject(obj, exportObject.templateObject)

        obj.clearFlags(RF_NeedLoad.value)

        //val loadContext = getSerializeContext()
        //val prevSerializedObject = loadContext.serializedObject
        //loadContext.serializedObject = obj

        //Ar.templateForGetArchetypeFromLoader = exportObject.templateObject

        if (obj.hasAnyFlags(RF_ClassDefaultObject.value)) {
            //obj.clazz.serializeDefaultObject(obj, Ar)
        } else {
            try {
                obj.deserialize(Ar, 0)
            } catch (e: Throwable) {
                LOG_STREAMING.warn("Failed to deserialize ${obj.pathName}", e)
                bLoadHasFailed = true
            }
        }
        //Ar.templateForGetArchetypeFromLoader = null

        obj.flags = obj.flags or RF_LoadCompleted.value
        //loadContext.serializedObject = prevSerializedObject

        asyncPackageLogVerbose(Level.TRACE, desc, "SerializeExport", "Serialized export ${obj.pathName}")

        return true
    }

    fun eventDrivenIndexToObject(index: FPackageObjectIndex, bCheckSerialized: Boolean): UObject? {
        var result: UObject? = null
        if (index.isNull()) {
            return result
        }
        if (index.isExport()) {
            result = data.exports[index.toExport()].exportObject
        } else if (index.isImport()) {
            result = importStore.findOrGetImportObject(index)
            if (result == null) {
                LOG_STREAMING.warn("Missing %s import 0x%016X for package %s".format(
                    if (index.isScriptImport()) "script" else "package",
                    index.value().toLong(),
                    desc.diskPackageName.toString()
                ))
            }
        }
        return result
    }

    fun <T> castEventDrivenIndexToObject(index: FPackageObjectIndex, bCheckSerialized: Boolean): T? {
        val result = eventDrivenIndexToObject(index, bCheckSerialized) ?: return null
        return result as T
    }

    fun getPackageNode(phase: EEventLoadNode2): FEventLoadNode2 {
        check(phase < Package_NumPhases)
        return data.packageNodes[phase.value]!!
    }

    fun getExportBundleNode(phase: EEventLoadNode2, exportBundleIndex: Int = 0): FEventLoadNode2 {
        check(exportBundleIndex < data.exportBundleCount)
        val exportBundleNodeIndex = exportBundleIndex * ExportBundle_NumPhases.value + phase.value
        return data.exportBundleNodes[exportBundleNodeIndex]!!
    }
    // endregion

    fun callCompletionCallbacks(loadingResult: EAsyncLoadingResult) {
        val loadedPackage = if (!bLoadHasFailed) linkerRoot else null
        for (completionCallback in completionCallbacks) {
            completionCallback(desc.getUPackageName(), loadedPackage, loadingResult)
        }
        completionCallbacks.clear()
    }

    private fun createNodes(eventSpecs: List<FAsyncLoadEventSpec>) {
        val barrierCount = 1
        for (phase in 0 until Package_NumPhases.value) {
            data.packageNodes[phase] = FEventLoadNode2(eventSpecs[phase], this, -1, barrierCount)
        }

        for (exportBundleIndex in 0 until data.exportBundleCount) {
            val nodeIndex = ExportBundle_NumPhases.value * exportBundleIndex
            for (phase in 0 until ExportBundle_NumPhases.value) {
                data.exportBundleNodes[nodeIndex + phase] = FEventLoadNode2(eventSpecs[Package_NumPhases.value + phase], this, exportBundleIndex, barrierCount)
            }
        }
    }

    private fun setupSerializedArcs(graphArchive: FArchive) {
        val importedPackagesCount = graphArchive.readInt32()
        for (importedPackageIndex in 0 until importedPackagesCount) {
            val importedPackageId = FPackageId(graphArchive)
            val externalArcCount = graphArchive.readInt32()

            val importedPackage = asyncLoadingThread.getAsyncPackage(importedPackageId)
            for (externalArcIndex in 0 until externalArcCount) {
                var fromExportBundleIndex = graphArchive.readInt32()
                val toExportBundleIndex = graphArchive.readInt32()
                if (importedPackage != null) {
                    fromExportBundleIndex = if (fromExportBundleIndex == UInt.MAX_VALUE.toInt())
                        importedPackage.data.exportBundleCount - 1
                    else
                        fromExportBundleIndex

                    check(fromExportBundleIndex < importedPackage.data.exportBundleCount)
                    check(toExportBundleIndex < data.exportBundleCount)
                    val fromNodeIndexBase = fromExportBundleIndex * ExportBundle_NumPhases.value
                    val toNodeIndexBase = toExportBundleIndex * ExportBundle_NumPhases.value
                    for (phase in 0 until ExportBundle_NumPhases.value) {
                        val toNodeIndex = toNodeIndexBase + phase
                        val fromNodeIndex = fromNodeIndexBase + phase
                        data.exportBundleNodes[toNodeIndex]!!.dependsOn(importedPackage.data.exportBundleNodes[fromNodeIndex]!!)
                    }
                }
            }
        }
    }

    private fun setupScriptDependencies() {}

    /**
     * Create UPackage
     *
     * @return true
     */
    private fun createUPackage(packageSummary: FPackageSummary) {
        check(linkerRoot == null)

        // temp packages are never stored or found in loaded package store
        var packageRef: FLoadedPackageRef? = null

        // Try to find existing package or create it if not already present.
        val existingPackage: Package? = null
        if (desc.canBeImported()) {
            packageRef = importStore.globalPackageStore.loadedPackageStore[desc.diskPackageId]
            linkerRoot = packageRef!!.`package`
        }
        if (linkerRoot == null) {
            if (existingPackage != null) {
                linkerRoot = existingPackage
            } else {
                linkerRoot = IoPackage(importStore, nameMap, data.exports, desc.diskPackageName.toString(), provider)
                bCreatedLinkerRoot = true
            }
            linkerRoot!!.apply {
                flags = flags or (RF_Public.value or RF_WasLoaded.value)
                //fileName = desc.diskPackageName
                //canBeImportedFlag = desc.canBeImported()
                //packageId = desc.diskPackageId
                packageFlags = packageSummary.packageFlags.toInt()
                //linkerPackageVersion = GPackageFileUE4Version
                //linkerLicenseeVersion = GPackageFileLicenseeUE4Version
                //linkerCustomVersion = packageSummaryVersions // only if (!bCustomVersionIsLatest)
            }
            if (packageRef != null) {
                packageRef.`package` = linkerRoot
            }
        } else linkerRoot!!.apply {
            //check(canBeImported() == desc.canBeImported())
            //check(getPackageId() == desc.diskPackageId)
            check(packageFlags == packageSummary.packageFlags.toInt())
            //check(linkerPackageVersion == GPackageFileUE4Version)
            //check(linkerLicenseeVersion == GPackageFileLicenseeUE4Version)
            check(hasAnyFlags(RF_WasLoaded.value))
        }

        if (bCreatedLinkerRoot) {
            asyncPackageLogVerbose(Level.TRACE, desc, "CreateUPackage: AddPackage",
                "New UPackage created.")
        } else {
            asyncPackageLogVerbose(Level.TRACE, desc, "CreateUPackage: UpdatePackage",
                "Existing UPackage updated.")
        }
    }

    /**
     * Finalizes external dependencies till time limit is exceeded
     *
     * @return Complete if all dependencies are finished, TimeOut otherwise
     */
    fun processExternalReads(action: EExternalReadAction): EAsyncPackageState {
        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.WaitingForExternalReads)
        val waitTime = if (action == ExternalReadAction_Poll) {
            -1.0
        } else { // if (action == ExternalReadAction_Wait)
            0.0
        }

        while (externalReadIndex < externalReadDependencies.size) {
            val readCallback = externalReadDependencies[externalReadIndex]
            if (!readCallback(waitTime)) {
                return EAsyncPackageState.TimeOut
            }
            ++externalReadIndex
        }

        externalReadDependencies.clear()
        asyncPackageLoadingState = EAsyncPackageLoadingState2.ExportsDone
        getPackageNode(Package_ExportsSerialized).releaseBarrier()
        return EAsyncPackageState.Complete
    }

    enum class EExternalReadAction { ExternalReadAction_Poll, ExternalReadAction_Wait }
}