package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.io.FIoDispatcher

@ExperimentalUnsignedTypes
class FAsyncLoadingThread2 {
    val ioDispatcher = FIoDispatcher.get()
    val globalNameMap = FNameMap()
    val globalPackageStore = FPackageStore(ioDispatcher, globalNameMap)

    fun lazyInitializeFromLoadPackage() {
        globalNameMap.loadGlobal(ioDispatcher)
        if (GIsInitialLoad) {
            globalPackageStore.setupInitialLoadData()
        }
        globalPackageStore.setupCulture()
        globalPackageStore.loadContainers(ioDispatcher.mountedContainers)
        ioDispatcher.addOnContainerMountedListener(globalPackageStore)
    }
}