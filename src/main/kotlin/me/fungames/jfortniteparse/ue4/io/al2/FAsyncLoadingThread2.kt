package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_Medium
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
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
}