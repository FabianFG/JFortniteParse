package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.LOG_STREAMING
import me.fungames.jfortniteparse.fileprovider.PakFileProvider
import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FMappedName
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FNameMap
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE5_BASE
import me.fungames.jfortniteparse.util.await
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger

class FScriptObjectEntry {
    var objectName: FMinimalName
    var globalIndex: FPackageObjectIndex
    var outerIndex: FPackageObjectIndex
    var cdoClassIndex: FPackageObjectIndex

    constructor(Ar: FArchive, nameMap: List<String>) {
        objectName = FMinimalName(Ar, nameMap)
        globalIndex = FPackageObjectIndex(Ar)
        outerIndex = FPackageObjectIndex(Ar)
        cdoClassIndex = FPackageObjectIndex(Ar)
    }
}

class FPackageStore(val provider: PakFileProvider) : FOnContainerMountedListener {
    val loadedContainers = hashMapOf<FIoContainerId, FLoadedContainer>()

    //val currentCultureNames = mutableListOf<String>()

    val packageNameMapsCritical = Object()

    val storeEntriesMap = hashMapOf<FPackageId, FFilePackageStoreEntry>()
    val redirectsPackageMap = hashMapOf<FPackageId, Pair<FName, FPackageId>>()

    val scriptObjectEntriesMap: Map<FPackageObjectIndex, FScriptObjectEntry>

    init {
        val initialLoadArchive: FArchive
        val globalNameMap = FNameMap()
        if (provider.game >= GAME_UE5_BASE) {
            initialLoadArchive = FByteArchive(provider.saveChunk(FIoChunkId(0u, 0u, EIoChunkType5.ScriptObjects)), provider.versions)
            globalNameMap.load(initialLoadArchive, FMappedName.EType.Global)
        } else {
            val nameBuffer = provider.saveChunk(FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNames))
            val hashBuffer = provider.saveChunk(FIoChunkId(0u, 0u, EIoChunkType.LoaderGlobalNameHashes))
            globalNameMap.load(nameBuffer, hashBuffer, FMappedName.EType.Global)

            initialLoadArchive = FByteArchive(provider.saveChunk(FIoChunkId(0u, 0u, EIoChunkType.LoaderInitialLoadMeta)), provider.versions)
        }

        val numScriptObjects = initialLoadArchive.readInt32()
        scriptObjectEntriesMap = HashMap(numScriptObjects)
        repeat(numScriptObjects) {
            val entry = FScriptObjectEntry(initialLoadArchive, globalNameMap.nameEntries)
            val mappedName = FMappedName.fromMinimalName(entry.objectName)
            check(mappedName.isGlobal())
            entry.objectName = globalNameMap.getMinimalName(mappedName)

            scriptObjectEntriesMap[entry.globalIndex] = entry
        }

        //currentCultureNames.add(Locale.getDefault().toString().replace('_', '-'))
        loadContainers(provider.mountedIoStoreReaders())
    }

    fun loadContainers(containers: List<FIoStoreReaderImpl>) {
        val containersToLoad = containers.filter { it.containerId.isValid() }

        if (containersToLoad.isEmpty()) {
            return
        }

        val remaining = AtomicInteger(containersToLoad.size)
        val event = CompletableFuture<Void>()

        for (container in containersToLoad) {
            val containerId = container.containerId
            val loadedContainer = loadedContainers.getOrPut(containerId) { FLoadedContainer() }
            LOG_STREAMING.debug("Loading mounted container ID '0x%016X'".format(containerId.value().toLong()))

            val headerChunkId = FIoChunkId(containerId.value(), 0u, if (provider.game >= GAME_UE5_BASE) EIoChunkType5.ContainerHeader else EIoChunkType.ContainerHeader)
            val ioBuffer = container.read(headerChunkId)

            Thread {
                val containerHeader = FIoContainerHeader(FByteArchive(ioBuffer, provider.versions))
                loadedContainer.containerNameMap = containerHeader.redirectsNameMap
                loadedContainer.packageCount = containerHeader.packageCount
                loadedContainer.storeEntries = containerHeader.storeEntries
                synchronized(packageNameMapsCritical) {
                    loadedContainer.storeEntries.forEachIndexed { index, containerEntry ->
                        val packageId = containerHeader.packageIds[index]
                        storeEntriesMap[packageId] = containerEntry
                    }

                    /*val localizedPackages = currentCultureNames.firstNotNullOfOrNull { containerHeader.culturePackageMap[it] }
                    if (localizedPackages != null) {
                        for (redirect in localizedPackages) {
                            val sourcePackageName = redirect.sourcePackageName?.let { containerHeader.redirectsNameMap.getName(it) } ?: FName.NAME_None
                            redirectsPackageMap[redirect.sourcePackageId] = sourcePackageName to redirect.targetPackageId
                        }
                    }*/

                    for (redirect in containerHeader.packageRedirects) {
                        val sourcePackageName = redirect.sourcePackageName?.let { containerHeader.redirectsNameMap.getName(it) } ?: FName.NAME_None
                        redirectsPackageMap[redirect.sourcePackageId] = sourcePackageName to redirect.targetPackageId
                    }
                }

                if (remaining.decrementAndGet() == 0) {
                    event.complete(null)
                }
            }.start()
        }

        event.await()
    }

    override fun onContainerMounted(container: FIoStoreReaderImpl) {
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

    fun findStoreEntry(packageId: FPackageId): FFilePackageStoreEntry? {
        synchronized(packageNameMapsCritical) {
            return storeEntriesMap[packageId]
        }
    }

    fun getPackageRedirectInfo(packageId: FPackageId): Pair<FName, FPackageId>? {
        synchronized(packageNameMapsCritical) {
            return redirectsPackageMap[packageId]
        }
    }

    class FLoadedContainer {
        lateinit var containerNameMap: FNameMap
        lateinit var storeEntries: Array<FFilePackageStoreEntry>
        var packageCount = 0u
    }
}