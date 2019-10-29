package me.fungames.jfortniteparse.ue4.pak

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.pak.reader.FPakArchive
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

const val PakVersion_Initial = 1
const val PakVersion_NoTimestamps = 2
const val PakVersion_CompressionEncryption = 3          // UE4.3+
const val PakVersion_IndexEncryption = 4                // UE4.17+ - encrypts only pak file index data leaving file content as is
const val PakVersion_RelativeChunkOffsets = 5           // UE4.20+
const val PakVersion_DeleteRecords = 6                  // UE4.21+ - this constant is not used in UE4 code
const val PakVersion_EncryptionKeyGuid = 7              // ... allows to use multiple encryption keys over the single project
const val PakVersion_FNameBasedCompressionMethod = 8    // UE4.22+ - use string instead of enum for compression method
const val PakVersion_Last = 9
const val PakVersion_Latest = PakVersion_Last - 1

@ExperimentalUnsignedTypes
class FPakInfo : UEClass {
    companion object {
        const val PAK_MAGIC = 0x5A6F12E1u

        const val size = 4 * 2 + 8 * 2 + 20 + /* new fields */ 1 + 16

        fun readPakInfo(Ar : FPakArchive): FPakInfo {
            var offset = Ar.pakSize() - size
            val terminator = Ar.pakSize() - size - 300 //Dont run into endless loop if the file is no pak file
            var maxNumCompressionMethods = 0
            var testInfo: FPakInfo? = null
            do {
                try {
                    Ar.seek(offset)
                    testInfo = FPakInfo(Ar, maxNumCompressionMethods)
                    break
                } catch (e : ParserException) {
                    offset -= 32 //One compression method is 32 bytes long
                    maxNumCompressionMethods++
                }
            } while (offset > terminator)
            if (testInfo == null)
                throw ParserException("File '${Ar.fileName} has an unknown format")
            return testInfo
        }
    }
    var encryptionKeyGuid : FGuid
    var encryptedIndex : Boolean
    var version : Int
    var indexOffset : Long
    var indexSize : Long
    var indexHash : ByteArray
    var compressionMethods : MutableList<String>

    constructor(Ar : FArchive, maxNumCompressionMethods : Int = 4) {
        super.init(Ar)

        // New FPakInfo fields
        encryptionKeyGuid = FGuid(Ar)
        encryptedIndex = Ar.readFlag()

        // Old FPakInfoFields
        val magic = Ar.readUInt32()
        if (magic != PAK_MAGIC)
            throw ParserException("Invalid pak file magic", Ar)
        version = Ar.readInt32()
        indexOffset = Ar.readInt64()
        indexSize = Ar.readInt64()
        indexHash = Ar.read(20)
        compressionMethods = mutableListOf()
        if (this.version >= PakVersion_FNameBasedCompressionMethod) {
            compressionMethods.add("None")
            for (i in 0 until maxNumCompressionMethods) {
                val d = Ar.read(32)
                val str = d.takeWhile { it != 0.toByte() }.toByteArray().toString(Charsets.UTF_8)
                if (str.isBlank())
                    break
                compressionMethods.add(str)
            }
        }
        super.complete(Ar)
    }
}

@ExperimentalUnsignedTypes
class FPakCompressedBlock : UEClass {
    var compressedStart : Long
    var compressedEnd : Long

    constructor(Ar: FArchive) {
        super.init(Ar)
        compressedStart = Ar.readInt64()
        compressedEnd = Ar.readInt64()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt64(compressedStart)
        Ar.writeInt64(compressedEnd)
        super.completeWrite(Ar)
    }


    constructor(compressedStart: Long, compressedEnd: Long) : super() {
        this.compressedStart = compressedStart
        this.compressedEnd = compressedEnd
    }
}

@ExperimentalUnsignedTypes
class FPakEntry : UEClass {
    var name : String
    var pos : Long
    var size : Long
    var uncompressedSize : Long
    var compressionMethod : CompressionMethod
    var hash : ByteArray
    var compressionBlocks : Array<FPakCompressedBlock>
    var isEncrypted : Boolean = false
    var compressionBlockSize : Int = 0

    constructor(Ar: FPakArchive, inIndex : Boolean) {
        super.init(Ar)
        name = if (inIndex) Ar.readString() else ""
        pos = Ar.readInt64()
        size = Ar.readInt64()
        uncompressedSize = Ar.readInt64()
        compressionMethod = if (Ar.pakInfo.version >= PakVersion_FNameBasedCompressionMethod) {
            try {
                CompressionMethod.valueOf(Ar.pakInfo.compressionMethods[Ar.readInt32()])
            } catch (e : IllegalArgumentException) {
                CompressionMethod.Unknown
            }
        } else {
            when(Ar.readInt32()) {
                0 -> CompressionMethod.None
                4 -> CompressionMethod.Oodle
                else -> CompressionMethod.Unknown
            }
        }
        if (Ar.pakInfo.version < PakVersion_NoTimestamps)
            Ar.readInt64() // Timestamp
        hash = Ar.read(20)

        compressionBlocks = emptyArray()
        if (Ar.pakInfo.version >= PakVersion_CompressionEncryption) {
            if (compressionMethod != CompressionMethod.None)
                compressionBlocks = Ar.readTArray { FPakCompressedBlock(Ar) }
            isEncrypted = Ar.readFlag()
            compressionBlockSize = Ar.readInt32()
        }
        if (Ar.pakInfo.version >= PakVersion_RelativeChunkOffsets) {
            // Convert relative compressed offsets to absolute
            compressionBlocks.forEach {
                it.compressedStart += pos
                it.compressedEnd += pos
            }
        }
        super.complete(Ar)
    }
}