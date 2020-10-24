package me.fungames.jfortniteparse.ue4.io

import java.util.concurrent.atomic.AtomicInteger

enum class EIoStoreResolveResult {
    IoStoreResolveResult_OK,
    IoStoreResolveResult_NotFound
}

class FIoBatchImpl {
    var headRequest: FIoRequestImpl? = null
    var tailRequest: FIoRequestImpl? = null

    // Used for contiguous reads
    lateinit var ioBuffer: BytePointer
    var callback: FIoReadCallback? = null
    val unfinishedRequestsCount = AtomicInteger()
}

class FIoRequestImpl : FIoRequest {
    var batch: FIoBatchImpl? = null
    var nextRequest: FIoRequestImpl? = null
    var batchNextRequest: FIoRequestImpl? = null
    override lateinit var status: FIoStatusException // TODO change to FIoStatus
    override lateinit var chunkId: FIoChunkId
    lateinit var options: FIoReadOptions
    lateinit var ioBuffer: BytePointer
    var callback: FIoReadCallback? = null
    var unfinishedReadsCount = 0
    lateinit var priority: EIoDispatcherPriority
    var bFailed = false

    override val isOk: Boolean
        get() = status.isOk

    override fun getResultOrThrow() =
        if (status.isOk) ioBuffer else throw status
}