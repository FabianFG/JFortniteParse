package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.reader.FArchive
import java.nio.ByteBuffer
import java.nio.ByteOrder

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

class FIoStatusException : ParserException {
    val status: FIoStatus

    constructor(status: FIoStatus, cause: Throwable? = null) : super(status.toString(), cause) {
        this.status = status
    }

    constructor(errorCode: EIoErrorCode, errorMessage: String = "", cause: Throwable? = null)
        : this(FIoStatus(errorCode, errorMessage), cause)

    constructor(status: FIoStatus, Ar: FArchive, cause: Throwable? = null) : super(status.toString(), Ar, cause) {
        this.status = status
    }

    constructor(errorCode: EIoErrorCode, errorMessage: String = "", Ar: FArchive, cause: Throwable? = null)
        : this(FIoStatus(errorCode, errorMessage), Ar, cause)
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

        private inline fun createEmptyId() = FIoChunkId(ByteArray(12))
    }

    val chunkId: ULong
        get() = ByteBuffer.wrap(id).order(ByteOrder.LITTLE_ENDIAN).long.toULong()

    /*private*/ var id = ByteArray(12)

    constructor(id: ByteArray) {
        check(id.size == 12)
        this.id = id
    }

    constructor(chunkId: ULong, chunkIndex: UShort, ioChunkType: EIoChunkType) : this(
        ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
            .putLong(chunkId.toLong())
            .putShort(chunkIndex.toShort())
            .put(11, ioChunkType.ordinal.toByte())
            .array())

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

class FIoDispatcherMountedContainer(
    val environment: FIoStoreEnvironment,
    val containerId: FIoContainerId
)

interface FOnContainerMountedListener {
    fun onContainerMounted(container: FIoDispatcherMountedContainer)
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

typealias FDirectoryIndexVisitorFunction = (filename: String, tocEntryIndex: UInt) -> Boolean

interface FIoDirectoryIndexReader {
    fun getMountPoint(): String
    fun getChildDirectory(directory: FIoDirectoryIndexHandle): FIoDirectoryIndexHandle
    fun getNextDirectory(directory: FIoDirectoryIndexHandle): FIoDirectoryIndexHandle
    fun getFile(directory: FIoDirectoryIndexHandle): FIoDirectoryIndexHandle
    fun getNextFile(directory: FIoDirectoryIndexHandle): FIoDirectoryIndexHandle
    fun getDirectoryName(directory: FIoDirectoryIndexHandle): String
    fun getFileName(file: FIoDirectoryIndexHandle): String
    fun getFileData(file: FIoDirectoryIndexHandle): UInt
    fun iterateDirectoryIndex(directory: FIoDirectoryIndexHandle, path: String, visit: FDirectoryIndexVisitorFunction): Boolean
}

class FIoStoreTocChunkInfo(
    val id: FIoChunkId,
    val hash: FIoChunkHash,
    val offset: ULong,
    val size: ULong,
    val compressedSize: ULong,
    val partitionIndex: Int,
    val bForceUncompressed: Boolean,
    val bIsMemoryMapped: Boolean,
    val bIsCompressed: Boolean
)