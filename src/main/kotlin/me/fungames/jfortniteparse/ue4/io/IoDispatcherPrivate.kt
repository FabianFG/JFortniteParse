package me.fungames.jfortniteparse.ue4.io

import java.util.concurrent.CompletableFuture
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

typealias FIoDispatcherEventQueue = FGenericIoDispatcherEventQueue
typealias FFileIoStoreImpl = FGenericFileIoStoreImpl

enum class EIoStoreResolveResult {
    IoStoreResolveResult_OK,
    IoStoreResolveResult_NotFound
}

class FIoBatchImpl {
    var callback: (() -> Unit)? = null
    var event: CompletableFuture<*>? = null
    //var graphEvent: FGraphEventRef? = null
    val unfinishedRequestsCount = AtomicInteger()
}

class FIoRequestImpl : FIoRequest {
    val dispatcher: FIoDispatcherImpl
    var batch: FIoBatchImpl? = null
    var nextRequest: FIoRequestImpl? = null
    var chunkId: FIoChunkId
    var options: FIoReadOptions
    lateinit var ioBuffer: ByteArray
    var ioBufferOff = 0
    var callback: FIoReadCallback? = null
    var unfinishedReadsCount = 0
    var priority = EIoDispatcherPriority.IoDispatcherPriority_Medium
    val errorCode = AtomicReference(EIoErrorCode.Unknown)
    var bFailed = false

    constructor(dispatcher: FIoDispatcherImpl, chunkId: FIoChunkId, options: FIoReadOptions) {
        this.dispatcher = dispatcher
        this.chunkId = chunkId
        this.options = options
    }

    override val status get() = FIoStatus(errorCode.get())

    override val result get(): Result<ByteArray> {
        val status = FIoStatus(errorCode.get())
        check(status.isCompleted)
        return if (status.isOk) {
            Result.success(ioBuffer)
        } else {
            Result.failure(status.toException())
        }
    }
}