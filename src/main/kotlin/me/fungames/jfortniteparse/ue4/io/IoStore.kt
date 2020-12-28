package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.compression.Compression
import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.util.align
import java.io.File
import java.io.FileNotFoundException
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

/**
 * I/O store container format version
 */
enum class EIoStoreTocVersion {
    Invalid,
    Initial,
    DirectoryIndex
}

const val IO_CONTAINER_FLAG_COMPRESSED = 0x1
const val IO_CONTAINER_FLAG_ENCRYPTED = 0x2
const val IO_CONTAINER_FLAG_SIGNED = 0x3
const val IO_CONTAINER_FLAG_INDEXED = 0x4

/**
 * I/O Store TOC header.
 */
class FIoStoreTocHeader {
    companion object {
        val TOC_MAGIC_IMG = "-==--==--==--==-"
    }

    var tocMagic = ByteArray(16)
    var version: EIoStoreTocVersion
    var tocHeaderSize: UInt
    var tocEntryCount: UInt
    var tocCompressedBlockEntryCount: UInt
    var tocCompressedBlockEntrySize: UInt // For sanity checking
    var compressionMethodNameCount: UInt
    var compressionMethodNameLength: UInt
    var compressionBlockSize: UInt
    var directoryIndexSize: ULong
    var containerId: FIoContainerId
    var encryptionKeyGuid: FGuid
    var containerFlags: Int //EIoContainerFlags
    //var pad: ByteArray // uint8[60]

    constructor(Ar: FArchive) {
        Ar.read(tocMagic)
        if (!checkMagic())
            throw ParserException("Invalid utoc magic")
        version = EIoStoreTocVersion.values()[Ar.readInt32()]
        tocHeaderSize = Ar.readUInt32()
        tocEntryCount = Ar.readUInt32()
        tocCompressedBlockEntryCount = Ar.readUInt32()
        tocCompressedBlockEntrySize = Ar.readUInt32()
        compressionMethodNameCount = Ar.readUInt32()
        compressionMethodNameLength = Ar.readUInt32()
        compressionBlockSize = Ar.readUInt32()
        directoryIndexSize = Ar.readUInt64()
        containerId = FIoContainerId(Ar)
        encryptionKeyGuid = FGuid(Ar)
        containerFlags = Ar.readInt32()
        Ar.skip(60)
        //pad = Ar.read(60)
    }

    fun makeMagic() {
        tocMagic = TOC_MAGIC_IMG.toByteArray()
    }

    fun checkMagic() = tocMagic.contentEquals(TOC_MAGIC_IMG.toByteArray())
}

/**
 * Combined offset and length.
 */
class FIoOffsetAndLength {
    // We use 5 bytes for offset and size, this is enough to represent
    // an offset and size of 1PB
    private var offsetAndLength: UByteArray

    constructor() {
        offsetAndLength = UByteArray(5 + 5)
    }

    constructor(Ar: FArchive) {
        offsetAndLength = Ar.read(5 + 5).toUByteArray()
    }

    var offset: ULong
        get() = (offsetAndLength[4].toULong() or
                (offsetAndLength[3].toULong() shl 8) or
                (offsetAndLength[2].toULong() shl 16) or
                (offsetAndLength[1].toULong() shl 24) or
                (offsetAndLength[0].toULong() shl 32))
        set(value) {
            offsetAndLength[0] = (value shr 32).toUByte()
            offsetAndLength[1] = (value shr 24).toUByte()
            offsetAndLength[2] = (value shr 16).toUByte()
            offsetAndLength[3] = (value shr 8).toUByte()
            offsetAndLength[4] = (value shr 0).toUByte()
        }
    var length: ULong
        get() = (offsetAndLength[9].toULong() or
                (offsetAndLength[8].toULong() shl 8) or
                (offsetAndLength[7].toULong() shl 16) or
                (offsetAndLength[6].toULong() shl 24) or
                (offsetAndLength[5].toULong() shl 32))
        set(value) {
            offsetAndLength[5] = (value shr 32).toUByte()
            offsetAndLength[6] = (value shr 24).toUByte()
            offsetAndLength[7] = (value shr 16).toUByte()
            offsetAndLength[8] = (value shr 8).toUByte()
            offsetAndLength[9] = (value shr 0).toUByte()
        }
}

const val IO_STORE_TOC_ENTRY_META_FLAG_COMPRESSED = 1 shl 0
const val IO_STORE_TOC_ENTRY_META_FLAG_MEMORY_MAPPED = 1 shl 1

/**
 * TOC entry meta data
 */
class FIoStoreTocEntryMeta {
    var chunkHash: FIoChunkHash
    var flags: UByte

    constructor(Ar: FArchive) {
        chunkHash = FIoChunkHash(Ar)
        flags = Ar.readUInt8()
    }
}

/**
 * Compression block entry.
 */
class FIoStoreTocCompressedBlockEntry {
    companion object {
        val OffsetBits = 40u
        val OffsetMask = ((1L shl OffsetBits.toInt()) - 1L).toULong()
        val SizeBits = 24u
        val SizeMask = ((1 shl SizeBits.toInt()) - 1).toUInt()
        val SizeShift = 8u
    }

    /* 5 bytes offset, 3 bytes for size / uncompressed size and 1 byte for compresseion method. */
    val data = ByteArray(5 + 3 + 3 + 1)

    constructor(Ar: FArchive) {
        Ar.read(data)
    }

    var offset: ULong
        get() {
            val offset = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).long.toULong()
            return offset and OffsetMask
        }
        set(value) {
            /*val l = (value and OffsetMask).toLong()
            ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).putLong(l)*/
            throw NotImplementedError()
        }

    var compressedSize: UInt
        get() {
            val size = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).apply { position(1 * 4 /*+ 1*/) }.int.toUInt()
            return (size shr SizeShift.toInt()) and SizeMask
        }
        set(value) {
            throw NotImplementedError()
        }

    var uncompressedSize: UInt
        get() {
            val uncompressedSize = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).apply { position(2 * 4 /*+ 2*/) }.int.toUInt()
            return uncompressedSize and SizeMask
        }
        set(value) {
            throw NotImplementedError()
        }

    var compressionMethodIndex: UByte
        get() {
            val index = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).apply { position(2 * 4 /*+ 2*/) }.int.toUInt()
            return (index shr SizeBits.toInt()).toUByte()
        }
        set(value) {
            throw NotImplementedError()
        }
}

class FIoStoreToc {
    private val chunkIdToIndex = mutableMapOf<FIoChunkId, Int>()
    private val toc = FIoStoreTocResource()
    private lateinit var filesToIndex: Array<String>
    private lateinit var fileTocEntryIndices: IntArray

    fun initialize() {
        chunkIdToIndex.clear()

        for (chunkIndex in 0 until toc.chunkIds.size) {
            chunkIdToIndex[toc.chunkIds[chunkIndex]] = chunkIndex
        }
    }

    val tocResource get() = toc
    fun getTocEntryIndex(chunkId: FIoChunkId) = chunkIdToIndex[chunkId]
    fun getOffsetAndLength(chunkId: FIoChunkId) = chunkIdToIndex[chunkId]?.let { toc.chunkOffsetLengths[it] }
}

// class FIoStoreWriterImpl

class FIoStoreReaderImpl {
    private val toc = FIoStoreToc()
    private var decryptionKey: ByteArray? = null
    private lateinit var containerFileHandle: RandomAccessFile
    var directoryIndexReader: FIoDirectoryIndexReaderImpl? = null
        private set
    private val threadBuffers = object : ThreadLocal<FThreadBuffers>() {
        override fun initialValue() = FThreadBuffers()
    }
    lateinit var environment: FIoStoreEnvironment //custom, original code does not retain this

    fun initialize(environment: FIoStoreEnvironment, decryptionKeys: Map<FGuid, ByteArray>) {
        this.environment = environment
        val containerFile = File(environment.path + ".ucas")
        try {
            containerFileHandle = RandomAccessFile(containerFile, "r")
        } catch (e: FileNotFoundException) {
            throw FIoStatusException(EIoErrorCode.FileOpenFailed, "Failed to open IoStore container file '$containerFile'")
        }
        val tocResource = toc.tocResource
        tocResource.read(File(environment.path + ".utoc"), TOC_READ_OPTION_READ_ALL)

        toc.initialize()

        if ((tocResource.header.containerFlags and IO_CONTAINER_FLAG_ENCRYPTED) != 0) {
            decryptionKey = decryptionKeys[tocResource.header.encryptionKeyGuid]
                ?: throw FIoStatusException(EIoErrorCode.FileOpenFailed, "Missing decryption key for IoStore container file '$containerFile'")
        }

        if ((tocResource.header.containerFlags and IO_CONTAINER_FLAG_INDEXED) != 0 && tocResource.directoryIndexBuffer != null) {
            directoryIndexReader = FIoDirectoryIndexReaderImpl(tocResource.directoryIndexBuffer!!, decryptionKey)
        }
    }

    val containerId get() = toc.tocResource.header.containerId
    val containerFlags get() = toc.tocResource.header.containerFlags
    val encryptionKeyGuid get() = toc.tocResource.header.encryptionKeyGuid

    fun enumerateChunks(callback: (FIoStoreTocChunkInfo) -> Boolean) {
        val tocResource = toc.tocResource

        for (chunkIndex in 0 until tocResource.chunkIds.size) {
            val chunkInfo = getTocChunkInfo(chunkIndex)
            if (!callback(chunkInfo)) {
                break
            }
        }
    }

    fun getChunkInfo(chunkId: FIoChunkId): FIoStoreTocChunkInfo {
        val tocEntryIndex = toc.getTocEntryIndex(chunkId)!!
        if (tocEntryIndex > 0) {
            return getTocChunkInfo(tocEntryIndex)
        } else {
            throw FIoStatusException(EIoErrorCode.NotFound, "Not found")
        }
    }

    fun getChunkInfo(tocEntryIndex: Int): FIoStoreTocChunkInfo {
        val tocResource = toc.tocResource

        if (tocEntryIndex < tocResource.chunkIds.size) {
            return getTocChunkInfo(tocEntryIndex)
        } else {
            throw FIoStatusException(EIoErrorCode.InvalidParameter, "Invalid TocEntryIndex")
        }
    }

    fun read(chunkId: FIoChunkId/*, options: FIoReadOptions = FIoReadOptions()*/): ByteArray {
        val offsetAndLength = toc.getOffsetAndLength(chunkId)
            ?: throw FIoStatusException(EIoErrorCode.NotFound, "Unknown chunk ID")

        val threadBuffers = threadBuffers.get()
        val tocResource = toc.tocResource
        val compressionBlockSize = tocResource.header.compressionBlockSize
        val firstBlockIndex = (offsetAndLength.offset / compressionBlockSize).toInt()
        val lastBlockIndex = ((align(offsetAndLength.offset + offsetAndLength.length, compressionBlockSize.toULong()) - 1u) / compressionBlockSize).toInt()
        var offsetInBlock = offsetAndLength.offset % compressionBlockSize
        val dst = ByteArray(offsetAndLength.length.toInt())
        var dstOff = 0uL
        var remainingSize = offsetAndLength.length
        for (blockIndex in firstBlockIndex..lastBlockIndex) {
            val compressionBlock = tocResource.compressionBlocks[blockIndex]
            val rawSize = align(compressionBlock.compressedSize, Aes.BLOCK_SIZE.toUInt())
            if (threadBuffers.compressedBuffer == null || threadBuffers.compressedBuffer!!.size.toUInt() < rawSize) {
                threadBuffers.compressedBuffer = ByteArray(rawSize.toInt())
            }
            val uncompressedSize = compressionBlock.uncompressedSize
            if (threadBuffers.uncompressedBuffer == null || threadBuffers.uncompressedBuffer!!.size.toUInt() < uncompressedSize) {
                threadBuffers.uncompressedBuffer = ByteArray(uncompressedSize.toInt())
            }
            synchronized(containerFileHandle) {
                containerFileHandle.seek(compressionBlock.offset.toLong())
                containerFileHandle.read(threadBuffers.compressedBuffer, 0, rawSize.toInt())
            }
            if ((tocResource.header.containerFlags and IO_CONTAINER_FLAG_ENCRYPTED) != 0) {
                Aes.decryptData(threadBuffers.compressedBuffer!!, 0, rawSize.toInt(), decryptionKey!!)
            }
            val src = if (compressionBlock.compressionMethodIndex == 0u.toUByte()) {
                threadBuffers.compressedBuffer!!
            } else {
                val compressionMethod = tocResource.compressionMethods[compressionBlock.compressionMethodIndex.toInt()]
                try {
                    Compression.uncompressMemory(compressionMethod, threadBuffers.uncompressedBuffer!!, 0, uncompressedSize.toInt(), threadBuffers.compressedBuffer!!, 0, compressionBlock.compressedSize.toInt())
                    threadBuffers.uncompressedBuffer!!
                } catch (e: Exception) {
                    throw FIoStatusException(EIoErrorCode.CorruptToc, "Failed uncompressing block", e)
                }
            }
            val sizeInBlock = min(compressionBlockSize - offsetInBlock, remainingSize)
            System.arraycopy(src, offsetInBlock.toInt(), dst, dstOff.toInt(), sizeInBlock.toInt())
            offsetInBlock = 0u
            remainingSize -= sizeInBlock
            dstOff += sizeInBlock
        }
        return dst
    }

    fun getFilenamesByBlockIndex(inBlockIndexList: List<Int>, outFileList: MutableList<String>) {
        val directoryIndex = directoryIndexReader

        directoryIndex?.iterateDirectoryIndex(FIoDirectoryIndexHandle.rootDirectory(), "") { filename, tocEntryIndex ->
            for (blockIndex in inBlockIndexList) {
                if (tocChunkContainsBlockIndex(tocEntryIndex.toInt(), blockIndex)) {
                    //if (filename !in outFileList) {
                    outFileList.add(filename)
                    //}
                    break
                }
            }

            true
        }
    }

    fun getFiles() : List<GameFile> {
        val files = ArrayList<GameFile>()
        directoryIndexReader?.iterateDirectoryIndex(FIoDirectoryIndexHandle.rootDirectory(), "") { filename, tocEntryIndex ->
            files.add(GameFile(filename, pakFileName = environment.path,ioPackageId = FPackageId(toc.tocResource.chunkIds[tocEntryIndex.toInt()].chunkId)))
            true
        }
        return files
    }

    fun getFileNames(outFileList: MutableList<String>) {
        val directoryIndex = directoryIndexReader

        directoryIndex?.iterateDirectoryIndex(FIoDirectoryIndexHandle.rootDirectory(), "") { filename, tocEntryIndex ->
            //if (filename !in outFileList) {
            outFileList.add(filename)
            //}
            true
        }
    }

    fun tocChunkContainsBlockIndex(tocEntryIndex: Int, blockIndex: Int): Boolean {
        val tocResource = toc.tocResource
        val offsetLength = tocResource.chunkOffsetLengths[tocEntryIndex]

        val compressionBlockSize = tocResource.header.compressionBlockSize
        val firstBlockIndex = (offsetLength.offset / compressionBlockSize).toInt()
        val lastBlockIndex = ((align(offsetLength.offset + offsetLength.length, compressionBlockSize.toULong()) - 1u) / compressionBlockSize).toInt()

        return blockIndex in firstBlockIndex..lastBlockIndex
    }

    private fun getTocChunkInfo(tocEntryIndex: Int): FIoStoreTocChunkInfo {
        val tocResource = toc.tocResource
        val meta = tocResource.chunkMetas[tocEntryIndex]
        val offsetLength = tocResource.chunkOffsetLengths[tocEntryIndex]

        val bIsContainerCompressed = (tocResource.header.containerFlags and IO_CONTAINER_FLAG_COMPRESSED) != 0

        return FIoStoreTocChunkInfo(
            id = tocResource.chunkIds[tocEntryIndex],
            hash = meta.chunkHash,
            bIsCompressed = (meta.flags.toInt() and IO_STORE_TOC_ENTRY_META_FLAG_COMPRESSED) != 0,
            bIsMemoryMapped = (meta.flags.toInt() and IO_STORE_TOC_ENTRY_META_FLAG_MEMORY_MAPPED) != 0,
            bForceUncompressed = bIsContainerCompressed && (meta.flags.toInt() and IO_STORE_TOC_ENTRY_META_FLAG_COMPRESSED) == 0,
            offset = offsetLength.offset,
            size = offsetLength.length,
            compressedSize = getCompressedSize(tocResource.chunkIds[tocEntryIndex], tocResource, offsetLength)
        )
    }

    private fun getCompressedSize(chunkId: FIoChunkId, tocResource: FIoStoreTocResource, offsetLength: FIoOffsetAndLength): ULong {
        val compressionBlockSize = tocResource.header.compressionBlockSize
        val firstBlockIndex = (offsetLength.offset / compressionBlockSize).toInt()
        val lastBlockIndex = ((align(offsetLength.offset + offsetLength.length, compressionBlockSize.toULong()) - 1u) / compressionBlockSize).toInt()

        var compressedSize = 0uL
        for (blockIndex in firstBlockIndex..lastBlockIndex) {
            val compressionBlock = tocResource.compressionBlocks[blockIndex]
            compressedSize += compressionBlock.compressedSize
        }

        return compressedSize
    }

    class FThreadBuffers {
        var compressedBuffer: ByteArray? = null
        var uncompressedBuffer: ByteArray? = null
    }
}

const val TOC_READ_OPTION_READ_DIRECTORY_INDEX = 1 shl 0
const val TOC_READ_OPTION_READ_TOC_META = 1 shl 1
const val TOC_READ_OPTION_READ_ALL = TOC_READ_OPTION_READ_DIRECTORY_INDEX or TOC_READ_OPTION_READ_TOC_META

class FIoStoreTocResource {
    companion object {
        const val COMPRESSION_METHOD_NAME_LEN = 32
    }

    lateinit var header: FIoStoreTocHeader
    lateinit var chunkIds: MutableList<FIoChunkId>
    lateinit var chunkOffsetLengths: MutableList<FIoOffsetAndLength>
    lateinit var compressionBlocks: MutableList<FIoStoreTocCompressedBlockEntry>
    lateinit var compressionMethods: MutableList<FName>
    lateinit var chunkBlockSignatures: MutableList<ByteArray> // FSHAHash
    lateinit var chunkMetas: MutableList<FIoStoreTocEntryMeta>
    var directoryIndexBuffer: ByteArray? = null

    fun read(tocFile: File, readOptions: Int) {
        val tocBuffer = FByteArchive(tocFile.readBytes()) // RandomAccessFile is slow for this purpose
        header = FIoStoreTocHeader(tocBuffer)

        // Chunk IDs
        chunkIds = MutableList(header.tocEntryCount.toInt()) { FIoChunkId(tocBuffer) }

        // Chunk offsets
        chunkOffsetLengths = MutableList(header.tocEntryCount.toInt()) { FIoOffsetAndLength(tocBuffer) }

        // Compression blocks
        compressionBlocks = MutableList(header.tocCompressedBlockEntryCount.toInt()) { FIoStoreTocCompressedBlockEntry(tocBuffer) }

        // Compression methods
        compressionMethods = ArrayList(header.compressionMethodNameCount.toInt() + 1)
        compressionMethods.add(FName.NAME_None)
        for (i in 0u until header.compressionMethodNameCount) {
            compressionMethods.add(FName.dummy(String(tocBuffer.read(header.compressionMethodNameLength.toInt()), Charsets.US_ASCII).trimEnd('\u0000')))
        }

        // Chunk block signatures
        if (header.containerFlags and IO_CONTAINER_FLAG_SIGNED != 0) {
            val hashSize = tocBuffer.readInt32()
            tocBuffer.skip(hashSize.toLong()) // actually: var tocSignature = reader.ReadBytes(hashSize);
            tocBuffer.skip(hashSize.toLong()) // actually: var blockSignature = reader.ReadBytes(hashSize);
            chunkBlockSignatures = MutableList(header.tocCompressedBlockEntryCount.toInt()) { tocBuffer.read(20) }

            // You could verify hashes here but nah
        } else {
            chunkBlockSignatures = ArrayList(0)
        }

        // Directory index
        if (header.version >= EIoStoreTocVersion.DirectoryIndex
            && (readOptions and TOC_READ_OPTION_READ_DIRECTORY_INDEX) != 0
            && (header.containerFlags and IO_CONTAINER_FLAG_INDEXED) != 0
            && header.directoryIndexSize > 0u) {
            directoryIndexBuffer = tocBuffer.read(header.directoryIndexSize.toInt())
        }

        // Meta
        chunkMetas = if ((readOptions and TOC_READ_OPTION_READ_TOC_META) != 0) {
            MutableList(header.tocEntryCount.toInt()) { FIoStoreTocEntryMeta(tocBuffer) }
        } else {
            ArrayList(0)
        }
    }
}