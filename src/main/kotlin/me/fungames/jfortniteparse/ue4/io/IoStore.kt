package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.compression.Compression
import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.pak.GameFile
import me.fungames.jfortniteparse.ue4.pak.PakFileReader
import me.fungames.jfortniteparse.ue4.pak.reader.FPakArchive
import me.fungames.jfortniteparse.ue4.pak.reader.FPakFileArchive
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE5_BASE
import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import me.fungames.jfortniteparse.util.align
import java.io.Closeable
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
    DirectoryIndex,
    PartitionSize
}

const val IO_CONTAINER_FLAG_COMPRESSED = 1 shl 0
const val IO_CONTAINER_FLAG_ENCRYPTED  = 1 shl 1
const val IO_CONTAINER_FLAG_SIGNED     = 1 shl 2
const val IO_CONTAINER_FLAG_INDEXED    = 1 shl 3

/**
 * I/O Store TOC header.
 */
class FIoStoreTocHeader {
    companion object {
        val TOC_MAGIC_IMG = "-==--==--==--==-"
    }

    var tocMagic = ByteArray(16)
    var version: EIoStoreTocVersion
    var reserved0: UByte
    var reserved1: UShort
    var tocHeaderSize: UInt
    var tocEntryCount: UInt
    var tocCompressedBlockEntryCount: UInt
    var tocCompressedBlockEntrySize: UInt // For sanity checking
    var compressionMethodNameCount: UInt
    var compressionMethodNameLength: UInt
    var compressionBlockSize: UInt
    var directoryIndexSize: UInt
    var partitionCount: UInt
    var containerId: FIoContainerId
    var encryptionKeyGuid: FGuid
    var containerFlags: Int //EIoContainerFlags
    var reserved3: UByte
    var reserved4: UShort
    var reserved5: UInt
    var partitionSize: ULong
    var reserved6: ULongArray //size: 6

    constructor(Ar: FArchive) {
        Ar.read(tocMagic)
        if (!checkMagic())
            throw ParserException("TOC header magic mismatch", Ar)
        version = EIoStoreTocVersion.values().getOrElse(Ar.read()) {
            val latest = EIoStoreTocVersion.values().last()
            PakFileReader.logger.warn("Unsupported TOC version $it, falling back to latest ($latest)")
            latest
        }
        reserved0 = Ar.readUInt8()
        reserved1 = Ar.readUInt16()
        tocHeaderSize = Ar.readUInt32()
        tocEntryCount = Ar.readUInt32()
        tocCompressedBlockEntryCount = Ar.readUInt32()
        tocCompressedBlockEntrySize = Ar.readUInt32()
        compressionMethodNameCount = Ar.readUInt32()
        compressionMethodNameLength = Ar.readUInt32()
        compressionBlockSize = Ar.readUInt32()
        directoryIndexSize = Ar.readUInt32()
        partitionCount = Ar.readUInt32()
        containerId = FIoContainerId(Ar)
        encryptionKeyGuid = FGuid(Ar)
        containerFlags = Ar.read()
        reserved3 = Ar.readUInt8()
        reserved4 = Ar.readUInt16()
        reserved5 = Ar.readUInt32()
        partitionSize = Ar.readUInt64()
        reserved6 = ULongArray(6) { Ar.readUInt64() }
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
        val OFFSET_BITS = 40u
        val OFFSET_MASK = ((1L shl OFFSET_BITS.toInt()) - 1L).toULong()
        val SIZE_BITS = 24u
        val SIZE_MASK = ((1 shl SIZE_BITS.toInt()) - 1).toUInt()
        val SIZE_SHIFT = 8u
    }

    /* 5 bytes offset, 3 bytes for size / uncompressed size and 1 byte for compresseion method. */
    val data = ByteArray(5 + 3 + 3 + 1)

    constructor(Ar: FArchive) {
        Ar.read(data)
    }

    var offset: ULong
        get() {
            val offset = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).long.toULong()
            return offset and OFFSET_MASK
        }
        set(value) {
            /*val l = (value and OffsetMask).toLong()
            ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).putLong(l)*/
            throw NotImplementedError()
        }

    var compressedSize: UInt
        get() {
            val size = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).apply { position(1 * 4 /*+ 1*/) }.int.toUInt()
            return (size shr SIZE_SHIFT.toInt()) and SIZE_MASK
        }
        set(value) {
            throw NotImplementedError()
        }

    var uncompressedSize: UInt
        get() {
            val uncompressedSize = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).apply { position(2 * 4 /*+ 2*/) }.int.toUInt()
            return uncompressedSize and SIZE_MASK
        }
        set(value) {
            throw NotImplementedError()
        }

    var compressionMethodIndex: UByte
        get() {
            val index = ByteBuffer.wrap(data).order(ByteOrder.LITTLE_ENDIAN).apply { position(2 * 4 /*+ 2*/) }.int.toUInt()
            return (index shr SIZE_BITS.toInt()).toUByte()
        }
        set(value) {
            throw NotImplementedError()
        }
}

// class FIoStoreWriterImpl

class FIoStoreReaderImpl(var versions: VersionContainer = VersionContainer.DEFAULT) : Closeable {
    var game: Int
        inline get() = versions.game
        set(value) {
            versions.game = value
        }
    var ver: Int
        inline get() = versions.ver
        set(value) {
            versions.ver = value
        }
    val tocResource = FIoStoreTocResource()
    private var decryptionKey: ByteArray? = null
    private val containerFileHandles = mutableListOf<FPakArchive>()
    private var _directoryIndexReader: FIoDirectoryIndexReader? = null
    private val directoryIndexReaderLock = Object()
    val directoryIndexReader: FIoDirectoryIndexReader? get() {
        synchronized(directoryIndexReaderLock) {
            if (_directoryIndexReader != null) {
                return _directoryIndexReader
            }
            if ((tocResource.header.containerFlags and IO_CONTAINER_FLAG_INDEXED) == 0 || tocResource.directoryIndexBuffer == null) {
                return null
            }
            if ((tocResource.header.containerFlags and IO_CONTAINER_FLAG_ENCRYPTED) != 0 && decryptionKey == null) {
                return null
            }
            val out = FIoDirectoryIndexReaderImpl(tocResource.directoryIndexBuffer!!, decryptionKey)
            tocResource.directoryIndexBuffer = null
            _directoryIndexReader = out
            return out
        }
    }
    private val threadBuffers = object : ThreadLocal<FThreadBuffers>() {
        override fun initialValue() = FThreadBuffers()
    }
    lateinit var environment: FIoStoreEnvironment //custom, original code does not retain this

    var concurrent = false

    fun initialize(utocReader: FArchive, ucasReader: FPakArchive, decryptionKeys: Map<FGuid, ByteArray>) {
        this.environment = FIoStoreEnvironment(ucasReader.fileName.substringBeforeLast('.'))
        tocResource.read(utocReader, TOC_READ_OPTION_READ_ALL)
        utocReader.close()
        if (tocResource.header.partitionCount > 1u) {
            throw FIoStatusException(EIoErrorCode.CorruptToc, "This method does not support IoStore environments with multiple partitions")
        }
        containerFileHandles.add(ucasReader)

        if ((tocResource.header.containerFlags and IO_CONTAINER_FLAG_ENCRYPTED) != 0) {
            decryptionKey = decryptionKeys[tocResource.header.encryptionKeyGuid]
                ?: throw FIoStatusException(EIoErrorCode.FileOpenFailed, "Missing decryption key for IoStore environment '${environment.path}'")
        }
    }

    fun initialize(environment: FIoStoreEnvironment, readOptions: Int, decryptionKeys: Map<FGuid, ByteArray>) {
        this.environment = environment
        val start = System.currentTimeMillis()
        tocResource.read(File(environment.path + ".utoc"), readOptions)
        for (partitionIndex in 0u until tocResource.header.partitionCount) {
            val containerFilePath = StringBuilder(256)
            containerFilePath.append(environment.path)
            if (partitionIndex > 0u) {
                containerFilePath.append("_s").append(partitionIndex.toInt())
            }
            containerFilePath.append(".ucas")
            val containerFile = File(containerFilePath.toString())
            try {
                containerFileHandles.add(FPakFileArchive(RandomAccessFile(containerFile, "r"), containerFile))
            } catch (e: FileNotFoundException) {
                throw FIoStatusException(EIoErrorCode.FileOpenFailed, "Failed to open IoStore container file '$containerFile'")
            }
        }

        if ((tocResource.header.containerFlags and IO_CONTAINER_FLAG_ENCRYPTED) != 0) {
            decryptionKey = decryptionKeys[tocResource.header.encryptionKeyGuid]
                ?: throw FIoStatusException(EIoErrorCode.FileOpenFailed, "Missing decryption key for IoStore environment '${environment.path}'")
        }

        PakFileReader.logger.info("IoStore \"{}\": {} {}, version {} in {}ms",
            environment.path,
            this.tocResource.header.tocEntryCount,
            if (decryptionKey != null) "encrypted chunks" else "chunks",
            this.tocResource.header.version.ordinal,
            System.currentTimeMillis() - start)
    }

    val containerId get() = tocResource.header.containerId
    val containerFlags get() = tocResource.header.containerFlags
    val encryptionKeyGuid get() = tocResource.header.encryptionKeyGuid

    fun enumerateChunks(callback: (FIoStoreTocChunkInfo) -> Boolean) {
        for (chunkIndex in tocResource.chunkIds.indices) {
            val chunkInfo = getTocChunkInfo(chunkIndex)
            if (!callback(chunkInfo)) {
                break
            }
        }
    }

    fun getChunkInfo(chunkId: FIoChunkId): FIoStoreTocChunkInfo {
        val tocEntryIndex = tocResource.getTocEntryIndex(chunkId)
        if (tocEntryIndex != null) {
            return getTocChunkInfo(tocEntryIndex)
        } else {
            throw FIoStatusException(EIoErrorCode.NotFound, "Not found")
        }
    }

    fun getChunkInfo(tocEntryIndex: Int): FIoStoreTocChunkInfo {
        if (tocEntryIndex < tocResource.chunkIds.size) {
            return getTocChunkInfo(tocEntryIndex)
        } else {
            throw FIoStatusException(EIoErrorCode.InvalidParameter, "Invalid TocEntryIndex")
        }
    }

    fun read(chunkId: FIoChunkId/*, options: FIoReadOptions = FIoReadOptions()*/): ByteArray {
        val offsetAndLength = tocResource.getOffsetAndLength(chunkId)
            ?: throw FIoStatusException(EIoErrorCode.NotFound, "Unknown chunk ID")

        val exContainerFileHandles = if (concurrent) containerFileHandles.map { it.clone() } else containerFileHandles

        val threadBuffers = threadBuffers.get()
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
            val partitionIndex = (compressionBlock.offset / tocResource.header.partitionSize).toInt()
            val partitionOffset = (compressionBlock.offset % tocResource.header.partitionSize).toLong()
            val exAr = exContainerFileHandles[partitionIndex]
            exAr.seek(partitionOffset)
            exAr.read(threadBuffers.compressedBuffer!!, 0, rawSize.toInt())
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
                    throw FIoStatusException(EIoErrorCode.CorruptToc, "Failed uncompressing block", exAr, e)
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

    private var _files: List<GameFile>? = null
    private val filesLock = Object()
    val files: List<GameFile> get() {
        synchronized(filesLock) {
            _files?.let { return it }
            val files = ArrayList<GameFile>()
            val exportBundleDataChunkType = (if (game >= GAME_UE5_BASE) EIoChunkType5.ExportBundleData else EIoChunkType.ExportBundleData).ordinal.toUByte()
            directoryIndexReader?.iterateDirectoryIndex(FIoDirectoryIndexHandle.rootDirectory(), "") { filename, tocEntryIndex ->
                val chunkId = tocResource.chunkIds[tocEntryIndex.toInt()]
                if (chunkId.chunkType == exportBundleDataChunkType) {
                    files.add(GameFile(filename, pakFileName = environment.path, ioChunkId = chunkId, ioStoreReader = this))
                }
                true
            }
            _files = files
            return files
        }
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
        val offsetLength = tocResource.chunkOffsetLengths[tocEntryIndex]

        val compressionBlockSize = tocResource.header.compressionBlockSize
        val firstBlockIndex = (offsetLength.offset / compressionBlockSize).toInt()
        val lastBlockIndex = ((align(offsetLength.offset + offsetLength.length, compressionBlockSize.toULong()) - 1u) / compressionBlockSize).toInt()

        return blockIndex in firstBlockIndex..lastBlockIndex
    }

    private fun getTocChunkInfo(tocEntryIndex: Int): FIoStoreTocChunkInfo {
        check(tocResource.chunkMetas.isNotEmpty()) { "TOC was read without ChunkMetas" }
        val meta = tocResource.chunkMetas[tocEntryIndex]
        val offsetLength = tocResource.chunkOffsetLengths[tocEntryIndex]

        val bIsContainerCompressed = (tocResource.header.containerFlags and IO_CONTAINER_FLAG_COMPRESSED) != 0

        val compressionBlockSize = tocResource.header.compressionBlockSize
        val firstBlockIndex = (offsetLength.offset / compressionBlockSize).toInt()
        val lastBlockIndex = ((align(offsetLength.offset + offsetLength.length, compressionBlockSize.toULong()) - 1u) / compressionBlockSize).toInt()

        var compressedSize = 0uL
        var partitionIndex = -1
        for (blockIndex in firstBlockIndex..lastBlockIndex) {
            val compressionBlock = tocResource.compressionBlocks[blockIndex]
            compressedSize += compressionBlock.compressedSize
            if (partitionIndex < 0) {
                partitionIndex = (compressionBlock.offset / tocResource.header.partitionSize).toInt()
            }
        }

        return FIoStoreTocChunkInfo(
            id = tocResource.chunkIds[tocEntryIndex],
            hash = meta.chunkHash,
            bIsCompressed = (meta.flags.toInt() and IO_STORE_TOC_ENTRY_META_FLAG_COMPRESSED) != 0,
            bIsMemoryMapped = (meta.flags.toInt() and IO_STORE_TOC_ENTRY_META_FLAG_MEMORY_MAPPED) != 0,
            bForceUncompressed = bIsContainerCompressed && (meta.flags.toInt() and IO_STORE_TOC_ENTRY_META_FLAG_COMPRESSED) == 0,
            offset = offsetLength.offset,
            size = offsetLength.length,
            compressedSize = compressedSize,
            partitionIndex = partitionIndex
        )
    }

    class FThreadBuffers {
        var compressedBuffer: ByteArray? = null
        var uncompressedBuffer: ByteArray? = null
    }

    override fun close() {
        containerFileHandles.forEach { it.close() }
    }
}

const val TOC_READ_OPTION_READ_DIRECTORY_INDEX = 1 shl 0
const val TOC_READ_OPTION_READ_TOC_META = 1 shl 1
const val TOC_READ_OPTION_READ_ALL = TOC_READ_OPTION_READ_DIRECTORY_INDEX or TOC_READ_OPTION_READ_TOC_META

/**
 * Container TOC data.
 */
class FIoStoreTocResource {
    companion object {
        const val COMPRESSION_METHOD_NAME_LEN = 32
    }

    lateinit var header: FIoStoreTocHeader
    lateinit var chunkIds: Array<FIoChunkId>
    lateinit var chunkOffsetLengths: Array<FIoOffsetAndLength>
    lateinit var compressionBlocks: Array<FIoStoreTocCompressedBlockEntry>
    lateinit var compressionMethods: Array<String>
    //lateinit var chunkBlockSignatures: Array<ByteArray> // FSHAHash
    lateinit var chunkMetas: Array<FIoStoreTocEntryMeta>
    var directoryIndexBuffer: ByteArray? = null
    private lateinit var chunkIdToIndex: MutableMap<FIoChunkId, Int>

    fun read(tocFile: File, readOptions: Int) = read(object : FByteArchive(tocFile.readBytes()) {
        override fun printError() = super.printError() + ", file $tocFile"
    }, readOptions)

    fun read(tocBuffer: FArchive, readOptions: Int) {
        // Header
        header = FIoStoreTocHeader(tocBuffer)

        if (header.tocHeaderSize != 144u /*sizeof(FIoStoreTocHeader)*/) {
            throw FIoStatusException(EIoErrorCode.CorruptToc, "TOC header size mismatch", tocBuffer)
        }

        if (header.tocCompressedBlockEntrySize != 12u /*sizeof(FIoStoreTocCompressedBlockEntry)*/) {
            throw FIoStatusException(EIoErrorCode.CorruptToc, "TOC compressed block entry size mismatch", tocBuffer)
        }

        if (header.version < EIoStoreTocVersion.DirectoryIndex) {
            throw FIoStatusException(EIoErrorCode.CorruptToc, "Outdated TOC header version", tocBuffer)
        }

        if (header.version < EIoStoreTocVersion.PartitionSize) {
            header.partitionCount = 1u
            header.partitionSize = ULong.MAX_VALUE
        }

        // Chunk IDs
        chunkIdToIndex = HashMap(header.tocEntryCount.toInt(), 1f)
        chunkIds = Array(header.tocEntryCount.toInt()) {
            val id = FIoChunkId(tocBuffer)
            chunkIdToIndex[id] = it
            id
        }

        // Chunk offsets
        chunkOffsetLengths = Array(header.tocEntryCount.toInt()) { FIoOffsetAndLength(tocBuffer) }

        // Compression blocks
        compressionBlocks = Array(header.tocCompressedBlockEntryCount.toInt()) { FIoStoreTocCompressedBlockEntry(tocBuffer) }

        // Compression methods
        compressionMethods = Array(header.compressionMethodNameCount.toInt() + 1) {
            if (it == 0) "None"
            else {
                val compressionMethodName = tocBuffer.read(header.compressionMethodNameLength.toInt())
                var length = 0
                while (compressionMethodName[length] != 0.toByte()) {
                    ++length
                }
                String(compressionMethodName, 0, length)
            }
        }

        // Chunk block signatures
        if (header.containerFlags and IO_CONTAINER_FLAG_SIGNED != 0) {
            val hashSize = tocBuffer.readInt32()
            /*var tocSignature = tocBuffer.read(hashSize)
            var blockSignature = tocBuffer.read(hashSize)
            chunkBlockSignatures = Array(header.tocCompressedBlockEntryCount.toInt()) { tocBuffer.read(20) }*/
            tocBuffer.skip(hashSize + hashSize + header.tocCompressedBlockEntryCount.toInt() * 20L)

            // You could verify hashes here but nah
        }/* else {
            chunkBlockSignatures = emptyArray()
        }*/

        // Directory index
        if (header.containerFlags and IO_CONTAINER_FLAG_INDEXED != 0 && header.directoryIndexSize > 0u) {
            if (readOptions and TOC_READ_OPTION_READ_DIRECTORY_INDEX != 0) {
                directoryIndexBuffer = tocBuffer.read(header.directoryIndexSize.toInt())
            } else {
                tocBuffer.skip(header.directoryIndexSize.toLong())
            }
        }

        // Meta
        chunkMetas = if ((readOptions and TOC_READ_OPTION_READ_TOC_META) != 0) {
            Array(header.tocEntryCount.toInt()) { FIoStoreTocEntryMeta(tocBuffer) }
        } else {
            emptyArray()
        }
    }

    fun getTocEntryIndex(chunkId: FIoChunkId) = chunkIdToIndex[chunkId]
    fun getOffsetAndLength(chunkId: FIoChunkId) = chunkIdToIndex[chunkId]?.let { chunkOffsetLengths[it] }
}