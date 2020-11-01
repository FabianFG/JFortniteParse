package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_Count
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.kotlinPointers.BytePointer
import java.io.RandomAccessFile

class FFileIoStoreContainerFile {
    lateinit var fileHandle: RandomAccessFile
    var fileSize = 0uL
    var compressionBlockSize = 0uL
    lateinit var compressionMethods: List<FName>
    lateinit var compressionBlocks: List<FIoStoreTocCompressedBlockEntry>
    lateinit var filePath: String

    //var mappedFileHandle: TUniquePtr<IMappedFileHandle>
    lateinit var encryptionKeyGuid: FGuid
    var encryptionKey: ByteArray? = null
    var containerFlags = 0
    lateinit var blockSignatureHashes: List<ByteArray>
}

class FFileIoStoreBuffer {
    var next: FFileIoStoreBuffer? = null
    var memory: BytePointer? = null
    var priority = IoDispatcherPriority_Count
}

class FFileIoStoreBlockKey {
    var fileIndex = 0u
    var blockIndex = 0u
    var hash = 0uL

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FFileIoStoreBlockKey

        if (hash != other.hash) return false

        return true
    }

    override fun hashCode() = hash.hashCode()
}

class FFileIoStoreBlockScatter {
    var request: FIoRequestImpl? = null
    var dstOffset = 0uL
    var srcOffset = 0uL
    var size = 0uL
}

class FFileIoStoreCompressedBlock {
    var next: FFileIoStoreCompressedBlock? = null
    lateinit var key: FFileIoStoreBlockKey
    lateinit var compressionMethod: FName
    var rawOffset = 0uL
    var uncompressedSize = 0u
    var compressedSize = 0u
    var rawSize = 0u
    var rawBlocksCount = 0u
    var unfinishedRawBlocksCount = 0u
    lateinit var singleRawBlock: FFileIoStoreReadRequest
    val scatterList = mutableListOf<FFileIoStoreBlockScatter>()
    var compressionContext: FFileIoStoreCompressionContext? = null
    var compressedDataBuffer: ByteArray? = null
    var encryptionKey: ByteArray? = null // FAES::FAESKey
    var signatureHash: ByteArray? = null // FSHAHash
    var bFailed = false
}

class FFileIoStoreReadRequest {
    var next: FFileIoStoreReadRequest? = null
    lateinit var fileHandle: RandomAccessFile
    var offset = (-1).toULong()
    var size = (-1).toULong()
    lateinit var key: FFileIoStoreBlockKey
    var buffer: FFileIoStoreBuffer? = null
    val compressedBlocks = mutableListOf<FFileIoStoreCompressedBlock>()
    var compressedBlocksRefCount = 0u
    val immediateScatter = FFileIoStoreBlockScatter()
    var priority = IoDispatcherPriority_Count
    var bIsCacheable = false
    var bFailed = false
}

class FFileIoStoreReadRequestList {
    fun isEmpty() = head == null

    fun add(Request: FFileIoStoreReadRequest) {
        if (tail != null) {
            tail!!.next = Request
        } else {
            head = Request
        }
        tail = Request
        Request.next = null
    }

    fun append(ListHead: FFileIoStoreReadRequest, ListTail: FFileIoStoreReadRequest) {
        check(ListTail.next == null)
        if (tail != null) {
            tail!!.next = ListHead
        } else {
            head = ListHead
        }
        tail = ListTail
    }

    fun append(list: FFileIoStoreReadRequestList) {
        if (list.head != null) {
            append(list.head!!, list.tail!!)
        }
    }

    fun clear() {
        head = null
        tail = null
    }

    var head: FFileIoStoreReadRequest? = null
        private set
    var tail: FFileIoStoreReadRequest? = null
        private set
}

class FFileIoStoreResolvedRequest {
    lateinit var request: FIoRequestImpl
    var resolvedOffset = 0uL
    var resolvedSize = 0uL
}

class FFileIoStoreBufferAllocator {
    private lateinit var bufferMemory: BytePointer
    private val buffersCritical = Object()
    private var firstFreeBuffer: FFileIoStoreBuffer? = null

    fun initialize(memorySize: Int, bufferSize: Int/*, bufferAlignment: Int*/) {
        val bufferCount = memorySize / bufferSize
        val memorySize = bufferCount * bufferSize
        bufferMemory = BytePointer(memorySize)//reinterpret_cast<uint8*>(FMemory::Malloc(MemorySize, bufferAlignment))
        for (bufferIndex in 0 until bufferCount) {
            val buffer = FFileIoStoreBuffer()
            buffer.memory = bufferMemory + bufferIndex * bufferSize
            buffer.next = firstFreeBuffer
            firstFreeBuffer = buffer
        }
    }

    fun allocBuffer(): FFileIoStoreBuffer? {
        val buffer = firstFreeBuffer
        if (buffer != null) {
            firstFreeBuffer = buffer.next
            return buffer
        }
        return null
    }

    fun freeBuffer(buffer: FFileIoStoreBuffer) {
        synchronized(buffersCritical) {
            buffer.next = firstFreeBuffer
            firstFreeBuffer = buffer
        }
    }
}

class FFileIoStoreRequestQueue {
    private val byPriority = Array(IoDispatcherPriority_Count.ordinal) { FByPriority() }

    fun peek(): FFileIoStoreReadRequest? {
        for (priority in IoDispatcherPriority_Count.ordinal - 1 downTo 0) {
            val queue = byPriority[priority]
            if (queue.head != null) {
                return queue.head
            }
        }
        return null
    }

    fun pop(request: FFileIoStoreReadRequest) {
        check(request.priority < IoDispatcherPriority_Count)
        val queue = byPriority[request.priority.ordinal]
        check(queue.head == request)
        queue.head = queue.head!!.next
        if (queue.head == null) {
            queue.tail = null
        }
        request.next = null
    }

    fun push(request: FFileIoStoreReadRequest) {
        check(request.priority < IoDispatcherPriority_Count)
        val queue = byPriority[request.priority.ordinal]
        if (queue.tail != null) {
            queue.tail!!.next = request
            queue.tail = request
        } else {
            queue.head = request
            queue.tail = request
        }
        request.next = null
    }

    private class FByPriority {
        var head: FFileIoStoreReadRequest? = null
        var tail: FFileIoStoreReadRequest? = null
    }
}