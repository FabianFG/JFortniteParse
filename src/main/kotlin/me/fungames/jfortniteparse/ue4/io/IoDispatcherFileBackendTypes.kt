package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.kotlinPointers.BytePointer
import java.io.RandomAccessFile
import java.util.*

class FFileIoStoreContainerFile {
    lateinit var fileHandle: RandomAccessFile
    var fileSize = 0uL
    var compressionBlockSize = 0uL
    lateinit var compressionMethods: List<String>
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
}

class FFileIoStoreBlockKey {
    var fileIndex = 0u
    var blockIndex = 0u

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FFileIoStoreBlockKey

        if (fileIndex != other.fileIndex) return false
        if (blockIndex != other.blockIndex) return false

        return true
    }

    override fun hashCode(): Int {
        var result = fileIndex.hashCode()
        result = 31 * result + blockIndex.hashCode()
        return result
    }
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
    lateinit var compressionMethod: String
    var rawOffset = 0uL
    var uncompressedSize = 0u
    var compressedSize = 0u
    var rawSize = 0u
    var unfinishedRawBlocksCount = 0u
    val rawBlocks = mutableListOf<FFileIoStoreReadRequest>()
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
    var sequence = 0u
    var priority = 0
    val immediateScatter = FFileIoStoreBlockScatter()
    var bIsCacheable = false
    var bFailed = false

    companion object {
        @JvmStatic var nextSequence = 0u
    }

    init {
        sequence = nextSequence++
    }
}

class FFileIoStoreReadRequestList {
    fun isEmpty() = head == null

    fun add(request: FFileIoStoreReadRequest) {
        if (tail != null) {
            tail!!.next = request
        } else {
            head = request
        }
        tail = request
        request.next = null
    }

    fun append(listHead: FFileIoStoreReadRequest, listTail: FFileIoStoreReadRequest) {
        check(listTail.next == null)
        if (tail != null) {
            tail!!.next = listHead
        } else {
            head = listHead
        }
        tail = listTail
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
        synchronized(buffersCritical) {
            val buffer = firstFreeBuffer
            if (buffer != null) {
                firstFreeBuffer = buffer.next
                return buffer
            }
            return null
        }
    }

    fun freeBuffer(buffer: FFileIoStoreBuffer) {
        synchronized(buffersCritical) {
            buffer.next = firstFreeBuffer
            firstFreeBuffer = buffer
        }
    }
}

class FFileIoStoreRequestQueue {
    private var heap = PriorityQueue<FFileIoStoreReadRequest> { a, b ->
        if (a.priority == b.priority) {
            a.sequence.compareTo(b.sequence)
        } else {
            b.priority - a.priority
        }
    }
    private val criticalSection = Object()

    fun peek(): FFileIoStoreReadRequest? {
        synchronized(criticalSection) {
            if (heap.isEmpty()) {
                return null
            }
            return heap.peek()
        }
    }

    fun pop(): FFileIoStoreReadRequest? {
        synchronized(criticalSection) {
            if (heap.isEmpty()) {
                return null
            }
            return heap.remove()
        }
    }

    fun push(request: FFileIoStoreReadRequest) {
        synchronized(criticalSection) {
            heap.add(request)
        }
    }

    fun push(requests: FFileIoStoreReadRequestList) {
        synchronized(criticalSection) {
            var request = requests.head
            while (request != null) {
                heap.add(request)
                request = request.next
            }
        }
    }

    fun updateOrder() {
        synchronized(criticalSection) {
            heap = PriorityQueue(heap)
        }
    }
}