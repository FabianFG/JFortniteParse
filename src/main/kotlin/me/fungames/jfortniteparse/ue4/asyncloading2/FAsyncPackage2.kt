package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.io.FIoRequest
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.util.get
import java.nio.ByteBuffer

/**
 * Structure containing intermediate data required for async loading of all exports of a package.
 */
class FAsyncPackage2 {
    /** Basic information associated with this package  */
    val desc: FAsyncPackageDesc2
    val data: FAsyncPackageData

    /** Cached async loading thread object this package was created by */
    val asyncLoadingThread: FAsyncLoadingThread2
    var asyncPackageLoadingState: EAsyncPackageLoadingState2 = EAsyncPackageLoadingState2.NewPackage

    /** True if our load has failed  */
    var bLoadHasFailed = false

    lateinit var ioRequest: FIoRequest
    lateinit var ioBuffer: ByteArray
    private var exportBundlesSize = 0uL
    private var cookedHeaderSize = 0u
    private var loadOrder = 0u

    private var exportMap: FExportMapEntry? = null
    private val importStore: FPackageImportStore
    private val nameMap = FNameMap()

    constructor(
        desc: FAsyncPackageDesc2,
        data: FAsyncPackageData,
        asyncLoadingThread: FAsyncLoadingThread2,
        //graphAllocator: FAsyncLoadEventGraphAllocator,
        //eventSpecs: FAsyncLoadEventSpec
    ) {
        this.desc = desc
        this.data = data
        this.asyncLoadingThread = asyncLoadingThread
        //this.graphAllocator = graphAllocator
        importStore = FPackageImportStore(asyncLoadingThread.globalPackageStore, desc)
        //addRequestID(desc.requestID)

        exportBundlesSize = desc.storeEntry!!.exportBundlesSize
        loadOrder = desc.storeEntry!!.loadOrder

        //data.exports =
    }

    //fun processExportBundle() {}

    fun processPackageSummary() {
        check(asyncPackageLoadingState == EAsyncPackageLoadingState2.WaitingForIo)
        asyncPackageLoadingState = EAsyncPackageLoadingState2.ProcessPackageSummary

        /*if (bLoadHasFailed) {
            if (desc.canBeImported()) {
                val packageRef = importStore.globalPackageStore.loadedPackageStore.findPackageRef(desc.diskPackageId)!!
                packageRef.setHasFailed()
            }
        } else {*/
        val packageSummaryData = ioBuffer
        val packageSummaryAr = FByteArchive(packageSummaryData)
        val packageSummary = FPackageSummary(packageSummaryAr)

        if (packageSummary.nameMapNamesSize > 0) {
            val nameMapNamesData = FByteArchive(ByteBuffer.wrap(packageSummaryData, packageSummary.nameMapNamesOffset, packageSummary.nameMapNamesSize))
            val nameMapHashesData = FByteArchive(ByteBuffer.wrap(packageSummaryData, packageSummary.nameMapHashesOffset, packageSummary.nameMapHashesSize))
            nameMap.load(nameMapNamesData, nameMapHashesData, FMappedName.EType.Package)
        }

        val packageName = nameMap.getName(packageSummary.name)
        println(packageName)
        if (packageSummary.sourceName != packageSummary.name) {
            val sourcePackageName = nameMap.getName(packageSummary.sourceName)
            desc.setDiskPackageName(packageName, sourcePackageName)
        } else {
            desc.setDiskPackageName(packageName)
        }

        cookedHeaderSize = packageSummary.cookedHeaderSize
        packageSummaryAr.seek(packageSummary.importMapOffset)
        for (i in 0 until (packageSummary.exportMapOffset - packageSummary.importMapOffset) / 8 /*sizeof(FPackageObjectIndex)*/) {
            importStore.importMap.add(FPackageObjectIndex(packageSummaryAr))
        }
        packageSummaryAr.seek(packageSummary.exportMapOffset)
        exportMap = FExportMapEntry(packageSummaryAr)

        packageSummaryAr.seek(packageSummary.exportBundlesOffset)
        data.exportBundleHeaders = MutableList(data.exportBundleCount) { FExportBundleHeader(packageSummaryAr) }
        data.exportBundleEntries = MutableList(data.exportBundleCount) { FExportBundleEntry(packageSummaryAr) }

        //createUPackage(packageSummary)
        packageSummaryAr.seek(packageSummary.graphDataOffset)
        setupSerializedArcs(packageSummaryAr)
        //}
    }

    //fun exportsDone() {}

    //fun postLoadExportBundle() {}

    //fun deferredPostLoadExportBundle() {}

    //private fun createNodes(eventSpecs: FAsyncLoadEventSpec) {}

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
                    val fromNodeIndexBase = fromExportBundleIndex.toUInt() * EEventLoadNode2.ExportBundle_NumPhases.value
                    val toNodeIndexBase = toExportBundleIndex.toUInt() * EEventLoadNode2.ExportBundle_NumPhases.value
                    for (phase in 0u until EEventLoadNode2.ExportBundle_NumPhases.value) {
                        val toNodeIndex = toNodeIndexBase + phase
                        val fromNodeIndex = fromNodeIndexBase + phase
                        data.exportBundleNodes[toNodeIndex].dependsOn(importedPackage.data.exportBundleNodes[fromNodeIndex])
                    }
                }
            }
        }
    }

    //private fun setupScriptDependencies() {}
}