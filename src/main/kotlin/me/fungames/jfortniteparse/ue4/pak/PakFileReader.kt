package me.fungames.jfortniteparse.ue4.pak

import me.fungames.jfortniteparse.compression.Compression
import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.exceptions.InvalidAesKeyException
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_Latest
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_PathHashIndex
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_RelativeChunkOffsets
import me.fungames.jfortniteparse.ue4.pak.objects.FPakCompressedBlock
import me.fungames.jfortniteparse.ue4.pak.objects.FPakEntry
import me.fungames.jfortniteparse.ue4.pak.objects.FPakInfo
import me.fungames.jfortniteparse.ue4.pak.reader.FPakArchive
import me.fungames.jfortniteparse.ue4.pak.reader.FPakFileArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4_GET_AR_VER
import me.fungames.jfortniteparse.ue4.versions.LATEST_SUPPORTED_UE4_VERSION
import me.fungames.jfortniteparse.util.INDEX_NONE
import me.fungames.jfortniteparse.util.parseHexBinary
import me.fungames.jfortniteparse.util.toInt64
import me.fungames.jfortniteparse.util.toUInt32
import me.fungames.kotlinPointers.BytePointer
import mu.KotlinLogging
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.min


@ExperimentalUnsignedTypes
private typealias FPathHashIndex = Map<ULong, Int>

private typealias FPakDirectory = Map<String, Int>
private typealias FDirectoryIndex = Map<String, FPakDirectory>

@ExperimentalUnsignedTypes
class PakFileReader(val Ar : FPakArchive, val keepIndexData : Boolean = false) {

    constructor(file : File, game : Int = GAME_UE4(LATEST_SUPPORTED_UE4_VERSION)) : this(FPakFileArchive(RandomAccessFile(file, "r"), file).apply { this.game = game; this.ver = GAME_UE4_GET_AR_VER(game) })
    constructor(filePath : String, game : Int = GAME_UE4(LATEST_SUPPORTED_UE4_VERSION)) : this(File(filePath), game)

    var encodedPakEntries: ByteArray = byteArrayOf()
        private set

    var concurrent = false

    val fileName = Ar.fileName

    val pakInfo : FPakInfo = FPakInfo.readPakInfo(Ar)
    var aesKey : ByteArray? = null
        /**
         * Sets the aes key for this pak file after testing it
         * @throws InvalidAesKeyException if the given aes key is invalid
         */
        @Throws(InvalidAesKeyException::class)
        set(value) {
            if (value == null)
                return
            if (!testAesKey(value))
                throw InvalidAesKeyException("Given aes key '$value'is not working with '$fileName'")
            field = value
        }

    lateinit var mountPrefix : String

    var fileCount = 0
        private set
    var encryptedFileCount = 0
        private set
    lateinit var files : List<GameFile>
        private set

    var pathHashSeed = 0UL

    var hasPathHashIndex = false
    lateinit var pathHashIndex : FPathHashIndex

    var hasFullDirectoryIndex = false
    lateinit var directoryIndex : FDirectoryIndex

    init {
        if (pakInfo.version > PakVersion_Latest)
            logger.warn("Pak file \"$fileName\" has unsupported version ${pakInfo.version}")
        Ar.pakInfo = pakInfo
    }

    override fun toString() = fileName

    fun isEncrypted() = pakInfo.encryptedIndex

    fun extract(gameFile: GameFile) : ByteArray {
        require(gameFile.pakFileName == fileName) { "Wrong pak file reader, required ${gameFile.pakFileName}, this is $fileName" }
        logger.debug("Extracting ${gameFile.getName()} from $fileName at ${gameFile.pos} with size ${gameFile.size}")
        // If this reader is used as a concurrent reader create a clone of the main reader to
        // provide thread safety
        val exAr = if (concurrent) Ar.clone() else Ar
        exAr.seek(gameFile.pos)
        // Pak Entry is written before the file data,
        // but its the same as the one from the index, just without a name
        val tempEntry = FPakEntry(exAr, false)
        tempEntry.compressionBlocks.forEach {
            it.compressedStart += gameFile.pos
            it.compressedEnd += gameFile.pos
        }
        when {
            gameFile.isCompressed() -> {
                logger.debug("${gameFile.getName()} is compressed with ${gameFile.compressionMethod}")
                var data = ByteArray(0)
                tempEntry.compressionBlocks.forEach { block ->
                    exAr.seek(block.compressedStart)
                    var srcSize = (block.compressedEnd - block.compressedStart).toInt()
                    // Read the compressed block
                    val src = if (gameFile.isEncrypted) {
                        // The compressed block is encrypted, align it and then decrypt
                        val key = aesKey ?: throw ParserException("Decrypting a encrypted file requires an aes key to be set")
                        while (srcSize % Aes.BLOCK_SIZE != 0)
                            srcSize++
                        val encrypted = exAr.read(srcSize)
                        var decrypted = Aes.decrypt(encrypted, key)
                        if (decrypted.size != srcSize)
                            decrypted = decrypted.copyOf(srcSize)
                        decrypted
                    } else
                    // Read the block data
                        exAr.read(srcSize)
                    // Calculate the uncompressed size,
                    // its either just the compression block size
                    // or if its the last block its the remaining data size
                    val uncompressedSize = min(gameFile.compressionBlockSize, (gameFile.uncompressedSize - data.size).toInt())
                    data += Compression.decompress(src, uncompressedSize, gameFile.compressionMethod)
                }
                return when {
                    data.size > gameFile.uncompressedSize -> data.copyOf(gameFile.uncompressedSize.toInt())
                    data.size == gameFile.uncompressedSize.toInt() -> data
                    else -> throw ParserException("Decompression of ${gameFile.getName()} failed, ${data.size} < ${gameFile.uncompressedSize}")
                }
            }
            gameFile.isEncrypted -> {
                logger.debug("${gameFile.getName()} is encrypted, decrypting")
                val key = aesKey ?: throw ParserException("Decrypting a encrypted file requires an aes key to be set")
                // AES is block encryption, all encrypted blocks need to be 16 bytes long,
                // fix the game file length by growing it to the next multiple of 16 bytes
                var newLength = gameFile.size
                while (newLength % Aes.BLOCK_SIZE != 0L)
                    newLength++
                val encryptedData = exAr.read(newLength.toInt())
                var decrypted = Aes.decrypt(encryptedData, key)
                if (decrypted.size != gameFile.size.toInt())
                    decrypted = decrypted.copyOf(gameFile.size.toInt())
                return decrypted
            }
            else -> return exAr.read(gameFile.size.toInt())
        }
    }

    fun indexCheckBytes() : ByteArray {
        Ar.seek(pakInfo.indexOffset)
        return Ar.read(128)
    }

    /**
     * Test whether the given aes key is valid by attempting to read the pak mount point and validating it
     */
    fun testAesKey(key : ByteArray) : Boolean {
        if (!isEncrypted())
            return true
        return testAesKey(indexCheckBytes(), key)
    }

    /**
     * Test whether the given aes key is valid by attempting to read the pak mount point and validating it
     */
    fun testAesKey(key : String) = testAesKey((if (key.startsWith("0x")) key.substring(2) else key).parseHexBinary())

    private fun readIndexInternal() : List<GameFile> {
        // Prepare primary index and decrypt if necessary
        Ar.seek(pakInfo.indexOffset)
        val primaryIndex = if (isEncrypted()) {
            val key = this.aesKey
            if (key != null) {
                val encryptedIndex = Ar.read(pakInfo.indexSize.toInt())
                Aes.decrypt(encryptedIndex, key)
            } else
                throw ParserException("Reading an encrypted index requires a valid aes key")
        } else
            Ar.read(pakInfo.indexSize.toInt())

        val primaryIndexAr = Ar.createReader(primaryIndex, pakInfo.indexOffset)
        primaryIndexAr.pakInfo = Ar.pakInfo

        // Read the index
        var mountPoint = primaryIndexAr.readString()
        var badMountPoint = false
        if (!mountPoint.startsWith("../../.."))
            badMountPoint = true
        else
            mountPoint = mountPoint.replaceFirst("../../..", "")
        if (mountPoint[0] != '/' || (mountPoint.length > 1 && mountPoint[1] == '.'))
            badMountPoint = true
        if (badMountPoint) {
            logger.warn("Pak \"$fileName\" has strange mount point \"$mountPoint\", mounting to root")
            mountPoint = "/"
        }
        if (mountPoint.startsWith('/'))
            mountPoint = mountPoint.substring(1)
        this.mountPrefix = mountPoint

        this.fileCount = primaryIndexAr.readInt32()
        this.encryptedFileCount = 0
        this.pathHashSeed = primaryIndexAr.readUInt64()

        var readerHasPathHashIndex = primaryIndexAr.readBoolean()
        var pathHashIndexOffset = INDEX_NONE.toLong()
        var pathHashIndexSize = 0L
        val pathHashIndexHash = ByteArray(20)
        if (readerHasPathHashIndex) {
            pathHashIndexOffset = primaryIndexAr.readInt64()
            pathHashIndexSize = primaryIndexAr.readInt64()
            primaryIndexAr.read(pathHashIndexHash)
            readerHasPathHashIndex = readerHasPathHashIndex && pathHashIndexOffset != INDEX_NONE.toLong()
        }
        var readerHasFullDirectoryIndex = primaryIndexAr.readBoolean()
        var fullDirectoryIndexOffset = INDEX_NONE.toLong()
        var fullDirectoryIndexSize = 0L
        val fullDirectoryIndexHash = ByteArray(20)
        if (readerHasFullDirectoryIndex) {
            fullDirectoryIndexOffset = primaryIndexAr.readInt64()
            fullDirectoryIndexSize = primaryIndexAr.readInt64()
            primaryIndexAr.read(fullDirectoryIndexHash)
            readerHasFullDirectoryIndex = readerHasFullDirectoryIndex && fullDirectoryIndexOffset != INDEX_NONE.toLong()
        }

        encodedPakEntries = primaryIndexAr.readTArray { it.readInt8() }.toByteArray()

        val filesNum = primaryIndexAr.readInt32()
        if (filesNum < 0) {
            // Should not be possible for any values in the PrimaryIndex to be invalid, since we verified the index hash
            throw ParserException("Corrupt Index: Negative FilesNum $filesNum")
        }

        val tempMap = mutableMapOf<String, GameFile>()
        for (indexCount in 0 until filesNum) {
            val entry = FPakEntry(primaryIndexAr, false)
            val gameFile = GameFile(entry, mountPrefix, fileName)
            if (gameFile.isEncrypted)
                this.encryptedFileCount++
            tempMap[gameFile.path] = gameFile
        }

        // Decide which SecondaryIndex(es) to load
        val willUseFullDirectoryIndex : Boolean
        val willUsePathHashIndex : Boolean
        val readFullDirectoryIndex : Boolean
        if (readerHasPathHashIndex && readerHasFullDirectoryIndex) {
            willUseFullDirectoryIndex = /*IsPakKeepFullDirectory() seems to be false */ false
            willUsePathHashIndex = !willUseFullDirectoryIndex
            //false aswell: bool bWantToReadFullDirectoryIndex = IsPakKeepFullDirectory() || IsPakValidatePruning() || IsPakDelayPruning();
            readFullDirectoryIndex = readerHasFullDirectoryIndex // && bWantToReadFullDirectoryIndex
        } else if (readerHasPathHashIndex) {
            willUsePathHashIndex = true
            willUseFullDirectoryIndex = false
            readFullDirectoryIndex = false
        } else if (readerHasFullDirectoryIndex) {
            // We don't support creating the PathHash Index at runtime; we want to move to having only the PathHashIndex, so supporting not having it at all is not useful enough to write
            willUsePathHashIndex = false
            willUseFullDirectoryIndex = true
            readFullDirectoryIndex = true
        } else {
            throw ParserException("readerHasPathHashIndex = false and readerHasFullDirectoryIndex = false")
        }

        // Load the Secondary Index(es)

        val pathHashIndexData = ByteArray(pathHashIndexSize.toInt())
        val pathHashIndexAr = Ar.createReader(pathHashIndexData, pathHashIndexOffset)
        pathHashIndexAr.pakInfo = Ar.pakInfo

        if (willUsePathHashIndex) {
            if (pathHashIndexOffset < 0 || Ar.pakSize() < (pathHashIndexOffset + pathHashIndexSize) ) {
                throw ParserException("PathHashIndex out of range: ${Ar.pakSize()} < $pathHashIndexOffset + $pathHashIndexSize")
            }

            // Prepare path hash index and decrypt if necessary
            Ar.seek(pathHashIndexOffset)
            val pathHashIndex = if (isEncrypted()) {
                val key = this.aesKey
                if (key != null) {
                    val encryptedIndex = Ar.read(pathHashIndexSize.toInt())
                    Aes.decrypt(encryptedIndex, key)
                } else
                    throw ParserException("Reading an encrypted index requires a valid aes key")
            } else
                Ar.read(pathHashIndexSize.toInt())
            pathHashIndex.copyInto(pathHashIndexData)
            this.pathHashIndex = pathHashIndexAr.readTMap { it.readUInt64() to it.readInt32() }
            hasPathHashIndex = true
        }

        if (!readFullDirectoryIndex) {
            require(willUseFullDirectoryIndex)
            directoryIndex = pathHashIndexAr.readTMap {
                it.readString() to it.readTMap {
                        it2 -> it2.readString() to it2.readInt32()
                }
            }
            hasFullDirectoryIndex = false
        } else {
            if (Ar.pakSize() < (fullDirectoryIndexOffset + fullDirectoryIndexSize)) {
                throw ParserException("FullDirectoryIndex out of range: ${Ar.pakSize()} < $fullDirectoryIndexOffset + $fullDirectoryIndexSize")
            }


            Ar.seek(fullDirectoryIndexOffset)
            val fullDirectoryIndexData = if (isEncrypted()) {
                val key = this.aesKey
                if (key != null) {
                    val encryptedIndex = Ar.read(fullDirectoryIndexSize.toInt())
                    Aes.decrypt(encryptedIndex, key)
                } else
                    throw ParserException("Reading an encrypted index requires a valid aes key")
            } else
                Ar.read(fullDirectoryIndexSize.toInt())

            val secondaryIndexAr = Ar.createReader(fullDirectoryIndexData, fullDirectoryIndexOffset)
            secondaryIndexAr.pakInfo = Ar.pakInfo
            directoryIndex = secondaryIndexAr.readTMap {
                it.readString() to it.readTMap {
                        it2 -> it2.readString() to it2.readInt32()
                }
            }
            hasFullDirectoryIndex = true
        }

        for ((dirName, dirContent) in directoryIndex) {
            for ((fileName, offset) in dirContent) {
                val path = dirName + fileName
                val entry = readBitEntry(offset)
                entry.name = path
                if(entry.isEncrypted)
                    encryptedFileCount++
                val gameFile = GameFile(entry, mountPrefix, this.fileName)
                tempMap[mountPrefix + path] = gameFile
            }
        }

        val files = mutableListOf<GameFile>()
        tempMap.values.forEach {
            if (it.isUE4Package()) {
                val uexp = tempMap[it.path.substringBeforeLast(".") + ".uexp"]
                if(uexp != null)
                    it.uexp = uexp
                val ubulk = tempMap[it.path.substringBeforeLast(".") + ".ubulk"]
                if(ubulk != null)
                    it.ubulk = ubulk
                files.add(it)
            } else {
                if (!it.path.endsWith(".uexp") && !it.path.endsWith(".ubulk"))
                    files.add(it)
            }
        }
        this.files = files

        // Print statistics
        var stats = "Pak %s: %d files".format(if (Ar is FPakFileArchive) Ar.file else fileName, fileCount)
        if (encryptedFileCount != 0)
            stats += " (%d encrypted)".format(encryptedFileCount)
        if (mountPrefix.contains('/'))
            stats += ", mount point: \"%s\"".format(mountPrefix)
        logger.info(stats + ", version %d".format(pakInfo.version))

        if (!keepIndexData) {
            this.encodedPakEntries = byteArrayOf()
            this.directoryIndex = emptyMap()
            this.pathHashIndex = emptyMap()
        }

        return this.files
    }

    private fun readBitEntry(entryOffset : Int): FPakEntry {
        // Grab the big bitfield value:
        // Bit 31 = Offset 32-bit safe?
        // Bit 30 = Uncompressed size 32-bit safe?
        // Bit 29 = Size 32-bit safe?
        // Bits 28-23 = Compression method
        // Bit 22 = Encrypted
        // Bits 21-6 = Compression blocks count
        // Bits 5-0 = Compression block size

        val compressionMethodIndex : UInt
        var compressionBlockSize : UInt
        val offset : Long
        val uncompressedSize : Long
        val size : Long
        val encrypted : Boolean
        val compressionBlocks : Array<FPakCompressedBlock>

        var sourcePtr = BytePointer(encodedPakEntries)
        sourcePtr += entryOffset
        val value = sourcePtr.toUInt32()
        sourcePtr += 4

        // Filter out the CompressionMethod.
        compressionMethodIndex = (value shr 23) and 0x3fu

        // Test for 32-bit safe values. Grab it, or memcpy the 64-bit value
        // to avoid alignment exceptions on platforms requiring 64-bit alignment
        // for 64-bit variables.
        //

        // Read the Offset.
        val isOffset32BitSafe = (value and (1u shl 31)) != 0u
        if (isOffset32BitSafe) {
            offset = sourcePtr.toUInt32().toLong()
            sourcePtr += 4
        } else {
            offset = sourcePtr.toInt64()
            sourcePtr += 8
        }

        // Read the UncompressedSize.
        val isUncompressedSize32BitSafe = (value and (1u shl 30)) != 0u
        if (isUncompressedSize32BitSafe) {
            uncompressedSize = sourcePtr.toUInt32().toLong()
            sourcePtr += 4
        } else {
            uncompressedSize = sourcePtr.toInt64()
            sourcePtr += 8
        }

        // Fill in the Size.
        if (compressionMethodIndex != 0u) {
            // Size is only present if compression is applied.
            val isSize32BitSafe = (value and (1u shl 29)) != 0u
            if (isSize32BitSafe) {
                size = sourcePtr.toUInt32().toLong()
                sourcePtr += 4
            } else {
                size = sourcePtr.toInt64()
                sourcePtr += 8
            }
        } else {
            // The Size is the same thing as the UncompressedSize when
            // CompressionMethod == COMPRESS_None.
            size = uncompressedSize
        }

        // Filter the encrypted flag.
        encrypted = (value and (1u shl 22)) != 0u

        // This should clear out any excess CompressionBlocks that may be valid in the user's
        // passed in entry.
        val compressionBlocksCount = (value shr 6) and 0xffffu

        compressionBlocks = Array(compressionBlocksCount.toInt()) { FPakCompressedBlock(0L, 0L) }

        // Filter the compression block size or use the UncompressedSize if less that 64k.
        compressionBlockSize = 0u
        if (compressionBlocksCount > 0u) {
            compressionBlockSize = if (uncompressedSize < 65536) uncompressedSize.toUInt() else ((value and 0x3fu) shl 11)
        }

        // Set bDeleteRecord to false, because it obviously isn't deleted if we are here.
        //deleted = false Not needed

        // Base offset to the compressed data
        val baseOffset = if (pakInfo.version >= PakVersion_RelativeChunkOffsets) 0 else offset

        // Handle building of the CompressionBlocks array.
        if (compressionBlocks.size == 1 && !encrypted) {
            // If the number of CompressionBlocks is 1, we didn't store any extra information.
            // Derive what we can from the entry's file offset and size.
            val compressedBlock = compressionBlocks[0]
            compressedBlock.compressedStart = baseOffset + FPakEntry.getSerializedSize(pakInfo.version, compressionMethodIndex.toInt(), compressionBlocksCount.toInt())
            compressedBlock.compressedEnd = compressedBlock.compressedStart + size
        } else if (compressionBlocks.isNotEmpty()) {
            // Get the right pointer to start copying the CompressionBlocks information from.
            var compressionBlockSizePtr = sourcePtr + 0

            // Alignment of the compressed blocks
            val compressedBlockAlignment = if (encrypted) Aes.BLOCK_SIZE else 1

            // CompressedBlockOffset is the starting offset. Everything else can be derived from there.
            var compressedBlockOffset = baseOffset + FPakEntry.getSerializedSize(pakInfo.version, compressionMethodIndex.toInt(), compressionBlocksCount.toInt())
            for (compressionBlockIndex in compressionBlocks.indices) {
                val compressedBlock = compressionBlocks[compressionBlockIndex]
                compressedBlock.compressedStart = compressedBlockOffset
                compressedBlock.compressedEnd = (compressedBlockOffset.toUInt() + compressionBlockSizePtr.toUInt32()).toLong()
                compressionBlockSizePtr += 4
                val align = compressedBlock.compressedEnd - compressedBlock.compressedStart
                compressedBlockOffset += align + compressedBlockAlignment - (align % compressedBlockAlignment)
            }
        }
        //TODO There is some kind of issue here, compression blocks are sometimes going to far by one byte
        compressionBlocks.forEach {
            it.compressedStart += offset
            it.compressedEnd += offset
        }
        return FPakEntry(pakInfo, "", offset, size, uncompressedSize, compressionMethodIndex.toInt(), compressionBlocks, encrypted, compressionBlockSize.toInt())
    }

    fun readIndex() : List<GameFile> = if (pakInfo.version >= PakVersion_PathHashIndex) readIndexInternal() else readIndexLegacy()

    private fun readIndexLegacy() : List<GameFile> {

        // Prepare index and decrypt if necessary
        Ar.seek(pakInfo.indexOffset)
        val index = if (isEncrypted()) {
            val key = this.aesKey
            if (key != null) {
                val encryptedIndex = Ar.read(pakInfo.indexSize.toInt())
                Aes.decrypt(encryptedIndex, key)
            } else
                throw ParserException("Reading an encrypted index requires a valid aes key")
        } else
            Ar.read(pakInfo.indexSize.toInt())

        val indexAr = Ar.createReader(index, pakInfo.indexOffset)
        indexAr.pakInfo = Ar.pakInfo

        // Read the index
        var mountPoint = indexAr.readString()
        var badMountPoint = false
        if (!mountPoint.startsWith("../../.."))
            badMountPoint = true
        else
            mountPoint = mountPoint.replaceFirst("../../..", "")
        if (mountPoint[0] != '/' || (mountPoint.length > 1 && mountPoint[1] == '.'))
            badMountPoint = true
        if (badMountPoint) {
            logger.warn("Pak \"$fileName\" has strange mount point \"$mountPoint\", mounting to root")
            mountPoint = "/"
        }
        if (mountPoint.startsWith('/'))
            mountPoint = mountPoint.substring(1)
        this.mountPrefix = mountPoint

        this.fileCount = indexAr.readInt32()
        this.encryptedFileCount = 0

        val tempMap = mutableMapOf<String, GameFile>()
        for (indexCount in 0 until fileCount) {
            val entry = FPakEntry(indexAr, true)
            val gameFile = GameFile(entry, mountPrefix, fileName)
            if (gameFile.isEncrypted)
                this.encryptedFileCount++
            tempMap[gameFile.path] = gameFile
        }

        val files = mutableListOf<GameFile>()
        tempMap.values.forEach {
            if (it.isUE4Package()) {
                val uexp = tempMap[it.path.substringBeforeLast(".") + ".uexp"]
                if(uexp != null)
                    it.uexp = uexp
                val ubulk = tempMap[it.path.substringBeforeLast(".") + ".ubulk"]
                if(ubulk != null)
                    it.ubulk = ubulk
                files.add(it)
            } else {
                if (!it.path.endsWith(".uexp") && !it.path.endsWith(".ubulk"))
                    files.add(it)
            }
        }
        this.files = files


        // Print statistics
        var stats = "Pak %s: %d files".format(if (Ar is FPakFileArchive) Ar.file else fileName, fileCount)
        if (encryptedFileCount != 0)
            stats += " (%d encrypted)".format(encryptedFileCount)
        if (mountPrefix.contains('/'))
            stats += ", mount point: \"%s\"".format(mountPrefix)
        logger.info(stats + ", version %d".format(pakInfo.version))
        return this.files
    }

    companion object {

        val logger = KotlinLogging.logger("PakFile")

        fun testAesKey(bytes : ByteArray, key : ByteArray) : Boolean {
            val testAr = FByteArchive(Aes.decrypt(bytes, key))
            val stringLength = testAr.readInt32()
            if (stringLength > 128 || stringLength < -128)
                return false
            // Calculate the pos of the null terminator for this string
            // Then read the null terminator byte and check whether it is actually 0
            return when {
                stringLength == 0 -> testAr.readInt8() == 0.toByte()
                stringLength < 0 -> {
                    // UTF16
                    val nullTerminatorPos = 4 - (stringLength - 1) * 2
                    testAr.seek(nullTerminatorPos)
                    testAr.readInt16() == 0.toShort()
                }
                else -> {
                    // UTF8
                    val nullTerminatorPos = 4 + stringLength - 1
                    testAr.seek(nullTerminatorPos)
                    testAr.readInt8() == 0.toByte()
                }
            }
        }
    }
}