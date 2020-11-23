package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.io.EIoStoreResolveResult.IoStoreResolveResult_OK
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicBoolean

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

interface FIoRequest {
    val status: FIoStatus
    val result: Result<ByteArray>
}

typealias FIoReadCallback = (Result<ByteArray>) -> Unit

enum class EIoDispatcherPriority(val value: Int) {
    IoDispatcherPriority_Min(Int.MIN_VALUE),
    IoDispatcherPriority_Low(Int.MIN_VALUE / 2),
    IoDispatcherPriority_Medium(0),
    IoDispatcherPriority_High(Int.MAX_VALUE / 2),
    IoDispatcherPriority_Max(Int.MAX_VALUE)
}

/**
 * I/O batch
 *
 * This is a primitive used to group I/O requests for synchronization
 * purposes
 */
class FIoBatch {
    private var dispatcher: FIoDispatcherImpl
    internal var headRequest: FIoRequestImpl? = null
    internal var tailRequest: FIoRequestImpl? = null

    internal constructor(dispatcher: FIoDispatcherImpl) {
        this.dispatcher = dispatcher
    }

    constructor(other: FIoBatch) : this(other.dispatcher) {
        headRequest = other.headRequest
        other.headRequest = null
    }

    fun read(chunkId: FIoChunkId, options: FIoReadOptions, priority: Int): FIoRequest =
        readInternal(chunkId, options, priority)

    fun readWithCallback(chunkId: FIoChunkId, options: FIoReadOptions, priority: Int, callback: FIoReadCallback): FIoRequest =
        readInternal(chunkId, options, priority).also { it.callback = callback }

    fun issue() {
        dispatcher.issueBatch(this)
    }

    fun issue(priority: Int) {
        var request = headRequest
        while (request != null) {
            request.priority = priority
            request = request.nextRequest
        }
        issue()
    }

    fun issueWithCallback(callback: () -> Unit) {
        dispatcher.issueBatchWithCallback(this, callback)
    }

    fun issueAndTriggerEvent(event: CompletableFuture<*>) {
        dispatcher.issueBatchAndTriggerEvent(this, event)
    }

    /*fun issueAndDispatchSubsequents(event: FGraphEventRef) {
        dispatcher.issueBatchAndDispatchSubsequents(this, event)
    }*/

    private fun readInternal(chunkId: FIoChunkId, options: FIoReadOptions, priority: Int): FIoRequestImpl {
        val request = dispatcher.allocRequest(chunkId, options)
        request.priority = priority
        //request.addRef()
        if (headRequest == null) {
            check(tailRequest == null)
            headRequest = request
            tailRequest = request
        } else {
            check(tailRequest != null)
            tailRequest!!.nextRequest = request
            tailRequest = request
        }
        return request
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
    internal val impl = FIoDispatcherImpl(false /*FGenericPlatformProcess.supportsMultithreading()*/)

    fun mount(environment: FIoStoreEnvironment, encryptionKeyGuid: FGuid, encryptionKey: ByteArray?) =
        impl.mount(environment, encryptionKeyGuid, encryptionKey)

    fun newBatch() = FIoBatch(impl)

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

    private val fileIoStore = FFileIoStore(eventQueue, true /*bIsMultithreaded*/)
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

    fun allocRequest(chunkId: FIoChunkId, options: FIoReadOptions) = FIoRequestImpl(this, chunkId, options)

    fun allocBatch() = FIoBatchImpl()

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

    fun mount(environment: FIoStoreEnvironment, encryptionKeyGuid: FGuid, encryptionKey: ByteArray?): FIoDispatcherMountedContainer {
        val containerId = fileIoStore.mount(environment, encryptionKeyGuid, encryptionKey)
        val mountedContainer = FIoDispatcherMountedContainer(environment, containerId)
        containerMountedListeners.forEach { it.onContainerMounted(mountedContainer) }
        mountedContainers.add(mountedContainer)
        return mountedContainer
    }

    fun doesChunkExist(chunkId: FIoChunkId) = fileIoStore.doesChunkExist(chunkId)

    fun getSizeForChunk(chunkId: FIoChunkId) =
        // Only attempt to find the size if the FIoChunkId is valid
        if (chunkId.isValid()) {
            fileIoStore.getSizeForChunk(chunkId)
        } else {
            throw FIoStatusException(EIoErrorCode.InvalidParameter, "FIoChunkId is not valid")
        }

    fun issueBatchInternal(batch: FIoBatch, batchImpl: FIoBatchImpl?) {
        if (batch.headRequest == null) {
            if (batchImpl != null) {
                completeBatch(batchImpl)
            }
            return
        }
        check(batch.tailRequest != null)
        var requestCount = 0
        var request = batch.headRequest
        while (request != null) {
            request.batch = batchImpl
            request = request.nextRequest
            ++requestCount
        }
        batchImpl?.unfinishedRequestsCount?.addAndGet(requestCount)
        synchronized(waitingLock) {
            if (waitingRequestsHead == null) {
                waitingRequestsHead = batch.headRequest
            } else {
                waitingRequestsTail!!.nextRequest = batch.headRequest
            }
            waitingRequestsTail = batch.tailRequest
        }
        batch.headRequest = null
        batch.tailRequest = null
        onNewWaitingRequestsAdded()
    }

    fun issueBatch(batch: FIoBatch) {
        issueBatchInternal(batch, null)
    }

    fun issueBatchWithCallback(batch: FIoBatch, callback: () -> Unit) {
        val impl = allocBatch()
        impl.callback = callback
        issueBatchInternal(batch, impl)
    }

    fun issueBatchAndTriggerEvent(batch: FIoBatch, event: CompletableFuture<*>) {
        val impl = allocBatch()
        impl.event = event
        issueBatchInternal(batch, impl)
    }

    /*fun issueBatchAndDispatchSubsequents(batch: FIoBatch, event: FGraphEventRef) {
        val impl = allocBatch()
        impl.graphEvent = graphEvent
        issueBatchInternal(batch, impl)
    }*/

    private fun processCompletedRequests() {
        var completedRequestsHead = fileIoStore.getCompletedRequests()
        while (completedRequestsHead != null) {
            val nextRequest = completedRequestsHead.nextRequest
            if (completedRequestsHead.bFailed) {
                completeRequest(completedRequestsHead, EIoErrorCode.ReadError)
            } else {
                //totalLoaded += completedRequestsHead.ioBuffer.size
                completeRequest(completedRequestsHead, EIoErrorCode.Ok)
            }
            //completedRequestsHead.releaseRef()
            completedRequestsHead = nextRequest
            --pendingIoRequestsCount
        }
    }

    private fun completeBatch(batch: FIoBatchImpl) {
        batch.callback?.invoke()
        batch.event?.complete(null)
        /*if (batch.graphEvent != null) {
            val newTasks = mutableListOf<FBaseGraphTask>()
            batch.graphEvent.dispatchSubsequents(newTasks)
        }
        batchAllocator.destroy(batch)*/
    }

    private fun completeRequest(request: FIoRequestImpl, status: EIoErrorCode): Boolean {
        val expectedStatus = EIoErrorCode.Unknown
        if (!request.errorCode.compareAndSet(expectedStatus, status)) {
            return false
        }

        request.callback?.invoke(if (status == EIoErrorCode.Ok)
            Result.success(request.ioBuffer)
        else
            Result.failure(FIoStatusException(status)))
        request.batch?.also {
            check(it.unfinishedRequestsCount.get() > 0)
            if (it.unfinishedRequestsCount.decrementAndGet() == 0) {
                completeBatch(it)
            }
        }
        return true
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
                    completeRequest(request, EIoErrorCode.NotFound)
                    //request.releaseRef()
                    continue
                }
            } else {
                completeRequest(request, EIoErrorCode.InvalidParameter)
                //request.releaseRef()
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