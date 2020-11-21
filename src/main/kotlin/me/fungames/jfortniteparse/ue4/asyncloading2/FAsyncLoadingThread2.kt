package me.fungames.jfortniteparse.ue4.asyncloading2

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.io.*
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_Medium
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.util.INDEX_NONE
import org.slf4j.event.Level
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import kotlin.jvm.internal.Ref.BooleanRef
import kotlin.math.min

class FAsyncLoadingThread2 : Runnable {
    /** Thread to run the worker FRunnable on */
    private var thread: Thread? = null
    private val bStopRequested = AtomicBoolean()
    private val bSuspendRequested = AtomicBoolean()
    //private val workers = mutableListOf<FAsyncLoadingThreadWorker>()
    private val activeWorkersCount = AtomicInteger()
    private var bWorkersSuspended = false

    /** [ASYNC/GAME THREAD] true if the async thread is actually started. We don't start it until after we boot because the boot process on the game thread can create objects that are also being created by the loader  */
    private var bThreadStarted = false

    private var bLazyInitializedFromLoadPackage = false

    /** [ASYNC/GAME THREAD] Event used to signal loading should be cancelled */
    private val cancelLoadingEvent = CompletableFuture<Void>()
    /** [ASYNC/GAME THREAD] Event used to signal that the async loading thread should be suspended */
    private val threadSuspendedEvent = CompletableFuture<Void>()
    /** [ASYNC/GAME THREAD] Event used to signal that the async loading thread has resumed */
    private val threadResumedEvent = CompletableFuture<Void>()
    /** [ASYNC/GAME THREAD] List of queued packages to stream */
    private var queuedPackages = mutableListOf<FAsyncPackageDesc2>()
    /** [ASYNC/GAME THREAD] Package queue critical section */
    private var queueCritical = Object()
    /*internal val loadedPackagesToProcess = mutableListOf<FAsyncPackage2>()
    /** [GAME THREAD] Game thread completedPackages list */
    private val completedPackages = mutableListOf<FAsyncPackage2>()

    class FQueuedFailedPackageCallback(
        val packageName: FName,
        val callback: FCompletionCallback
    )

    private val queuedFailedPackageCallbacks = mutableListOf<FQueuedFailedPackageCallback>()*/

    private val asyncPackagesCritical = Object()
    /** Packages in active loading with GetAsyncPackageId() as key */
    private val asyncPackageLookup = mutableMapOf<FPackageId, FAsyncPackage2>()

    internal val externalReadQueue = LinkedList<FAsyncPackage2>() // TODO MPSC please
    private val waitingForIoBundleCounter = AtomicInteger()

    /** List of all pending package requests */
    private val pendingRequests = ConcurrentHashMap.newKeySet<Int>()

    /** [ASYNC/GAME THREAD] Number of package load requests in the async loading queue */
    private val queuedPackagesCounter = AtomicInteger()

    private val packageRequestID = AtomicInteger()

    /** I/O Dispatcher */
    private val ioDispatcher: FIoDispatcher

    private val globalNameMap = FNameMap()
    internal val globalPackageStore: FPackageStore

    class FBundleIoRequest(val pkg: FAsyncPackage2)

    private val waitingIoRequests = PriorityQueue<FBundleIoRequest> { o1, o2 -> o1.pkg.loadOrder.compareTo(o2.pkg.loadOrder) }
    private var pendingBundleIoRequestsTotalSize = 0uL

    val altZenaphore = FZenaphore()
    val workerZenaphores = mutableListOf<FZenaphore>()
    //val graphAllocator = FAsyncLoadEventGraphAllocator()
    val eventQueue = FAsyncLoadEventQueue2()
    val mainThreadEventQueue = FAsyncLoadEventQueue2()
    val altEventQueues = mutableListOf<FAsyncLoadEventQueue2>()
    val eventSpecs: List<FAsyncLoadEventSpec>
    var provider: FileProvider? = null

    constructor(ioDispatcher: FIoDispatcher) {
        this.thread = null
        this.ioDispatcher = ioDispatcher
        this.globalPackageStore = FPackageStore(ioDispatcher, globalNameMap)

        //GEventDrivenLoaderEnabled = true

        altEventQueues.add(eventQueue)
        altEventQueues.forEach { it.zenaphore = altZenaphore }

        eventSpecs = listOf(
            FAsyncLoadEventSpec(FAsyncPackage2::eventProcessPackageSummary, eventQueue, false),
            FAsyncLoadEventSpec(FAsyncPackage2::eventExportsDone, eventQueue, true),

            FAsyncLoadEventSpec(FAsyncPackage2::eventProcessExportBundle, eventQueue, false),
            FAsyncLoadEventSpec(FAsyncPackage2::eventPostLoadExportBundle, eventQueue, false),
            //FAsyncLoadEventSpec(FAsyncPackage2::eventDeferredPostLoadExportBundle, mainThreadEventQueue, false)
            FAsyncLoadEventSpec(FAsyncPackage2::eventDeferredPostLoadExportBundle, eventQueue, false)
        )

        FAsyncLoadingThreadState2.create(ioDispatcher)

        LOG_STREAMING.info("AsyncLoading2 - Created: Event Driven Loader: ${true}, Async Loading Thread: ${true}, Async Post Load: ${true}")
    }

    override fun run() {
        //asyncLoadingThreadId = Thread.currentThread().id

        FAsyncLoadingThreadState2.create(ioDispatcher)

        val threadState = FAsyncLoadingThreadState2.get()!!

        finalizeInitialLoad()

        val waiter = FZenaphoreWaiter(altZenaphore, "WaitForEvents")
        var bIsSuspended = false
        while (!bStopRequested.get()) {
            if (bIsSuspended) {
                if (!bSuspendRequested.get() /*&& !isGarbageCollectionWaiting()*/) {
                    threadResumedEvent.complete(null)
                    bIsSuspended = false
                    resumeWorkers()
                } else {
                    Thread.sleep(1)
                }
            } else {
                var bDidSomething = false
                do {
                    bDidSomething = false

                    if (queuedPackagesCounter.get() > 0) {
                        if (createAsyncPackagesFromQueue()) {
                            bDidSomething = true
                        }
                    }

                    var bShouldSuspend = false
                    var bPopped = false
                    do {
                        bPopped = false
                        for (queue in altEventQueues) {
                            if (queue.popAndExecute(threadState)) {
                                bPopped = true
                                bDidSomething = true
                            }

                            if (bSuspendRequested.get() /*|| isGarbageCollectionWaiting()*/) {
                                bShouldSuspend = true
                                bPopped = false
                                break
                            }
                        }
                    } while (bPopped)

                    if (bShouldSuspend || bSuspendRequested.get() /*|| isGarbageCollectionWaiting()*/) {
                        suspendWorkers()
                        threadSuspendedEvent.complete(null)
                        bIsSuspended = true
                        bDidSomething = true
                        break
                    }

                    var bDidExternalRead = false
                    do {
                        bDidExternalRead = false
                        val pkg = externalReadQueue.peek()
                        if (pkg != null) {
                            val action = FAsyncPackage2.EExternalReadAction.ExternalReadAction_Poll

                            val result = pkg.processExternalReads(action)
                            if (result == EAsyncPackageState.Complete) {
                                externalReadQueue.pop()
                                bDidExternalRead = true
                                bDidSomething = true
                            }
                        }
                    } while (bDidExternalRead)
                } while (bDidSomething)

                /*if (!bDidSomething) {
                    if (threadState.hasDeferredFrees()) {
                        threadState.processDeferredFrees()
                        bDidSomething = true
                    }

                    if (!deferredDeletePackages.isEmpty()) {
                        var pkg: FAsyncPackage2? = null
                        var count = 0
                        while (++count <= 100 && deferredDeletePackages.dequeue(pkg)) {
                            deleteAsyncPackage(pkg)
                        }
                        bDidSomething = true
                    }
                }*/

                if (!bDidSomething) {
                    if (waitingForIoBundleCounter.get() > 0) {
                        waiter.wait0()
                    } else {
                        val pkg = externalReadQueue.peek()
                        if (pkg != null) {
                            val result = pkg.processExternalReads(FAsyncPackage2.EExternalReadAction.ExternalReadAction_Wait)
                            check(result == EAsyncPackageState.Complete)
                            externalReadQueue.pop()
                        } else {
                            waiter.wait0()
                        }
                    }
                }
            }
        }
    }

    fun stop() {
        //workers.forEach { it.stopThread() }
        bSuspendRequested.set(true)
        bStopRequested.set(true)
        altZenaphore.notifyAll0()
    }

    /** Start the async loading thread */
    fun startThread() {
        val bAsyncLoadingThreadEnabled = true/*FAsyncLoadingThreadSettings.get().bAsyncLoadingThreadEnabled*/
        if (!bAsyncLoadingThreadEnabled) {
            finalizeInitialLoad()
        } else if (thread == null) {
            LOG_STREAMING.debug("Starting Async Loading Thread.")
            bThreadStarted = true
            thread = Thread(this, "FAsyncLoadingThread").apply { start() }
        }

        LOG_STREAMING.info("AsyncLoading2 - Thread Started: $bAsyncLoadingThreadEnabled, IsInitialLoad: $GIsInitialLoad")
    }

    /** Returns true if async loading is suspended */
    fun isAsyncLoadingSuspended() = bSuspendRequested.get()

    /**
     * [ASYNC THREAD] Finds an existing async package in the AsyncPackages by its name.
     *
     * @param packageName async package name.
     * @return Package or null if not found
     */
    fun findAsyncPackage(packageName: FName): FAsyncPackage2? {
        val packageId = FPackageId.fromName(packageName)
        if (packageId.isValid()) {
            synchronized(asyncPackagesCritical) {
                return asyncPackageLookup[packageId]
            }
        }
        return null
    }

    fun getAsyncPackage(packageId: FPackageId): FAsyncPackage2? {
        synchronized(asyncPackagesCritical) {
            return asyncPackageLookup[packageId]
        }
    }

    //fun insertPackage(package: FAsyncPackage2, bReinsert: Boolean = false) {}

    fun findOrInsertPackage(desc: FAsyncPackageDesc2, bInserted: BooleanRef): FAsyncPackage2? {
        var pkg: FAsyncPackage2?
        bInserted.element = false
        synchronized(asyncPackagesCritical) {
            pkg = asyncPackageLookup[desc.getAsyncPackageId()]
            if (pkg == null) {
                pkg = createAsyncPackage(desc)
                check(pkg != null) { "Failed to create async package " + desc.diskPackageName }
                //pkg.addRef()
                asyncPackageLookup[desc.getAsyncPackageId()] = pkg!!
                bInserted.element = true
            } else if (desc.requestID > 0) {
                pkg!!.addRequestID(desc.requestID)
            }
            desc.packageLoadedCallback?.let { pkg?.addCompletionCallback(it) }
        }
        return pkg
    }

    /**
     * [ASYNC/GAME THREAD] Queues a package for streaming.
     *
     * @param pkg package descriptor.
     */
    fun queuePackage(pkg: FAsyncPackageDesc2) {
        check(pkg.storeEntry != null) { "No package store entry for package " + pkg.diskPackageName }
        synchronized(queueCritical) {
            queuedPackagesCounter.getAndIncrement()
            queuedPackages.add(FAsyncPackageDesc2(pkg, pkg.packageLoadedCallback))
        }
        altZenaphore.notifyOne()
    }

    fun initializeLoading() {
        //FBulkDataBase.ioDispatcher = ioDispatcher

        //globalPackageStore.initialize()

        //asyncThreadReady().increment()

        LOG_STREAMING.info("AsyncLoading2 - Initialized")
    }

    fun shutdownLoading() {
        thread = null
        cancelLoadingEvent.cancel(false)
        //cancelLoadingEvent = null
        threadSuspendedEvent.cancel(false)
        //threadSuspendedEvent = null
        threadResumedEvent.cancel(false)
        //threadResumedEvent = null
    }

    fun loadPackage(inName: String, inPackageToLoadFrom: String? = null, completionCallback: FCompletionCallback? = null): Int {
        if (!bLazyInitializedFromLoadPackage) {
            bLazyInitializedFromLoadPackage = true
            lazyInitializeFromLoadPackage()
        }

        var requestID = INDEX_NONE

        // happy path where all inputs are actual package names
        val name = FName.dummy(inName)
        var diskPackageName: FName = if (inPackageToLoadFrom.isNullOrEmpty()) name else FName.dummy(inPackageToLoadFrom)
        var bHasCustomPackageName = name != diskPackageName

        // Verify packageToLoadName, or fixup to handle any input string that can be converted to a long package name.
        var diskPackageId = FPackageId.fromName(name)
        var storeEntry = globalPackageStore.findStoreEntry(diskPackageId)
        if (storeEntry == null) {
            val packageNameStr = diskPackageName.toString()
        }

        // Verify CustomPackageName, or fixup to handle any input string that can be converted to a long package name.
        // CustomPackageName must not be an existing disk package name,
        // that could cause missing or incorrect import objects for other packages.
        var customPackageName = FName()
        var customPackageId = FPackageId()
        if (bHasCustomPackageName) {
            val packageId = FPackageId.fromName(name)
            if (globalPackageStore.findStoreEntry(packageId) == null) {
                //val packageNameStr = name.toString()
                //if (FPackageName.isValidLongPackageName(packageNameStr)) {
                customPackageName = name
                customPackageId = packageId
                /*} else {
                    val newPackageNameStr: String
                    if (FPackageName.tryConvertFilenameToLongPackageName(packageNameStr, newPackageNameStr)) {
                        packageId = FPackageId.fromName(FName.dummy(newPackageNameStr))
                        if (globalPackageStore.findStoreEntry(packageId) == null) {
                            customPackageName = FName.dummy(newPackageNameStr)
                            customPackageId = packageId
                        }
                    }
                }*/
            }
        }
        check(customPackageId.isValid() == !customPackageName.isNone())

        var bCustomNameIsValid = (!bHasCustomPackageName && customPackageName.isNone()) || (bHasCustomPackageName && !customPackageName.isNone())
        var bDiskPackageIdIsValid = storeEntry != null
        if (!bDiskPackageIdIsValid) {
            // While there is an active load request for (inName=/Temp/PackageABC_abc, inPackageToLoadFrom=/Game/PackageABC), then allow these requests too:
            // (inName=/Temp/PackageA_abc, inPackageToLoadFrom=/Temp/PackageABC_abc) and (inName=/Temp/PackageABC_xyz, inPackageToLoadFrom=/Temp/PackageABC_abc)
            val pkg = getAsyncPackage(diskPackageId)
            if (pkg != null) {
                if (customPackageName.isNone()) {
                    customPackageName = pkg.desc.customPackageName
                    customPackageId = pkg.desc.customPackageId
                    bHasCustomPackageName = true
                    bCustomNameIsValid = true
                }
                diskPackageName = pkg.desc.diskPackageName
                diskPackageId = pkg.desc.diskPackageId
                storeEntry = pkg.desc.storeEntry
                bDiskPackageIdIsValid = true
            }
        }

        if (bDiskPackageIdIsValid && bCustomNameIsValid) {
            //broadcast OnAsyncLoadPackage

            // Generate new request ID and add it immediately to the global request list (it needs to be there before we exit
            // this function, otherwise it would be added when the packages are being processed on the async thread).
            requestID = packageRequestID.getAndIncrement()
            addPendingRequest(requestID)

            // Add new package request
            val packageDesc = FAsyncPackageDesc2(requestID, diskPackageId, storeEntry, diskPackageName, customPackageId, customPackageName, completionCallback)

            // Fixup for redirected packages since the slim StoreEntry itself has been stripped from both package names and package ids
            val redirectedDiskPackageId = globalPackageStore.getRedirectedPackageId(diskPackageId)
            if (redirectedDiskPackageId.isValid()) {
                packageDesc.diskPackageId = redirectedDiskPackageId
                packageDesc.sourcePackageName = packageDesc.diskPackageName
                packageDesc.diskPackageName = FName()
            }

            queuePackage(packageDesc)

            asyncPackageLog(Level.DEBUG, packageDesc, "LoadPackage: QueuePackage", "Package added to pending queue.")
        } else {
            val packageDesc = FAsyncPackageDesc2(requestID, diskPackageId, storeEntry, diskPackageName, customPackageId, customPackageName)
            if (!bDiskPackageIdIsValid) {
                asyncPackageLog(Level.WARN, packageDesc, "LoadPackage: SkipPackage",
                    "The package to load does not exist on disk or in the loader")
            } else /*if (!bCustomNameIsValid)*/ {
                asyncPackageLog(Level.WARN, packageDesc, "LoadPackage: SkipPackage", "The custom package name is invalid")
            }

            if (completionCallback != null) {
                // Queue completion callback and execute at next process loaded packages call to maintain behavior compatibility with old loader
                //queuedFailedPackageCallbacks.add(FQueuedFailedPackageCallback(name, completionCallback))
                completionCallback.onCompletion(name, Result.failure(ParserException("The package to load does not exist on disk or in the loader")))
            }
        }

        return requestID
    }

    /**
     * [ASYNC/GAME THREAD] Checks if a request ID already is added to the loading queue
     */
    fun containsRequestID(requestID: Int) = requestID in pendingRequests

    /**
     * [ASYNC/GAME THREAD] Adds a request ID to the list of pending requests
     */
    fun addPendingRequest(requestID: Int) = pendingRequests.add(requestID)

    /**
     * [ASYNC/GAME THREAD] Removes a request ID from the list of pending requests
     */
    fun removePendingRequests(requestIDs: Collection<Int>) = pendingRequests.removeAll(requestIDs)

    //fun addPendingCDOs(package: FAsyncPackage2, classes: Collection<Class>) {}

    private fun suspendWorkers() {}

    private fun resumeWorkers() {}

    private fun lazyInitializeFromLoadPackage() {
        globalNameMap.loadGlobal(ioDispatcher)
        if (GIsInitialLoad) {
            globalPackageStore.setupInitialLoadData()
        }
        globalPackageStore.setupCulture()
        globalPackageStore.loadContainers(ioDispatcher.mountedContainers)
        ioDispatcher.addOnContainerMountedListener(globalPackageStore)
    }

    private fun finalizeInitialLoad() {
        globalPackageStore.finalizeInitialLoad()
        //check(pendingCDOs.isEmpty())
        //pendingCDOs.clear()
    }

    // custom method so no "main thread loops" are required
    fun processLoadedPackage(pkg: FAsyncPackage2) {
        check(pkg.asyncPackageLoadingState == EAsyncPackageLoadingState2.DeferredPostLoadDone)
        pkg.asyncPackageLoadingState = EAsyncPackageLoadingState2.Finalize

        synchronized(asyncPackagesCritical) {
            asyncPackageLookup.remove(pkg.desc.getAsyncPackageId())
        }

        // Call external callbacks
        pkg.callCompletionCallbacks()

        // We don't need the package anymore
        check(pkg.asyncPackageLoadingState == EAsyncPackageLoadingState2.Finalize)
        pkg.asyncPackageLoadingState = EAsyncPackageLoadingState2.DeferredDelete
        pkg.markRequestIDsAsComplete()

        asyncPackageLog(Level.DEBUG, pkg.desc, "GameThread: LoadCompleted",
            "All loading of package is done, and the async package and load request will be deleted.")

        pkg.clearImportedPackages()
    }

    private fun createAsyncPackagesFromQueue(): Boolean {
        val threadState = FAsyncLoadingThreadState2.get()!!
        var bPackagesCreated = false
        val timeSliceGranularity = if (threadState.bUseTimeLimit) 4 else Integer.MAX_VALUE
        val queueCopy = mutableListOf<FAsyncPackageDesc2>()

        do {
            queueCopy.clear()
            var bShouldBreak = false
            synchronized(queueCritical) {
                val numPackagesToCopy = min(timeSliceGranularity, queuedPackages.size)
                if (numPackagesToCopy > 0) {
                    for (i in 0 until numPackagesToCopy) {
                        queueCopy.add(queuedPackages[i])
                    }
                    for (i in 0 until numPackagesToCopy) {
                        queuedPackages.removeAt(0)
                    }
                } else {
                    bShouldBreak = true
                }
            }

            if (bShouldBreak) {
                break
            }

            for (packageDesc in queueCopy) {
                val bInserted = BooleanRef()
                val pkg = findOrInsertPackage(packageDesc, bInserted)
                check(pkg != null) { "Failed to find or insert imported package " + packageDesc.diskPackageName }

                if (bInserted.element) {
                    asyncPackageLog(Level.DEBUG, packageDesc, "CreateAsyncPackages: AddPackage",
                        "Start loading package.")
                } else {
                    asyncPackageLogVerbose(Level.DEBUG, packageDesc, "CreateAsyncPackages: UpdatePackage",
                        "Package is already being loaded.")
                }

                queuedPackagesCounter.getAndDecrement()
                if (pkg != null) {
                    pkg.importPackagesRecursive()

                    if (bInserted.element) {
                        pkg.startLoading()
                    }

                    startBundleIoRequests()
                }
            }

            bPackagesCreated = bPackagesCreated || queueCopy.isNotEmpty()
        } while (!threadState.isTimeLimitExceeded("CreateAsyncPackagesFromQueue"))

        return bPackagesCreated
    }

    internal fun addBundleIoRequest(pkg: FAsyncPackage2) {
        waitingForIoBundleCounter.getAndIncrement()
        waitingIoRequests.add(FBundleIoRequest(pkg)) // heapPush
    }

    internal fun bundleIoRequestCompleted(pkg: FAsyncPackage2) {
        check(pendingBundleIoRequestsTotalSize >= pkg.exportBundlesSize)
        pendingBundleIoRequestsTotalSize -= pkg.exportBundlesSize
        if (waitingIoRequests.isNotEmpty()) {
            startBundleIoRequests()
        }
    }

    private fun startBundleIoRequests() {
        val maxPendingRequestsSize = 256uL shl 20
        val ioBatch = ioDispatcher.newBatch()
        while (waitingIoRequests.isNotEmpty()) {
            val bundleIoRequest = waitingIoRequests.peek()
            val pkg = bundleIoRequest.pkg
            if (pendingBundleIoRequestsTotalSize > 0u && pendingBundleIoRequestsTotalSize + pkg.exportBundlesSize > maxPendingRequestsSize) {
                break
            }
            pendingBundleIoRequestsTotalSize += pkg.exportBundlesSize
            waitingIoRequests.remove()

            val readOptions = FIoReadOptions()
            pkg.ioRequest = ioBatch.readWithCallback(FIoChunkId(pkg.desc.diskPackageId.value(), 0u, EIoChunkType.ExportBundleData),
                readOptions,
                IoDispatcherPriority_Medium
            ) { result ->
                result.fold({
                    pkg.ioBuffer = it
                }, {
                    asyncPackageLog(Level.WARN, pkg.desc, "StartBundleIoRequests: FailedRead",
                        "Failed reading chunk for package: ${(it as? FIoStatusException)?.status ?: it}")
                    pkg.failedException = it
                })
                pkg.getPackageNode(EEventLoadNode2.Package_ProcessSummary).releaseBarrier()
                pkg.asyncLoadingThread.waitingForIoBundleCounter.getAndDecrement()
            }
        }
        ioBatch.issue()
    }

    private fun createAsyncPackage(desc: FAsyncPackageDesc2): FAsyncPackage2 {
        check(desc.storeEntry != null) { "No package store entry for package ${desc.diskPackageName}" }

        val data = FAsyncPackageData()
        data.exportCount = desc.storeEntry!!.exportCount
        data.exportBundleCount = desc.storeEntry!!.exportBundleCount

        val exportBundleNodeCount = data.exportBundleCount * EEventLoadNode2.ExportBundle_NumPhases.value
        val importedPackageCount = desc.storeEntry!!.importedPackages.size
        val nodeCount = EEventLoadNode2.Package_NumPhases.value + exportBundleNodeCount

        data.exports = Array(data.exportCount) { FExportObject() }
        data.importedAsyncPackages = ArrayList(importedPackageCount)
        data.packageNodes = arrayOfNulls(nodeCount)
        data.exportBundleNodes = arrayOfNulls(exportBundleNodeCount)

        //existingAsyncPackagesCounter.increment()
        return FAsyncPackage2(desc, data, this, eventSpecs).also { it.provider = provider }
    }
}