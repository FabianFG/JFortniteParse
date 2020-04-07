package me.fungames.jfortniteparse.ue4.pak.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.pak.CompressionMethod
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_CompressionEncryption
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_FNameBasedCompressionMethod
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_NoTimestamps
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_RelativeChunkOffsets
import me.fungames.jfortniteparse.ue4.pak.reader.FPakArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_VALORANT

@ExperimentalUnsignedTypes
class FPakEntry : UClass {
    var name : String
    var pos : Long
    var size : Long
    var uncompressedSize : Long
    var compressionMethod : CompressionMethod
    var hash : ByteArray
    var compressionBlocks : Array<FPakCompressedBlock>
    var isEncrypted : Boolean = false
    var compressionBlockSize : Int = 0

    companion object {
        @JvmStatic
        fun getSerializedSize(version : Int, compressionMethod : Int = 0, compressionBlocksCount : Int = 0): Int {
            var serializedSize = /*pos*/ 8 + /*size*/ 8 + /*uncompressedSize*/ 8 + /*hash*/ 20
            serializedSize += if (version >= PakVersion_FNameBasedCompressionMethod) {
                4
            } else {
                4 // Old CompressedMethod var from pre-fname based compression methods
            }

            if (version >= PakVersion_CompressionEncryption) {
                serializedSize += /*isEncrypted*/ 1 + /*compressionBlockSize*/ 4
                if (compressionMethod != 0) {
                    serializedSize += /*FPakCompressedBlock*/ 8 * 2 * compressionBlocksCount + /*int32*/ 4
                }
            }
            if (version < PakVersion_NoTimestamps) {
                serializedSize += /*timestamp*/ 8
            }
            return serializedSize
        }
    }

    constructor(Ar: FPakArchive, inIndex : Boolean) {
        super.init(Ar)
        name = if (inIndex) Ar.readString() else ""
        pos = Ar.readInt64()
        size = Ar.readInt64()
        uncompressedSize = Ar.readInt64()
        compressionMethod = if (Ar.pakInfo.version >= PakVersion_FNameBasedCompressionMethod) {
            try {
                CompressionMethod.valueOf(Ar.pakInfo.compressionMethods[Ar.readInt32()])
            } catch (e : Exception) {
                if (Ar.game == GAME_VALORANT)
                    CompressionMethod.None
                else
                    CompressionMethod.Unknown
            }
        } else {
            when(Ar.readInt32()) {
                0 -> CompressionMethod.None
                4 -> CompressionMethod.Oodle
                else -> if (Ar.game == GAME_VALORANT)
                    CompressionMethod.None
                else
                    CompressionMethod.Unknown
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
            //Looks like Valorant sets the encryption flag to false although inis are encrypted
            if (Ar.game == GAME_VALORANT && name.endsWith(".ini"))
                isEncrypted = true
            //Note: This is not how it works in UE, default is Int8 but Fortnite uses Int32.
            // This project was originally intended to be used only with Fortnite, that's why it is twisted like that
            compressionBlockSize = if (Ar.game == GAME_VALORANT)
                Ar.readInt8().toInt()
            else
                Ar.readInt32()
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

    constructor(
        pakInfo: FPakInfo,
        name: String,
        pos: Long,
        size: Long,
        uncompressedSize: Long,
        compressionMethodIndex: Int,
        hash: ByteArray,
        compressionBlocks: Array<FPakCompressedBlock>,
        isEncrypted: Boolean,
        compressionBlockSize: Int
    ) : super() {
        this.name = name
        this.pos = pos
        this.size = size
        this.uncompressedSize = uncompressedSize
        this.compressionMethod = try {
            CompressionMethod.valueOf(pakInfo.compressionMethods[compressionMethodIndex])
        } catch (e : IllegalArgumentException) {
            CompressionMethod.Unknown
        }
        this.hash = hash
        this.compressionBlocks = compressionBlocks
        this.isEncrypted = isEncrypted
        this.compressionBlockSize = compressionBlockSize
    }
}