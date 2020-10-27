package me.fungames.jfortniteparse.ue4.io

import java.io.IOException
import java.io.RandomAccessFile

class FGenericIoDispatcherEventQueue {
    private val dispatcherEvent = Object()
    private val serviceEvent = Object()

    fun dispatcherNotify() {
        synchronized(dispatcherEvent) { dispatcherEvent.notify() }
    }

    fun dispatcherWait() {
        synchronized(dispatcherEvent) { dispatcherEvent.wait() }
    }

    fun dispatcherWaitForIo() {
        dispatcherWait()
    }

    fun serviceNotify() {
        synchronized(serviceEvent) { serviceEvent.notify() }
    }

    fun serviceWait() {
        synchronized(serviceEvent) { serviceEvent.wait() }
    }
}

class FGenericFileIoStoreImpl(
    private val eventQueue: FGenericIoDispatcherEventQueue,
    private val bufferAllocator: FFileIoStoreBufferAllocator,
    //private val blockCache: FFileIoStoreBlockCache
) {
    private val completedRequestsCritical = Object()
    private val completedRequests = FFileIoStoreReadRequestList()

    fun openContainer(containerFilePath: String): RandomAccessFile {
        return RandomAccessFile(containerFilePath, "r")
    }

    fun createCustomRequests(containerFile: FFileIoStoreContainerFile, resolvedRequest: FFileIoStoreResolvedRequest, outRequests: FFileIoStoreReadRequestList): Boolean {
        return false
    }

    fun startRequests(requestQueue: FFileIoStoreRequestQueue): Boolean {
        //LOG_IO_DISPATCHER.debug("startRequests()")
        val nextRequest = requestQueue.peek() ?: return false

        val dest = if (nextRequest.immediateScatter.request == null) {
            nextRequest.buffer = bufferAllocator.allocBuffer()
            if (nextRequest.buffer == null) {
                return false
            }
            nextRequest.buffer!!.memory!!
        } else {
            nextRequest.immediateScatter.request!!.ioBuffer + nextRequest.immediateScatter.dstOffset.toInt()
        }

        requestQueue.pop(nextRequest)

        //if (!blockCache.read(nextRequest)) { TODO block caching
        val fileHandle = nextRequest.fileHandle
        nextRequest.bFailed = true
        var retryCount = 0
        while (retryCount++ < 10) {
            try {
                fileHandle.seek(nextRequest.offset.toLong())
            } catch (e: IOException) {
                LOG_IO_DISPATCHER.warn("Failed seeking to offset %d (Retries: %d)".format(nextRequest.offset.toLong(), retryCount - 1), e)
                continue
            }
            try {
                fileHandle.read(dest.asArray(), dest.pos, nextRequest.size.toInt())
            } catch (e: IOException) {
                LOG_IO_DISPATCHER.warn("Failed reading %d bytes at offset %d (Retries: %d)".format(nextRequest.size.toLong(), nextRequest.offset.toLong(), retryCount - 1), e)
                continue
            }
            nextRequest.bFailed = false
            //blockCache.store(nextRequest)
            break
        }
        //}
        synchronized(completedRequestsCritical) {
            completedRequests.add(nextRequest)
        }
        eventQueue.dispatcherNotify()
        return true
    }

    fun getCompletedRequests(outRequests: FFileIoStoreReadRequestList) {
        synchronized(completedRequestsCritical) {
            outRequests.append(completedRequests)
            completedRequests.clear()
        }
    }
}
