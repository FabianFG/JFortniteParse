package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_High
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.util.await
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

val LOG_STREAMING: Logger = LoggerFactory.getLogger("Streaming")

class FPackageStore(
    val ioDispatcher: FIoDispatcher,
    val globalNameMap: FNameMap) : FOnContainerMountedListener {
    val loadedContainers = mutableMapOf<FIoContainerId, FLoadedContainer>()

    val currentCultureNames = mutableListOf<String>()

    val packageNameMapsCritical = Object()

    val storeEntriesMap = mutableMapOf<FPackageId, FPackageStoreEntry>()
    val redirectsPackageMap = mutableMapOf<FPackageId, FPackageId>()
    var nextCustomPackageIndex = 0

    val importStore = FGlobalImportStore()

    ///**
    // * Packages in active loading or completely loaded packages, with desc.diskPackageName as key.
    // * Does not track temp packages with custom UPackage names, since they are never imported by other packages.
    // */
    //val loadedPackageStore = mutableMapOf<FPackageId, FLoadedPackageRef>() /*FLoadedPackageStore*/
    var scriptArcsCount = 0

    fun setupCulture() {
        currentCultureNames.clear()
        currentCultureNames.add(Locale.getDefault().toString().replace('_', '-'))
    }

    fun setupInitialLoadData() {
        val initialLoadEvent = CompletableFuture<Void>()

        val ioBatch = ioDispatcher.newBatch()
        val ioRequest = ioBatch.read(FIoChunkId(0u, 0u, EIoChunkType.LoaderInitialLoadMeta), FIoReadOptions(), IoDispatcherPriority_High.value)
        ioBatch.issueAndTriggerEvent(initialLoadEvent)

        initialLoadEvent.await()
        val initialLoadArchive = FByteArchive(ByteBuffer.wrap(ioRequest.result.getOrThrow()))

        for (i in 0 until initialLoadArchive.readInt32()) {
            importStore.scriptObjectEntries.add(FScriptObjectEntry(initialLoadArchive, globalNameMap.nameEntries).also {
                val mappedName = FMappedName.fromMinimalName(it.objectName)
                check(mappedName.isGlobal())
                it.objectName = globalNameMap.getMinimalName(mappedName)

                importStore.scriptObjectEntriesMap[it.globalIndex] = it
            })
        }
    }

    fun loadContainers(containers: Iterable<FIoDispatcherMountedContainer>) {
        val containersToLoad = containers.filter { it.containerId.isValid() }

        if (containersToLoad.isEmpty()) {
            return
        }

        val remaining = AtomicInteger(containersToLoad.size)
        val event = CompletableFuture<Void>()
        val ioBatch = ioDispatcher.newBatch()

        for (container in containersToLoad) {
            val containerId = container.containerId
            val loadedContainer = loadedContainers.getOrPut(containerId) { FLoadedContainer() }
            if (loadedContainer.bValid && loadedContainer.order >= container.environment.order) {
                LOG_STREAMING.debug("Skipping loading mounted container ID '0x%016X', already loaded with higher order".format(containerId.value().toLong()))
                if (remaining.decrementAndGet() == 0) {
                    event.complete(null)
                }
                continue
            }

            LOG_STREAMING.debug("Loading mounted container ID '0x%016X'".format(containerId.value().toLong()))
            loadedContainer.bValid = true
            loadedContainer.order = container.environment.order

            val headerChunkId = FIoChunkId(containerId.value(), 0u, EIoChunkType.ContainerHeader)
            ioBatch.readWithCallback(headerChunkId, FIoReadOptions(), IoDispatcherPriority_High.value) { result ->
                val ioBuffer = result.getOrThrow()

                Thread {
                    val containerHeader = FContainerHeader(FByteArchive(ioBuffer))

                    val bHasContainerLocalNameMap = containerHeader.names.isNotEmpty()
                    if (bHasContainerLocalNameMap) {
                        loadedContainer.containerNameMap.load(containerHeader.names, containerHeader.nameHashes, FMappedName.EType.Container)
                    }

                    loadedContainer.packageCount = containerHeader.packageCount
                    loadedContainer.storeEntries = containerHeader.storeEntries
                    synchronized(packageNameMapsCritical) {
                        val storeEntriesAr = FByteArchive(loadedContainer.storeEntries)
                        val storeEntries = Array(loadedContainer.packageCount.toInt()) { FPackageStoreEntry(storeEntriesAr) }

                        storeEntries.forEachIndexed { index, containerEntry ->
                            val packageId = containerHeader.packageIds[index]
                            storeEntriesMap[packageId] = containerEntry
                        }

                        var localizedPackages: FSourceToLocalizedPackageIdMap? = null
                        for (cultureName in currentCultureNames) {
                            localizedPackages = containerHeader.culturePackageMap[cultureName]
                            if (localizedPackages != null) {
                                break
                            }
                        }

                        if (localizedPackages != null) {
                            for (pair in localizedPackages) {
                                val sourceId = pair.first
                                val localizedId = pair.second
                                redirectsPackageMap[sourceId] = localizedId
                            }
                        }

                        for (redirect in containerHeader.packageRedirects) {
                            redirectsPackageMap[redirect.first] = redirect.second
                        }
                    }

                    if (remaining.decrementAndGet() == 0) {
                        event.complete(null)
                    }
                }.start()
            }
        }

        ioBatch.issue()
        event.await()

        applyRedirects(redirectsPackageMap)
    }

    override fun onContainerMounted(container: FIoDispatcherMountedContainer) {
        loadContainers(listOf(container))
    }

    fun applyRedirects(redirects: Map<FPackageId, FPackageId>) {
        synchronized(packageNameMapsCritical) {
            if (redirects.isEmpty()) {
                return
            }

            for ((sourceId, redirectId) in redirects) {
                check(redirectId.isValid())
                val redirectEntry = storeEntriesMap[redirectId]!!
                storeEntriesMap[sourceId] = redirectEntry
            }

            for (storeEntry in storeEntriesMap.values) {
                storeEntry.importedPackages.forEachIndexed { index, importedPackageId ->
                    redirects[importedPackageId]?.also { storeEntry.importedPackages[index] = it }
                }
            }
        }
    }

    fun finalizeInitialLoad() {
        //importStore.findAllScriptObjects()

        /*LOG_STREAMING.info("AsyncLoading2 - InitialLoad Finalized: %d script object entries in %.2f KB"
            .format(importStore.scriptObjects.size, ObjectSizeCalculator.getObjectSize(importStore.scriptObjects) / 1024f))*/
    }

    //inline val globalImportStore get() = importStore

    //fun removePackage(package: UPackage) {}

    //fun removePublicExport(object: UObject) {}

    fun findStoreEntry(packageId: FPackageId): FPackageStoreEntry? {
        synchronized(packageNameMapsCritical) {
            return storeEntriesMap[packageId]
        }
    }

    fun getRedirectedPackageId(packageId: FPackageId): FPackageId {
        synchronized(packageNameMapsCritical) {
            return redirectsPackageMap[packageId] ?: FPackageId()
        }
    }

    class FLoadedContainer {
        val containerNameMap = FNameMap()
        lateinit var storeEntries: ByteArray
        //lateinit var storeEntries: Array<FPackageStoreEntry>
        var packageCount = 0u
        var order = 0
        var bValid = false
    }
}