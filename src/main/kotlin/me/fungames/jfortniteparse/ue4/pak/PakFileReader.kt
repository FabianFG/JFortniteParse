package me.fungames.jfortniteparse.ue4.pak

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import me.fungames.jfortniteparse.compression.Compression
import me.fungames.jfortniteparse.encryption.aes.Aes
import me.fungames.jfortniteparse.exceptions.InvalidAesKeyException
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.pak.reader.FPakArchive
import me.fungames.jfortniteparse.ue4.pak.reader.FPakFileArchive
import me.fungames.jfortniteparse.util.parseHexBinary
import mu.KotlinLogging
import java.io.File
import java.io.RandomAccessFile
import kotlin.math.min

@ExperimentalUnsignedTypes
class PakFileReader(val Ar : FPakArchive) {
    companion object {
        val logger = KotlinLogging.logger("PakFile")
    }
    constructor(file : File) : this(FPakFileArchive(RandomAccessFile(file, "r"), file))
    constructor(filePath : String) : this(File(filePath))

    var concurrent = false

    val fileName = Ar.fileName

    val pakInfo : FPakInfo = FPakInfo.readPakInfo(Ar)
    var aesKey : String? = null
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
        FPakEntry(exAr, false)
        when {
            gameFile.isCompressed() -> {
                logger.debug("${gameFile.getName()} is compressed with ${gameFile.compressionMethod}")
                var data = ByteArray(0)
                gameFile.compressedBlocks.forEach { block ->
                    exAr.seek(block.compressedStart)
                    var srcSize = (block.compressedEnd - block.compressedStart).toInt()
                    // Read the compressed block
                    val src = if (gameFile.isEncrypted) {
                        // The compressed block is encrypted, align it and then decrypt
                        val key = aesKey ?: throw ParserException("Decrypting a encrypted file requires an aes key to be set")
                        while (srcSize % 16 != 0)
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
                while (newLength % 16 != 0L)
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

    /**
     * Test whether the given aes key is valid by attempting to read the pak mount point and validating it
     */
    fun testAesKey(key : ByteArray) : Boolean {
        if (!isEncrypted())
            return true
        Ar.seek(pakInfo.indexOffset)
        // Read 128 test bytes and decrypt it with the given key
        val testAr = Ar.createReader(Aes.decrypt(Ar.read(128), key), pakInfo.indexOffset)
        val stringLength = testAr.readInt32()
        if (stringLength > 512 || stringLength < -512)
            return false
        // Calculate the pos of the null terminator for this string
        // Then read the null terminator byte and check whether it is actually 0
        return when {
            stringLength == 0 -> testAr.readUInt16() == 0.toUShort()
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

    /**
     * Test whether the given aes key is valid by attempting to read the pak mount point and validating it
     */
    fun testAesKey(key : String) = testAesKey((if (key.startsWith("0x")) key.substring(2) else key).parseHexBinary())

    fun readIndex() : List<GameFile> {
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
                val uexp = tempMap[it.path.substringBeforeLast(".uasset") + ".uexp"]
                if(uexp != null)
                    it.uexp = uexp
                val ubulk = tempMap[it.path.substringBeforeLast(".uasset") + ".ubulk"]
                if(ubulk != null)
                    it.uexp = ubulk
                files.add(it)
            } else {
                //if (!it.path.endsWith(".uexp") && !it.path.endsWith(".ubulk"))
                    files.add(it)
            }
        }
        this.files = files

        logger.info(String.format("Pak %s: %d files (%d encrypted), mount point: \"%s\", version %d",
            fileName, this.fileCount, this.encryptedFileCount, this.mountPrefix,
            this.pakInfo.version))
        return this.files
    }



}