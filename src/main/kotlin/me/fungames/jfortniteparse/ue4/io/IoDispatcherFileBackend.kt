@file:Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS")

package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.ue4.io.EIoDispatcherPriority.IoDispatcherPriority_Count
import me.fungames.jfortniteparse.ue4.io.EIoStoreResolveResult.IoStoreResolveResult_NotFound
import me.fungames.jfortniteparse.ue4.io.EIoStoreResolveResult.IoStoreResolveResult_OK
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileNotFoundException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.min

val LOG_IO_DISPATCHER: Logger = LoggerFactory.getLogger("IoDispatcher")

class FFileIoStoreCompressionContext {
    var next: FFileIoStoreCompressionContext? = null
    var uncompressedBufferSize = 0uL
    var uncompressedBuffer: BytePointer? = null
}

class FFileIoStoreEncryptionKeys {
    fun interface FKeyRegisteredCallback {
        fun onKeyRegistered(guid: FGuid, key: ByteArray)
    }

    private val encryptionKeysByGuid = ConcurrentHashMap<FGuid, ByteArray>()
    private var keyRegisteredCallback: FKeyRegisteredCallback? = null

    fun getEncryptionKey(guid: FGuid): ByteArray? {
        return encryptionKeysByGuid[guid]
        // retrieve key from core delegate, not implemented here
    }

    fun setKeyRegisteredCallback(callback: FKeyRegisteredCallback) {
        keyRegisteredCallback = callback
    }

    private fun registerEncryptionKey(guid: FGuid, key: ByteArray) {
        encryptionKeysByGuid[guid] = key
        keyRegisteredCallback?.onKeyRegistered(guid, key)
    }
}

class FFileIoStoreReader(val platformImpl: FFileIoStoreImpl) {
    val toc = mutableMapOf<FIoChunkId, FIoOffsetAndLength>()
    val containerFile = FFileIoStoreContainerFile()
    lateinit var containerId: FIoContainerId
    var index = 0u
    var order = 0

    fun initialize(environment: FIoStoreEnvironment) {
        LOG_IO_DISPATCHER.info("Reading toc: ${environment.path}")

        val containerFilePath = environment.path + ".ucas"
        val tocFilePath = environment.path + ".utoc"

        try {
            containerFile.fileHandle = platformImpl.openContainer(containerFilePath)
            containerFile.fileSize = containerFile.fileHandle.length().toULong()
        } catch (e: FileNotFoundException) {
            throw FIoStatusException(EIoErrorCode.FileOpenFailed, "Failed to open IoStore container file '$containerFilePath'")
        }

        containerFile.filePath = containerFilePath

        val tocResource = FIoStoreTocResource()
        tocResource.read(File(tocFilePath), 0)
        val containerUncompressedSize = if (tocResource.header.tocCompressedBlockEntryCount > 0u) {
            tocResource.header.tocCompressedBlockEntryCount.toULong() * tocResource.header.compressionBlockSize.toULong()
        } else {
            containerFile.fileSize
        }

        toc.clear()

        for (chunkIndex in 0 until tocResource.header.tocEntryCount.toInt()) {
            val chunkOffsetLength = tocResource.chunkOffsetLengths[chunkIndex]
            if (chunkOffsetLength.offset + chunkOffsetLength.length > containerUncompressedSize) {
                throw FIoStatusException(EIoErrorCode.CorruptToc, "TOC TocEntry out of container bounds while reading '$tocFilePath'")
            }

            toc[tocResource.chunkIds[chunkIndex]] = chunkOffsetLength
        }

        for (compressedBlockEntry in tocResource.compressionBlocks) {
            if (compressedBlockEntry.offset + compressedBlockEntry.compressedSize > containerFile.fileSize) {
                throw FIoStatusException(EIoErrorCode.CorruptToc, "TOC TocCompressedBlockEntry out of container bounds while reading '$tocFilePath'")
            }
        }

        containerFile.apply {
            compressionMethods = tocResource.compressionMethods
            compressionBlockSize = tocResource.header.compressionBlockSize.toULong()
            compressionBlocks = tocResource.compressionBlocks
            containerFlags = tocResource.header.containerFlags
            encryptionKeyGuid = tocResource.header.encryptionKeyGuid
            blockSignatureHashes = tocResource.chunkBlockSignatures
        }

        containerId = tocResource.header.containerId
        order = environment.order
    }

    fun doesChunkExist(chunkId: FIoChunkId) = toc[chunkId] != null
    fun getSizeForChunk(chunkId: FIoChunkId) = toc[chunkId]?.length ?: throw FIoStatusException(EIoErrorCode.NotFound)
    fun resolve(chunkId: FIoChunkId) = toc[chunkId]
    val isEncrypted get() = (containerFile.containerFlags and IO_CONTAINER_FLAG_ENCRYPTED) != 0
    val isSigned get() = (containerFile.containerFlags and IO_CONTAINER_FLAG_SIGNED) != 0
    val encryptionKeyGuid get() = containerFile.encryptionKeyGuid
    var encryptionKey: ByteArray?
        get() = containerFile.encryptionKey
        set(key) {
            containerFile.encryptionKey = key
        }
}

class FFileIoStore : Runnable {
    companion object {
        @JvmStatic
        fun isValidEnvironment(environment: FIoStoreEnvironment) =
            File(environment.path + ".utoc").exists()
    }

    private var readBufferSize = 0uL
    private val eventQueue: FIoDispatcherEventQueue
    private val bufferAllocator = FFileIoStoreBufferAllocator()
    private val requestQueue = FFileIoStoreRequestQueue()
    private val platformImpl: FFileIoStoreImpl
    private val bIsMultithreaded: Boolean
    private val bStopRequested = AtomicBoolean(false)
    private val ioStoreReadersLock = Object()
    private val unorderedIoStoreReaders = mutableListOf<FFileIoStoreReader>()
    private val orderedIoStoreReaders = mutableListOf<FFileIoStoreReader>()
    private var firstFreeCompressionContext: FFileIoStoreCompressionContext? = null
    private val pendingRequestsCritical = Object()
    private val pendingRequests = FFileIoStoreReadRequestList()
    private val blockMapsByPrority = Array(IoDispatcherPriority_Count.ordinal) { FBlockMaps() } // Epic did the typo
    private var readyForDecompressionHead: FFileIoStoreCompressedBlock? = null
    private var readyForDecompressionTail: FFileIoStoreCompressedBlock? = null
    private val decompressedBlocksCritical = Object()
    private var firstDecompressedBlock: FFileIoStoreCompressedBlock? = null
    private val encryptionKeys = FFileIoStoreEncryptionKeys()
    private var completedRequestsHead: FIoRequestImpl? = null
    private var completedRequestsTail: FIoRequestImpl? = null
    private var submittedRequestsCount = 0u
    private var completedRequestsCount = 0u

    constructor(eventQueue: FIoDispatcherEventQueue, /*signatureErrorEvent: FIoSignatureErrorEvent,*/ bIsMultithreaded: Boolean) {
        this.eventQueue = eventQueue
        //this.signatureErrorEvent = signatureErrorEvent
        this.platformImpl = FFileIoStoreImpl(eventQueue, bufferAllocator/*, blockCache*/)
        this.bIsMultithreaded = bIsMultithreaded
        encryptionKeys.setKeyRegisteredCallback { guid, key ->
            synchronized(ioStoreReadersLock) {
                for (reader in unorderedIoStoreReaders) {
                    if (reader.isEncrypted && reader.encryptionKey == null && reader.encryptionKeyGuid == guid) {
                        LOG_IO_DISPATCHER.info("Updating container '%d' with encryption key guid '%s'", reader.containerId.value().toLong(), guid.toString())
                        reader.encryptionKey = key
                    }
                }
            }
        }
    }

    fun initialize() {
        readBufferSize = if (GIoDispatcherBufferSizeKB > 0) GIoDispatcherBufferSizeKB.toULong() shl 10 else 256uL shl 10

        val bufferMemorySize = GIoDispatcherBufferMemoryMB shl 20
        val bufferSize = GIoDispatcherBufferSizeKB shl 10
        //val bufferAlignment = GIoDispatcherBufferAlignment
        bufferAllocator.initialize(bufferMemorySize, bufferSize/*, bufferAlignment*/)

        //val cacheMemorySize = GIoDispatcherCacheSizeMB shl 20
        //blockCache.initialize(cacheMemorySize, bufferSize)

        val decompressionContextCount = if (GIoDispatcherDecompressionWorkerCount > 0) GIoDispatcherDecompressionWorkerCount else 4
        for (contextIndex in 0 until decompressionContextCount) {
            val context = FFileIoStoreCompressionContext()
            context.next = firstFreeCompressionContext
            firstFreeCompressionContext = context
        }

        Thread(this, "IoService").start()
    }

    fun mount(environment: FIoStoreEnvironment): FIoContainerId {
        val reader = FFileIoStoreReader(platformImpl)
        reader.initialize(environment)

        if (reader.isEncrypted) {
            val encryptionKey = encryptionKeys.getEncryptionKey(reader.encryptionKeyGuid)
            if (encryptionKey != null) {
                reader.encryptionKey = encryptionKey
            } else { // TODO GetBaseFilename
                LOG_IO_DISPATCHER.warn("Mounting container '%s' with invalid encryption key".format(environment.path.substringAfterLast(File.separatorChar)))
            }
        }

        var insertionIndex = 0
        val containerId = reader.containerId
        synchronized(ioStoreReadersLock) {
            reader.index = unorderedIoStoreReaders.size.toUInt()
            insertionIndex = orderedIoStoreReaders.upperBound(reader) { a, b ->
                if (a.order != b.order) {
                    a.order > b.order
                } else {
                    a.index > b.index
                }
            }
            unorderedIoStoreReaders.add(reader)
            orderedIoStoreReaders.add(insertionIndex, reader)
        }
        return containerId
    }

    fun resolve(request: FIoRequestImpl): EIoStoreResolveResult {
        synchronized(ioStoreReadersLock) {
            val resolvedRequest = FFileIoStoreResolvedRequest()
            resolvedRequest.request = request
            for (reader in orderedIoStoreReaders) {
                val offsetAndLength = reader.resolve(resolvedRequest.request.chunkId) ?: continue
                val requestedOffset = resolvedRequest.request.options.offset
                resolvedRequest.resolvedOffset = offsetAndLength.offset + requestedOffset
                if (requestedOffset > offsetAndLength.length) {
                    resolvedRequest.resolvedSize = 0u
                } else {
                    resolvedRequest.resolvedSize = min(resolvedRequest.request.options.size, offsetAndLength.length - requestedOffset)
                }

                request.unfinishedReadsCount = 0
                if (resolvedRequest.resolvedSize > 0u) {
                    val targetVa = request.options.targetVa
                    if (targetVa != null) {
                        resolvedRequest.request.ioBuffer = targetVa //FIoBuffer(FIoBuffer::Wrap, TargetVa, ResolvedRequest.ResolvedSize)
                    } else {
                        resolvedRequest.request.ioBuffer = BytePointer(resolvedRequest.resolvedSize.toInt())
                    }

                    val customRequests = FFileIoStoreReadRequestList()
                    if (platformImpl.createCustomRequests(reader.containerFile, resolvedRequest, customRequests)) {
                        synchronized(pendingRequestsCritical) {
                            pendingRequests.append(customRequests)
                        }
                        onNewPendingRequestsAdded()
                    } else {
                        readBlocks(reader, resolvedRequest)
                    }
                }

                return IoStoreResolveResult_OK
            }

            return IoStoreResolveResult_NotFound
        }
    }

    fun doesChunkExist(chunkId: FIoChunkId): Boolean {
        synchronized(ioStoreReadersLock) {
            for (reader in unorderedIoStoreReaders) {
                if (reader.doesChunkExist(chunkId)) {
                    return true
                }
            }
            return false
        }
    }

    fun getSizeForChunk(chunkId: FIoChunkId): ULong {
        synchronized(ioStoreReadersLock) {
            for (reader in orderedIoStoreReaders) {
                try {
                    return reader.getSizeForChunk(chunkId)
                } catch (ignored: FIoStatusException) {
                }
            }
            throw FIoStatusException(EIoErrorCode.NotFound)
        }
    }

    fun getCompletedRequests(): FIoRequestImpl? {
        if (!bIsMultithreaded) {
            while (platformImpl.startRequests(requestQueue));
        }

        val completedRequests = FFileIoStoreReadRequestList()
        platformImpl.getCompletedRequests(completedRequests)
        var completedRequest = completedRequests.head
        while (completedRequest != null) {
            ++completedRequestsCount

            val nextRequest = completedRequest.next

            if (completedRequest.immediateScatter.request == null) {
                check(completedRequest.buffer != null)
                val priority = completedRequest.priority
                check(priority < IoDispatcherPriority_Count)
                val blockMaps = blockMapsByPrority[priority.ordinal]

                blockMaps.rawBlocksMap.remove(completedRequest.key)

                for (compressedBlock in completedRequest.compressedBlocks) {
                    compressedBlock.bFailed = compressedBlock.bFailed || completedRequest.bFailed
                    if (compressedBlock.rawBlocksCount > 1u) {
                        if (compressedBlock.compressedDataBuffer == null) {
                            compressedBlock.compressedDataBuffer = ByteArray(compressedBlock.rawSize.toInt())
                        }

                        val src = completedRequest.buffer!!.memory
                        var srcOff = completedRequest.buffer!!.memory!!.pos
                        val dst = compressedBlock.compressedDataBuffer!!
                        var dstOff = 0
                        var copySize = completedRequest.size
                        val completedBlockOffsetInBuffer = completedRequest.offset.toLong() - compressedBlock.rawOffset.toLong()
                        if (completedBlockOffsetInBuffer < 0) {
                            srcOff -= completedBlockOffsetInBuffer.toInt()
                            copySize += completedBlockOffsetInBuffer.toULong()
                        } else {
                            dstOff += completedBlockOffsetInBuffer.toInt()
                        }
                        val compressedBlockRawEndOffset = compressedBlock.rawOffset + compressedBlock.rawSize
                        val completedBlockEndOffset = completedRequest.offset + completedRequest.size
                        if (completedBlockEndOffset > compressedBlockRawEndOffset) {
                            copySize -= completedBlockEndOffset - compressedBlockRawEndOffset
                        }
                        System.arraycopy(src, srcOff, dst, dstOff, copySize.toInt())
                        check(completedRequest.compressedBlocksRefCount > 0u)
                        --completedRequest.compressedBlocksRefCount
                    }

                    check(compressedBlock.unfinishedRawBlocksCount > 0u)
                    if (--compressedBlock.unfinishedRawBlocksCount == 0u) {
                        blockMaps.compressedBlocksMap.remove(compressedBlock.key)
                        if (readyForDecompressionTail == null) {
                            readyForDecompressionHead = compressedBlock
                            readyForDecompressionTail = compressedBlock
                        } else {
                            readyForDecompressionTail!!.next = compressedBlock
                            readyForDecompressionTail = compressedBlock
                        }
                        compressedBlock.next = null
                    }
                }
                if (completedRequest.compressedBlocksRefCount == 0u) {
                    freeBuffer(completedRequest.buffer!!)
                    //delete completedRequest
                }
            } else {
                check(completedRequest.buffer == null)
                val completedIoDispatcherRequest = completedRequest.immediateScatter.request!!
                completedIoDispatcherRequest.bFailed = completedIoDispatcherRequest.bFailed || completedRequest.bFailed
                //delete completedRequest
                check(completedIoDispatcherRequest.unfinishedReadsCount > 0)
                if (--completedIoDispatcherRequest.unfinishedReadsCount == 0) {
                    if (completedRequestsTail == null) {
                        completedRequestsHead = completedIoDispatcherRequest
                        completedRequestsTail = completedIoDispatcherRequest
                    } else {
                        completedRequestsTail!!.nextRequest = completedIoDispatcherRequest
                        completedRequestsTail = completedIoDispatcherRequest
                    }
                    completedRequestsTail!!.nextRequest = null
                }
            }

            completedRequest = nextRequest
        }

        var blockToReap: FFileIoStoreCompressedBlock?
        synchronized(decompressedBlocksCritical) {
            blockToReap = firstDecompressedBlock
            firstDecompressedBlock = null
        }

        while (blockToReap != null) {
            val next = blockToReap!!.next
            finalizeCompressedBlock(blockToReap!!)
            blockToReap = next
        }

        var blockToDecompress = readyForDecompressionHead
        while (blockToDecompress != null) {
            val next = blockToDecompress.next
            blockToDecompress.compressionContext = allocCompressionContext()
            if (blockToDecompress.compressionContext == null) {
                break
            }
            // Scatter block asynchronous when the block is compressed, encrypted or signed
            /*val bScatterAsync = bIsMultithreaded && (!BlockToDecompress.CompressionMethod.IsNone() || BlockToDecompress.EncryptionKey.IsValid() || BlockToDecompress.SignatureHash)
            if (bScatterAsync) {
                TGraphTask<FDecompressAsyncTask>::CreateTask().ConstructAndDispatchWhenReady(*this, BlockToDecompress)
            } else {*/
            scatterBlock(blockToDecompress, false)
            finalizeCompressedBlock(blockToDecompress)
            //}
            blockToDecompress = next
        }
        readyForDecompressionHead = blockToDecompress
        if (readyForDecompressionHead == null) {
            readyForDecompressionTail = null
        }

        val result = completedRequestsHead
        completedRequestsHead = null
        completedRequestsTail = null
        return result
    }

    fun init(): Boolean {
        return true
    }

    override fun run() {
        while (!bStopRequested.get()) {
            processIncomingRequests()
            if (!platformImpl.startRequests(requestQueue)) {
                //updateAsyncIOMinimumPriority()
                eventQueue.serviceWait()
            }
        }
    }

    fun stop() {
        bStopRequested.set(true)
        eventQueue.serviceNotify()
    }

    private fun onNewPendingRequestsAdded() {
        if (bIsMultithreaded) {
            eventQueue.serviceNotify()
        } else {
            processIncomingRequests()
        }
    }

    private fun readBlocks(reader: FFileIoStoreReader, resolvedRequest: FFileIoStoreResolvedRequest) {
        if (reader.isEncrypted && reader.encryptionKey == null) {
            LOG_IO_DISPATCHER.error("Reading from encrypted container (ID = '%d') with invalid encryption key (Guid = '%s')".format(reader.containerId.value().toInt(), reader.encryptionKeyGuid.toString()))
            return
        }
        val containerFile = reader.containerFile
        val compressionBlockSize = containerFile.compressionBlockSize
        val requestEndOffset = resolvedRequest.resolvedOffset + resolvedRequest.resolvedSize
        val requestBeginBlockIndex = (resolvedRequest.resolvedOffset / compressionBlockSize).toInt()
        val requestEndBlockIndex = ((requestEndOffset - 1u) / compressionBlockSize).toInt()

        val newBlocks = FFileIoStoreReadRequestList()

        var requestStartOffsetInBlock = resolvedRequest.resolvedOffset - requestBeginBlockIndex.toULong() * compressionBlockSize
        var requestRemainingBytes = resolvedRequest.resolvedSize
        var offsetInRequest = 0uL
        for (compressedBlockIndex in requestBeginBlockIndex..requestEndBlockIndex) {
            val compressedBlockKey = FFileIoStoreBlockKey()
            compressedBlockKey.fileIndex = reader.index
            compressedBlockKey.blockIndex = compressedBlockIndex.toUInt()
            val priority = resolvedRequest.request.priority
            val blockMaps = blockMapsByPrority[priority.ordinal]
            var compressedBlock = blockMaps.compressedBlocksMap[compressedBlockKey]
            if (compressedBlock == null) {
                compressedBlock = FFileIoStoreCompressedBlock()
                compressedBlock.key = compressedBlockKey
                compressedBlock.encryptionKey = reader.encryptionKey
                blockMaps.compressedBlocksMap[compressedBlockKey] = compressedBlock

                val bCacheable = offsetInRequest > 0u || requestRemainingBytes < compressionBlockSize

                val compressionBlockEntry = containerFile.compressionBlocks[compressedBlockIndex]
                compressedBlock.uncompressedSize = compressionBlockEntry.uncompressedSize
                compressedBlock.compressedSize = compressionBlockEntry.compressedSize
                compressedBlock.compressionMethod = containerFile.compressionMethods[compressionBlockEntry.compressionMethodIndex.toInt()]
                compressedBlock.signatureHash = if (reader.isSigned) containerFile.blockSignatureHashes[compressedBlockIndex] else null
                val rawOffset = compressionBlockEntry.offset
                val rawSize = align(compressionBlockEntry.compressedSize, Aes.BLOCK_SIZE.toUInt()) // The raw blocks size is always aligned to AES blocks size
                compressedBlock.rawOffset = rawOffset
                compressedBlock.rawSize = rawSize
                val rawBeginBlockIndex = (rawOffset / readBufferSize).toUInt()
                val rawEndBlockIndex = ((rawOffset + rawSize - 1u) / readBufferSize).toUInt()
                val rawBlockCount = rawEndBlockIndex - rawBeginBlockIndex + 1u
                compressedBlock.rawBlocksCount = rawBlockCount
                check(rawBlockCount > 0u)
                for (rawBlockIndex in rawBeginBlockIndex..rawEndBlockIndex) {
                    val rawBlockKey = FFileIoStoreBlockKey()
                    rawBlockKey.blockIndex = rawBlockIndex
                    rawBlockKey.fileIndex = reader.index

                    var rawBlock = blockMaps.rawBlocksMap[rawBlockKey]
                    if (rawBlock == null) {
                        rawBlock = FFileIoStoreReadRequest()
                        blockMaps.rawBlocksMap[rawBlockKey] = rawBlock

                        rawBlock.key = rawBlockKey
                        rawBlock.priority = priority
                        rawBlock.fileHandle = reader.containerFile.fileHandle
                        rawBlock.bIsCacheable = bCacheable
                        rawBlock.offset = rawBlockIndex * readBufferSize
                        val readSize = min(containerFile.fileSize, rawBlock.offset + readBufferSize) - rawBlock.offset
                        rawBlock.size = readSize
                        newBlocks.add(rawBlock)
                    }
                    if (rawBlockCount == 1u) {
                        compressedBlock.singleRawBlock = rawBlock
                    }
                    rawBlock.compressedBlocks.add(compressedBlock)
                    ++rawBlock.compressedBlocksRefCount
                    ++compressedBlock.unfinishedRawBlocksCount
                }
            }
            check(compressedBlock.uncompressedSize > requestStartOffsetInBlock)
            val requestSizeInBlock = min(compressedBlock.uncompressedSize - requestStartOffsetInBlock, requestRemainingBytes)
            check(offsetInRequest + requestSizeInBlock <= resolvedRequest.request.ioBuffer.size.toULong())
            check(requestStartOffsetInBlock + requestSizeInBlock <= compressedBlock.uncompressedSize)

            ++resolvedRequest.request.unfinishedReadsCount
            compressedBlock.scatterList.add(FFileIoStoreBlockScatter().apply {
                request = resolvedRequest.request
                dstOffset = offsetInRequest
                srcOffset = requestStartOffsetInBlock
                size = requestSizeInBlock
            })

            requestRemainingBytes -= requestSizeInBlock
            offsetInRequest += requestSizeInBlock
            requestStartOffsetInBlock = 0u
        }

        if (!newBlocks.isEmpty()) {
            synchronized(pendingRequestsCritical) {
                pendingRequests.append(newBlocks)
            }
            onNewPendingRequestsAdded()
        }
    }

    private fun freeBuffer(buffer: FFileIoStoreBuffer) {
        bufferAllocator.freeBuffer(buffer)
        eventQueue.serviceNotify()
    }

    private fun allocCompressionContext(): FFileIoStoreCompressionContext? {
        val result = firstFreeCompressionContext
        if (result != null) {
            firstFreeCompressionContext = firstFreeCompressionContext!!.next
        }
        return result
    }

    private fun freeCompressionContext(compressionContext: FFileIoStoreCompressionContext) {
        compressionContext.next = firstFreeCompressionContext
        firstFreeCompressionContext = compressionContext
    }

    private fun scatterBlock(compressedBlock: FFileIoStoreCompressedBlock, bIsAsync: Boolean) {
        val compressionContext = compressedBlock.compressionContext
        check(compressionContext != null)
        val compressedBuffer = if (compressedBlock.rawBlocksCount > 1u) {
            check(compressedBlock.compressedDataBuffer != null)
            BytePointer(compressedBlock.compressedDataBuffer!!)
        } else {
            val rawBlock = compressedBlock.singleRawBlock
            check(compressedBlock.rawOffset >= rawBlock.offset)
            val offsetInBuffer = compressedBlock.rawOffset - rawBlock.offset
            rawBlock.buffer!!.memory!! + offsetInBuffer.toInt()
        }
        /*if (CompressedBlock.SignatureHash)
        {
            FSHAHash BlockHash
            FSHA1::HashBuffer(CompressedBuffer, CompressedBlock.RawSize, BlockHash.Hash)
            if (*CompressedBlock.SignatureHash != BlockHash)
            {
                FIoSignatureError Error;
                {
                    FReadScopeLock _(IoStoreReadersLock)
                    const FFileIoStoreReader& Reader = *UnorderedIoStoreReaders[CompressedBlock.Key.FileIndex]
                    Error.ContainerName = FPaths::GetBaseFilename(Reader.GetContainerFile().FilePath)
                    Error.BlockIndex = CompressedBlock.Key.BlockIndex
                    Error.ExpectedHash = *CompressedBlock.SignatureHash
                    Error.ActualHash = BlockHash
                }

                UE_LOG(LogIoDispatcher, Warning, TEXT("Signature error detected in container '%s' at block index '%d'"), *Error.ContainerName, Error.BlockIndex)

                FScopeLock _(&SignatureErrorEvent.CriticalSection)
                if (SignatureErrorEvent.SignatureErrorDelegate.IsBound())
                {
                    SignatureErrorEvent.SignatureErrorDelegate.Broadcast(Error)
                }
            }
        }*/
        if (!compressedBlock.bFailed) {
            if (compressedBlock.encryptionKey != null/*.isValid()*/) {
                Aes.decryptData(compressedBuffer, compressedBlock.rawSize.toInt(), compressedBlock.encryptionKey!!)
            }
            val uncompressedBuffer: BytePointer
            if (compressedBlock.compressionMethod.isNone()) {
                uncompressedBuffer = compressedBuffer
            } else {
                if (compressionContext.uncompressedBufferSize < compressedBlock.uncompressedSize) {
                    //free(compressionContext.uncompressedBuffer)
                    compressionContext.uncompressedBuffer = BytePointer(compressedBlock.uncompressedSize.toInt())
                    compressionContext.uncompressedBufferSize = compressedBlock.uncompressedSize.toULong()
                }
                uncompressedBuffer = compressionContext.uncompressedBuffer!!

                try {
                    uncompressMemory(compressedBlock.compressionMethod, uncompressedBuffer, compressedBlock.uncompressedSize.toInt(), compressedBuffer, compressedBlock.compressedSize.toInt())
                } catch (e: Exception) {
                    LOG_IO_DISPATCHER.warn("Failed decompressing block", e)
                    compressedBlock.bFailed = true
                }
            }

            for (scatter in compressedBlock.scatterList) {
                System.arraycopy(uncompressedBuffer.asArray(), uncompressedBuffer.pos + scatter.srcOffset.toInt(), scatter.request!!.ioBuffer.asArray(), scatter.request!!.ioBuffer.pos + scatter.dstOffset.toInt(), scatter.size.toInt())
            }
        }

        if (bIsAsync) {
            synchronized(decompressedBlocksCritical) {
                compressedBlock.next = firstDecompressedBlock
                firstDecompressedBlock = compressedBlock

                eventQueue.dispatcherNotify()
            }
        }
    }

    private fun finalizeCompressedBlock(compressedBlock: FFileIoStoreCompressedBlock) {
        if (compressedBlock.rawBlocksCount > 1u) {
            check(compressedBlock.compressedDataBuffer != null)
            compressedBlock.compressedDataBuffer = null
        } else {
            val rawBlock = compressedBlock.singleRawBlock
            check(rawBlock.compressedBlocksRefCount > 0u)
            if (--rawBlock.compressedBlocksRefCount == 0u) {
                check(rawBlock.buffer != null)
                freeBuffer(rawBlock.buffer!!)
                //delete RawBlock
            }
        }
        check(compressedBlock.compressionContext != null)
        freeCompressionContext(compressedBlock.compressionContext!!)
        for (scatter in compressedBlock.scatterList) {
            scatter.request!!.bFailed == scatter.request!!.bFailed || compressedBlock.bFailed
            check(scatter.request!!.unfinishedReadsCount > 0)
            if (--scatter.request!!.unfinishedReadsCount == 0) {
                if (completedRequestsTail == null) {
                    completedRequestsHead = scatter.request
                    completedRequestsTail = scatter.request
                } else {
                    completedRequestsTail!!.nextRequest = scatter.request
                    completedRequestsTail = scatter.request
                }
                completedRequestsTail!!.nextRequest = null
            }
        }
        //delete CompressedBlock
    }

    private fun processIncomingRequests() {
        var requestToSchedule: FFileIoStoreReadRequest?
        synchronized(pendingRequestsCritical) {
            requestToSchedule = pendingRequests.head
            pendingRequests.clear()
        }

        while (requestToSchedule != null) {
            val nextRequest = requestToSchedule!!.next
            requestToSchedule!!.next = null
            requestQueue.push(requestToSchedule!!)
            ++submittedRequestsCount

            requestToSchedule = nextRequest
        }

        //updateAsyncIOMinimumPriority()
    }

    class FBlockMaps {
        val compressedBlocksMap = mutableMapOf<FFileIoStoreBlockKey, FFileIoStoreCompressedBlock>()
        val rawBlocksMap = mutableMapOf<FFileIoStoreBlockKey, FFileIoStoreReadRequest>()
    }
}