package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.io.EIoStoreResolveResult.IoStoreResolveResult_NotFound
import me.fungames.jfortniteparse.ue4.io.EIoStoreResolveResult.IoStoreResolveResult_OK
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

val GIoDispatcherBufferSizeKB = 256
val GIoDispatcherBufferAlignment = 4096
val GIoDispatcherBufferMemoryMB = 8
val GIoDispatcherDecompressionWorkerCount = 4
val GIoDispatcherCacheSizeMB = 0

/**
 * I/O error code.
 */
enum class EIoErrorCode(val text: String) {
    Ok("OK"),
    Unknown("Unknown Status"),
    InvalidCode("Invalid Code"),
    Cancelled("Cancelled"),
    FileOpenFailed("FileOpen Failed"),
    FileNotOpen("File Not Open"),
    ReadError("Read Error"),
    WriteError("Write Error"),
    NotFound("Not Found"),
    CorruptToc("Corrupt Toc"),
    UnknownChunkID("Unknown ChunkID"),
    InvalidParameter("Invalid Parameter"),
    SignatureError("SignatureError"),
    InvalidEncryptionKey("Invalid Encryption Key")
}

/**
 * I/O status with error code and message.
 */
class FIoStatus(val errorCode: EIoErrorCode, val errorMessage: String = "") {
    companion object {
        val OK = FIoStatus(EIoErrorCode.Ok, "OK")
        val UNKNOWN = FIoStatus(EIoErrorCode.Unknown, "Unknown Status")
        val INVALID = FIoStatus(EIoErrorCode.InvalidCode, "Invalid Code")
    }

    inline val isOk get() = errorCode == EIoErrorCode.Ok
    inline val isCompleted get() = errorCode != EIoErrorCode.Unknown
    override fun toString() = "$errorMessage (${errorCode.text})"
    inline fun toException() = FIoStatusException(this)
}

class FIoStatusException : IOException {
    val status: FIoStatus

    constructor(status: FIoStatus, cause: Throwable? = null) : super(status.toString(), cause) {
        this.status = status
    }

    constructor(errorCode: EIoErrorCode, errorMessage: String = "", cause: Throwable? = null) : this(FIoStatus(errorCode, errorMessage), cause)
}

//////////////////////////////////////////////////////////////////////////

/**
 * Helper used to manage creation of I/O store file handles etc
 */
class FIoStoreEnvironment(var path: String, var order: Int = 0)

class FIoChunkHash {
    /*private*/ val hash = ByteArray(32)

    constructor(Ar: FArchive) {
        Ar.read(hash)
    }
}

/**
 * Identifier to a chunk of data.
 */
class FIoChunkId {
    companion object {
        val INVALID_CHUNK_ID = createEmptyId()

        private inline fun createEmptyId() = FIoChunkId(ByteArray(12), 12)
    }

    /*private*/ var id = ByteArray(12)

    constructor(id: ByteArray, size: Int) {
        check(size == 12)
        this.id = id
    }

    constructor(chunkId: ULong, chunkIndex: UShort, ioChunkType: EIoChunkType) : this(
        ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
            .putLong(chunkId.toLong())
            .putShort(chunkIndex.toShort())
            .put(11, ioChunkType.ordinal.toByte())
            .array(), 12)

    constructor(Ar: FArchive) {
        Ar.read(id)
    }

    override fun hashCode(): Int {
        return id.contentHashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FIoChunkId

        if (!id.contentEquals(other.id)) return false

        return true
    }

    inline fun isValid() = this != INVALID_CHUNK_ID
}

/**
 * Addressable chunk types.
 */
enum class EIoChunkType {
    Invalid,
    InstallManifest,
    ExportBundleData,
    BulkData,
    OptionalBulkData,
    MemoryMappedBulkData,
    LoaderGlobalMeta,
    LoaderInitialLoadMeta,
    LoaderGlobalNames,
    LoaderGlobalNameHashes,
    ContainerHeader
}

//////////////////////////////////////////////////////////////////////////

class FIoReadOptions {
    var offset = 0uL
        private set
    var size = 0uL.inv()
        private set
    var targetVa: ByteArray? = null
    var targetVaOff = 0
    private var flags = 0

    constructor()

    constructor(offset: ULong, size: ULong) {
        this.offset = offset
        this.size = offset
    }

    fun setRange(offset: ULong, size: ULong) {
        this.offset = offset
        this.size = offset
    }
}

//////////////////////////////////////////////////////////////////////////

class FIoBatchReadOptions {
    var targetVa: ByteArray? = null
}

//////////////////////////////////////////////////////////////////////////

interface FIoRequest {
    val isOk: Boolean
    val status: FIoStatus
    val chunkId: FIoChunkId
    fun getResultOrThrow(): ByteArray
}

typealias FIoReadCallback = (Result<ByteArray>) -> Unit

enum class EIoDispatcherPriority {
    IoDispatcherPriority_Low,
    IoDispatcherPriority_Medium,
    IoDispatcherPriority_High,

    IoDispatcherPriority_Count
}

/**
 * I/O batch
 *
 * This is a primitive used to group I/O requests for synchronization
 * purposes
 */
class FIoBatch {
    var dispatcher: FIoDispatcherImpl? = null
    var impl: FIoBatchImpl? = null

    constructor()

    constructor(dispatcher: FIoDispatcherImpl, impl: FIoBatchImpl) {
        this.dispatcher = dispatcher
        this.impl = impl
    }

    fun isValid() = impl != null

    fun read(chunk: FIoChunkId, options: FIoReadOptions): FIoRequest =
        dispatcher!!.allocRequest(impl!!, chunk, options)

    fun forEachRequest(callback: (FIoRequest) -> Boolean) {
        dispatcher!!.iterateBatch(impl!!, callback)
    }

    /**
     * Initiates the loading of the batch as individual requests.
     */
    fun issue(priority: EIoDispatcherPriority) {
        dispatcher!!.issueBatch(impl!!, priority)
    }

    /**
     * Initiates the loading of the batch to a single contiguous output buffer. The requests will be in the
     * same order that they were added to the FIoBatch.
     * NOTE: It is not valid to call this on a batch containing requests that have been given a TargetVa to
     * read into as the requests are supposed to read into the batch's output buffer, doing so will cause the
     * method to return an error 'InvalidParameter'.
     *
     * @param options A set of options allowing customization on how the load will work.
     * @param callback An optional callback that will be triggered once the batch has finished loading.
     * The batch's output buffer will be provided as the parameter of the callback.
     *
     * @return This methods had the capacity to fail so the return value should be checked.
     */
    fun issueWithCallback(options: FIoBatchReadOptions, priority: EIoDispatcherPriority, callback: FIoReadCallback? = null) {
        dispatcher!!.setupBatchForContiguousRead(impl!!, options.targetVa, callback)
        dispatcher!!.issueBatch(impl!!, priority)
    }

    fun waitRequests() { // original name: Wait
        while (impl!!.unfinishedRequestsCount.get() > 0) {
            Thread.sleep(0)
        }
    }

    fun cancel() {
        throw NotImplementedError() // unimplemented()
    }
}

class FIoDispatcherMountedContainer(
    val environment: FIoStoreEnvironment,
    val containerId: FIoContainerId
)

interface FOnContainerMountedListener {
    fun onContainerMounted(container: FIoDispatcherMountedContainer)
}

private var GIoDispatcher: FIoDispatcher? = null

/**
 * I/O dispatcher
 */
class FIoDispatcher private constructor() {
    private val impl = FIoDispatcherImpl(false /*FGenericPlatformProcess.supportsMultithreading()*/)

    fun mount(environment: FIoStoreEnvironment, encryptionKeyGuid: FGuid, encryptionKey: ByteArray?) =
        impl.mount(environment, encryptionKeyGuid, encryptionKey)

    fun newBatch() = FIoBatch(impl, impl.allocBatch())

    fun freeBatch(batch: FIoBatch) {
        impl.freeBatch(batch.impl)
        batch.impl = null
    }

    fun readWithCallback(chunkId: FIoChunkId, options: FIoReadOptions, priority: EIoDispatcherPriority, callback: FIoReadCallback) {
        impl.readWithCallback(chunkId, options, priority, callback)
    }

    fun doesChunkExist(chunkId: FIoChunkId) = impl.doesChunkExist(chunkId)

    fun getSizeForChunk(chunkId: FIoChunkId) = impl.getSizeForChunk(chunkId)

    val mountedContainers get() = impl.mountedContainers

    val totalLoaded get() = impl.totalLoaded

    fun addOnContainerMountedListener(listener: FOnContainerMountedListener) {
        impl.containerMountedListeners.add(listener)
    }

    fun removeOnContainerMountedListener(listener: FOnContainerMountedListener) {
        impl.containerMountedListeners.remove(listener)
    }

    companion object {
        @JvmStatic
        fun isValidEnvironment(environment: FIoStoreEnvironment) =
            FFileIoStore.isValidEnvironment(environment)

        @JvmStatic
        fun isInitialized() = GIoDispatcher != null

        @JvmStatic
        fun initialize() {
            GIoDispatcher = FIoDispatcher()
            //impl.initialize()
        }

        @JvmStatic
        fun initializePostSettings() {
            check(GIoDispatcher != null)
            try {
                GIoDispatcher!!.impl.initializePostSettings()
            } catch (e: FIoStatusException) {
                LOG_IO_DISPATCHER.error("Failed to initialize IoDispatcher")
            }
        }

        @JvmStatic
        fun shutdown() {
            GIoDispatcher = null
        }

        @JvmStatic
        fun get() = GIoDispatcher!!
    }
}

class FIoDispatcherImpl(val bIsMultithreaded: Boolean) : Runnable {
    val eventQueue = FIoDispatcherEventQueue()

    private val fileIoStore = FFileIoStore(eventQueue, bIsMultithreaded)
    private val waitingLock = Object()
    private var waitingRequestsHead: FIoRequestImpl? = null
    private var waitingRequestsTail: FIoRequestImpl? = null
    private val bStopRequested = AtomicBoolean(false)
    internal val mountedContainers: MutableList<FIoDispatcherMountedContainer> = Collections.synchronizedList(mutableListOf<FIoDispatcherMountedContainer>())
    internal val containerMountedListeners = mutableListOf<FOnContainerMountedListener>()
    private var pendingIoRequestsCount = 0uL
    internal var totalLoaded = 0
        private set

    fun initialize() {}

    fun initializePostSettings(): Boolean {
        fileIoStore.initialize()
        if (bIsMultithreaded) Thread(this, "IoDispatcher").start()
        return true
    }

    fun allocRequest(chunkId: FIoChunkId, options: FIoReadOptions): FIoRequestImpl {
        val request = FIoRequestImpl()

        request.chunkId = chunkId
        request.options = options
        request.status = FIoStatus.UNKNOWN

        return request
    }

    fun allocRequest(batch: FIoBatchImpl, chunkId: FIoChunkId, options: FIoReadOptions): FIoRequestImpl {
        val request = allocRequest(chunkId, options)

        request.batch = batch

        if (batch.headRequest == null) {
            batch.headRequest = request
            batch.tailRequest = request
        } else {
            batch.tailRequest!!.batchNextRequest = request
            batch.tailRequest = request
        }

        check(batch.tailRequest!!.batchNextRequest == null)
        batch.unfinishedRequestsCount.incrementAndGet()

        return request
    }

    fun allocBatch() = FIoBatchImpl()

    fun freeBatch(batch: FIoBatchImpl?) {
        if (batch != null) {
            var request = batch.headRequest

            while (request != null) {
                val tmp = request
                request = request.batchNextRequest
//                freeRequest(tmp)
            }

//            batchAllocator.destroy(batch)
        }
    }

    fun onNewWaitingRequestsAdded() {
        if (bIsMultithreaded) {
            eventQueue.dispatcherNotify()
        } else {
            processIncomingRequests()
            while (pendingIoRequestsCount > 0u) {
                processCompletedRequests()
            }
        }
    }

    fun readWithCallback(chunkId: FIoChunkId, options: FIoReadOptions, priority: EIoDispatcherPriority, callback: FIoReadCallback) {
        val request = allocRequest(chunkId, options)
        request.callback = callback
        request.priority = priority
        request.nextRequest = null
        synchronized(waitingLock) {
            if (waitingRequestsTail == null) {
                waitingRequestsHead = request
                waitingRequestsTail = request
            } else {
                waitingRequestsTail!!.nextRequest = request
                waitingRequestsTail = request
            }
        }
        onNewWaitingRequestsAdded()
    }

    fun mount(environment: FIoStoreEnvironment, encryptionKeyGuid: FGuid, encryptionKey: ByteArray?) {
        val containerId = fileIoStore.mount(environment, encryptionKeyGuid, encryptionKey)
        val mountedContainer = FIoDispatcherMountedContainer(environment, containerId)
        containerMountedListeners.forEach { it.onContainerMounted(mountedContainer) }
        mountedContainers.add(mountedContainer)
    }

    fun doesChunkExist(chunkId: FIoChunkId) = fileIoStore.doesChunkExist(chunkId)

    fun getSizeForChunk(chunkId: FIoChunkId) =
        // Only attempt to find the size if the FIoChunkId is valid
        if (chunkId.isValid()) {
            fileIoStore.getSizeForChunk(chunkId)
        } else {
            throw FIoStatusException(EIoErrorCode.InvalidParameter, "FIoChunkId is not valid")
        }

    fun iterateBatch(batch: FIoBatchImpl, callbackFunction: (FIoRequestImpl) -> Boolean) {
        var request = batch.headRequest

        while (request != null) {
            val bDoContinue = callbackFunction(request)

            request = if (bDoContinue) request.batchNextRequest else null
        }
    }

    fun issueBatch(batch: FIoBatchImpl, priority: EIoDispatcherPriority) {
        synchronized(waitingLock) {
            if (waitingRequestsHead == null) {
                waitingRequestsHead = batch.headRequest
            } else {
                waitingRequestsTail!!.nextRequest = batch.headRequest
            }
            waitingRequestsTail = batch.tailRequest
            var request = batch.headRequest
            while (request != null) {
                request.nextRequest = request.batchNextRequest
                request.priority = priority
                request = request.batchNextRequest
            }
        }
        onNewWaitingRequestsAdded()
    }

    fun setupBatchForContiguousRead(batch: FIoBatchImpl, targetVa: ByteArray?, callback: FIoReadCallback?) {
        // Create the buffer
        var totalSize = 0uL
        var request = batch.headRequest
        while (request != null) {
            try {
                totalSize += min(getSizeForChunk(request.chunkId), request.options.size)
            } catch (ignored: FIoStatusException) {
            }
            request = request.batchNextRequest
        }

        // Set up memory buffers
        batch.ioBuffer = targetVa ?: ByteArray(totalSize.toInt())

        val dstBuffer = batch.ioBuffer

        // Now assign to each request
        val ptr = dstBuffer
        var ptrOff = 0
        var request1 = batch.headRequest
        while (request1 != null) {
            if (request1.options.targetVa != null) {
                throw FIoStatusException(EIoErrorCode.InvalidParameter, "A FIoBatch reading to a contiguous buffer cannot contain FIoRequests that have a TargetVa")
            }

            request1.options.targetVa = ptr
            request1.options.targetVaOff = ptrOff

            try {
                ptrOff += min(getSizeForChunk(request1.chunkId), request1.options.size).toInt()
            } catch (ignored: FIoStatusException) {
            }
            request1 = request1.batchNextRequest
        }

        // Set up callback
        batch.callback = callback
    }

    private fun processCompletedRequests() {
        var completedRequestsHead = fileIoStore.getCompletedRequests()
        while (completedRequestsHead != null) {
            val nextRequest = completedRequestsHead.nextRequest
            completeRequest(completedRequestsHead)
            completedRequestsHead = nextRequest
            --pendingIoRequestsCount
        }
    }

    private fun completeRequest(request: FIoRequestImpl) {
        if (!request.status.isCompleted) {
            if (request.bFailed) {
                request.status = FIoStatus(EIoErrorCode.ReadError)
            } else {
                request.status = FIoStatus(EIoErrorCode.Ok)
            }
        }
        if (request.callback != null) {
            if (request.status.isOk) {
                request.callback!!(Result.success(request.ioBuffer))
                totalLoaded += request.options.size.toInt()
            } else {
                request.callback!!(Result.failure(request.status.toException()))
            }
        }

        request.batch?.apply {
            check(unfinishedRequestsCount.get() > 0)
            if (unfinishedRequestsCount.decrementAndGet() == 0) {
                invokeCallback(this)
            }
        } // else freeRequest(request)
    }

    private fun invokeCallback(batch: FIoBatchImpl) {
        if (batch.callback == null) {
            // No point checking if the batch does not have a callback
            return
        }

        // If there is no valid tail request then it should not have been possible to call this method
        check(batch.tailRequest != null)

        // Since the requests will be processed in order we can just check the tail request
        check(batch.tailRequest!!.status.isCompleted)

        var status = FIoStatus(EIoErrorCode.Ok)
        // Check the requests in the batch to see if we need to report an error status
        var request = batch.headRequest
        while (request != null && status.isOk) {
            status = request.status
            request = request.batchNextRequest
        }

        // Return the buffer if there are no errors, or the failed status if there were
        if (status.isOk) {
            totalLoaded += batch.ioBuffer.size
            batch.callback!!(Result.success(batch.ioBuffer))
        } else {
            batch.callback!!(Result.failure(status.toException()))
        }
    }

    private fun processIncomingRequests() {
        var requestsToSubmitHead: FIoRequestImpl? = null
        var requestsToSubmitTail: FIoRequestImpl? = null
        while (true) {
            synchronized(waitingLock) {
                if (waitingRequestsHead != null) {
                    if (requestsToSubmitTail != null) {
                        requestsToSubmitTail!!.nextRequest = waitingRequestsHead
                        requestsToSubmitTail = waitingRequestsTail
                    } else {
                        requestsToSubmitHead = waitingRequestsHead
                        requestsToSubmitTail = waitingRequestsTail
                    }
                    waitingRequestsHead = null
                    waitingRequestsTail = null
                }
            }
            if (requestsToSubmitHead == null) {
                return
            }

            val request = requestsToSubmitHead!!
            requestsToSubmitHead = requestsToSubmitHead!!.nextRequest
            if (requestsToSubmitHead == null) {
                requestsToSubmitTail = null
            }

            // Make sure that the FIoChunkId in the request is valid before we try to do anything with it.
            if (request.chunkId.isValid()) {
                val result = fileIoStore.resolve(request)
                if (result != IoStoreResolveResult_OK) {
                    request.status = result.toStatus()
                    completeRequest(request) // MOD: if the given chunk ID does not exist, don't forget to complete the request
                    continue
                }
            } else {
                request.status = FIoStatus(EIoErrorCode.InvalidParameter, "FIoChunkId is not valid")
                completeRequest(request) // MOD: same with above
                continue
            }

            ++pendingIoRequestsCount
            request.nextRequest = null

            processCompletedRequests()
        }
    }

    //private fun init() = true

    override fun run() {
        //FMemory.setupTLSCachesOnCurrentThread()
        while (!bStopRequested.get()) {
            if (pendingIoRequestsCount > 0u) {
                eventQueue.dispatcherWaitForIo()
            } else {
                eventQueue.dispatcherWait()
            }
            processIncomingRequests()
            processCompletedRequests()
        }
    }

    fun stop() {
        bStopRequested.set(true)
        eventQueue.dispatcherNotify()
    }
}

/** A utility function to convert a EIoStoreResolveResult to the corresponding FIoStatus. */
fun EIoStoreResolveResult.toStatus(): FIoStatus {
    return when (this) {
        IoStoreResolveResult_OK -> FIoStatus(EIoErrorCode.Ok)
        IoStoreResolveResult_NotFound -> FIoStatus(EIoErrorCode.NotFound)
        else -> FIoStatus(EIoErrorCode.Unknown)
    }
}

class FIoDirectoryIndexHandle private constructor(val handle: UInt) {
    companion object {
        val INVALID_HANDLE = 0u.inv()
        val ROOT_HANDLE = 0u

        @JvmStatic
        fun fromIndex(index: UInt) = FIoDirectoryIndexHandle(index)

        @JvmStatic
        fun rootDirectory() = FIoDirectoryIndexHandle(ROOT_HANDLE)

        @JvmStatic
        fun invalid() = FIoDirectoryIndexHandle(INVALID_HANDLE)
    }

    fun isValid() = handle != INVALID_HANDLE

    operator fun compareTo(other: FIoDirectoryIndexHandle) = handle.compareTo(other.handle)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FIoDirectoryIndexHandle

        if (handle != other.handle) return false

        return true
    }

    override fun hashCode() = handle.hashCode()

    fun toIndex() = handle
}

interface FIoDirectoryIndexReader {
    fun getMountPoint(): String
    fun getChildDirectory(directory: FIoDirectoryIndexHandle): FIoDirectoryIndexHandle
    fun getNextDirectory(directory: FIoDirectoryIndexHandle): FIoDirectoryIndexHandle
    fun getFile(directory: FIoDirectoryIndexHandle): FIoDirectoryIndexHandle
    fun getNextFile(directory: FIoDirectoryIndexHandle): FIoDirectoryIndexHandle
    fun getDirectoryName(directory: FIoDirectoryIndexHandle): String
    fun getFileName(file: FIoDirectoryIndexHandle): String
    fun getFileData(file: FIoDirectoryIndexHandle): UInt
}

class FIoStoreTocChunkInfo(
    val id: FIoChunkId,
    val hash: FIoChunkHash,
    val offset: ULong,
    val size: ULong,
    val compressedSize: ULong,
    val bForceUncompressed: Boolean,
    val bIsMemoryMapped: Boolean,
    val bIsCompressed: Boolean
)