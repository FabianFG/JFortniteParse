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
        val nextRequest = requestQueue.pop() ?: return false

        val dest: ByteArray
        val destOff: Int
        if (nextRequest.immediateScatter.request == null) {
            nextRequest.buffer = bufferAllocator.allocBuffer()
            if (nextRequest.buffer == null) {
                requestQueue.push(nextRequest)
                return false
            }
            dest = nextRequest.buffer!!.memory!!.asArray()
            destOff = nextRequest.buffer!!.memory!!.pos
        } else {
            dest = nextRequest.immediateScatter.request!!.ioBuffer
            destOff = nextRequest.immediateScatter.request!!.ioBufferOff + nextRequest.immediateScatter.dstOffset.toInt()
        }

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
                fileHandle.read(dest, destOff, nextRequest.size.toInt())
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
