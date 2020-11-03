package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.io.EIoChunkType
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_Medium
import me.fungames.jfortniteparse.ue4.io.FIoChunkId
import me.fungames.jfortniteparse.ue4.io.FIoDispatcher
import me.fungames.jfortniteparse.ue4.io.FIoReadOptions
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.util.await
import me.fungames.jfortniteparse.util.complete
import java.util.concurrent.CompletableFuture

class FAsyncLoadingThread2 {
    private var bLazyInitializedFromLoadPackage = false
    val ioDispatcher = FIoDispatcher.get()
    val globalNameMap = FNameMap()
    val globalPackageStore = FPackageStore(ioDispatcher, globalNameMap)

    fun loadPackage(name: String): ByteArray {
        if (!bLazyInitializedFromLoadPackage) {
            bLazyInitializedFromLoadPackage = true
            lazyInitializeFromLoadPackage()
        }

        var requestId = -1

        val diskPackageId = FPackageId.fromName(FName.dummy(name))
        val evt = CompletableFuture<ByteArray>()
        ioDispatcher.readWithCallback(
            FIoChunkId(diskPackageId.value(), 0u, EIoChunkType.ExportBundleData),
            FIoReadOptions(),
            IoDispatcherPriority_Medium
        ) { evt.complete(it) }
        return evt.await()
    }

    /*private*/ fun lazyInitializeFromLoadPackage() {
        globalNameMap.loadGlobal(ioDispatcher)
        if (GIsInitialLoad) {
            globalPackageStore.setupInitialLoadData()
        }
        globalPackageStore.setupCulture()
//        globalPackageStore.loadContainers(ioDispatcher.mountedContainers)
        ioDispatcher.addOnContainerMountedListener(globalPackageStore)
        bLazyInitializedFromLoadPackage = true
    }

    fun getAsyncPackage(packageId: FPackageId): FAsyncPackage2? {
        TODO("Not yet implemented")
        /*synchronized(asyncPackagesCritical) {
            return asyncPackageLookup[packageId]
        }*/
    }

    fun createAsyncPackage(desc: FAsyncPackageDesc2): FAsyncPackage2 {
        check(desc.storeEntry != null) { "No package store entry for package ${desc.diskPackageName}" }

        val data = FAsyncPackageData()
        data.exportCount = desc.storeEntry!!.exportCount
        data.exportBundleCount = desc.storeEntry!!.exportBundleCount

        val exportBundleNodeCount = data.exportBundleCount * EEventLoadNode2.ExportBundle_NumPhases.value.toInt()
        val importedPackageCount = desc.storeEntry!!.importedPackages.size
        val nodeCount = EEventLoadNode2.Package_NumPhases.ordinal + exportBundleNodeCount

        data.exports = ArrayList(data.exportCount)
        data.importedAsyncPackages = ArrayList(importedPackageCount)
        data.packageNodes = ArrayList(nodeCount)
        data.exportBundleNodes = ArrayList(exportBundleNodeCount)

        //existingAsyncPackagesCounter.increment()
        return FAsyncPackage2(desc, data, this/*, graphAllocator, eventSpecs.data*/)
    }
}