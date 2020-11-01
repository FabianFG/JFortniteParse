package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_High
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger
import kotlin.concurrent.thread

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

    // val loadedPackageStore = FLoadedPackageStore()
    var scriptArcsCount = 0

    fun setupCulture() {
        currentCultureNames.clear()
        currentCultureNames.add(Locale.getDefault().toString().replace('_', '-'))
    }

    fun setupInitialLoadData() {
        val initialLoadIoBuffer: ByteArray
        val initialLoadEvent = CompletableFuture<ByteArray>()

        ioDispatcher.readWithCallback(
            FIoChunkId(0u, 0u, EIoChunkType.LoaderInitialLoadMeta),
            FIoReadOptions(),
            IoDispatcherPriority_High
        ) { initialLoadEvent.complete(it) }

        initialLoadIoBuffer = initialLoadEvent.await()

        val initialLoadArchive = FByteArchive(ByteBuffer.wrap(initialLoadIoBuffer))
        for (i in 0 until initialLoadArchive.readInt32()) {
            importStore.scriptObjectEntries.add(FScriptObjectEntry(initialLoadArchive))
        }

        for (scriptObjectEntry in importStore.scriptObjectEntries) {
            val mappedName = FMappedName.fromMinimalName(scriptObjectEntry.objectName)
            check(mappedName.isGlobal())
            scriptObjectEntry.objectName = globalNameMap.getMinimalName(mappedName)

            importStore.scriptObjectEntriesMap[scriptObjectEntry.globalIndex] = scriptObjectEntry
        }
    }

    fun loadContainers(containers: Iterable<FIoDispatcherMountedContainer>) {
        val containersToLoad = containers.filter { it.containerId.isValid() }

        if (containersToLoad.isEmpty()) {
            return
        }

        val remaining = AtomicInteger(containersToLoad.size)
        val event = CompletableFuture<Void>()

        for (container in containersToLoad) {
            val containerId = container.containerId
            val loadedContainer = loadedContainers.getOrPut(containerId) { FLoadedContainer() }
            if (loadedContainer.bValid && loadedContainer.order >= container.environment.order) {
                LOG_STREAMING.debug("Skipping loading mounted container ID '0x%dX', already loaded with higher order".format(containerId.value().toLong()))
                if (remaining.decrementAndGet() == 0) {
                    event.complete(null)
                }
                continue
            }

            LOG_STREAMING.debug("Loading mounted container ID '0x%dX'".format(containerId.value().toLong()))
            loadedContainer.bValid = true
            loadedContainer.order = container.environment.order

            val headerChunkId = FIoChunkId(containerId.value(), 0u, EIoChunkType.ContainerHeader)
            ioDispatcher.readWithCallback(headerChunkId, FIoReadOptions(), IoDispatcherPriority_High) {
                val ioBuffer = it.getOrThrow()

                thread {
                    val Ar = FByteArchive(ByteBuffer.wrap(ioBuffer))

                    val containerHeader = FContainerHeader(Ar)

                    val bHasContainerLocalNameMap = containerHeader.names.isNotEmpty()
                    if (bHasContainerLocalNameMap) {
                        loadedContainer.containerNameMap = FNameMap()
                        loadedContainer.containerNameMap.load(containerHeader.names, containerHeader.nameHashes, FMappedName.EType.Container)
                    }

                    loadedContainer.packageCount = containerHeader.packageCount
                    loadedContainer.storeEntries = containerHeader.storeEntries
                    synchronized(packageNameMapsCritical) {
                        val storeEntries = FByteArchive(loadedContainer.storeEntries).readTArray(loadedContainer.packageCount.toInt()) { FPackageStoreEntry(Ar) }

                        for ((index, containerEntry) in storeEntries.withIndex()) {
                            val packageId = containerHeader.packageIds[index]
                            storeEntriesMap.getOrPut(packageId) { containerEntry }
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
                }
            }
        }

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
                val redirectEntry = storeEntriesMap[redirectId]
                check(redirectEntry != null)
                storeEntriesMap[sourceId] = redirectEntry
//                TODO storeEntriesMap.getOrPut(sourceId) { redirectEntry }
            }

            for ((_, storeEntry) in storeEntriesMap) {
                for ((index, importedPackageId) in storeEntry.importedPackages.withIndex()) {
                    val redirectId = redirects[importedPackageId]
                    if (redirectId != null) {
                        storeEntry.importedPackages[index] = redirectId
                    }
                }
            }
        }
    }

    class FLoadedContainer {
        lateinit var containerNameMap: FNameMap
        lateinit var storeEntries: ByteArray
        var packageCount = 0u
        var order = 0
        var bValid = false
    }
}