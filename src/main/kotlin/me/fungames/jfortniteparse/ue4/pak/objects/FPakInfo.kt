package me.fungames.jfortniteparse.ue4.pak.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.pak.enums.*
import me.fungames.jfortniteparse.ue4.pak.reader.FPakArchive
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive

@ExperimentalUnsignedTypes
class FPakInfo : UClass {
    companion object {
        const val PAK_MAGIC = 0x5A6F12E1u

        const val size = 4 * 2 + 8 * 2 + 20 + 1 + 16
        const val size8 = size + 4 * 32
        const val size8a = size8 + 32
        const val size9 = size8a + 1

        val offsetsToTry =              arrayOf(size, size8, size8a, size9)
        val maxNumCompressionMethods =  arrayOf(0   , 4    , 5     , 5    )

        fun readPakInfo(Ar : FPakArchive) : FPakInfo {
            val pakSize = Ar.pakSize()
            var maxSize = -1
            var maxOffsetToTryIndex = -1
            for (i in offsetsToTry.size - 1 downTo 0) {
                if (pakSize - offsetsToTry[i] >= 0) {
                    maxSize = offsetsToTry[i]
                    maxOffsetToTryIndex = i
                    break
                }
            }
            if (maxSize < 0)
                throw ParserException("File '${Ar.fileName} has an unknown format")
            Ar.seek(pakSize - maxSize)
            val tempAr = FByteArchive(Ar.read(maxSize))
            for (i in 0 until maxOffsetToTryIndex) {
                tempAr.seek(maxSize - offsetsToTry[i])
                try {
                    return FPakInfo(tempAr, maxNumCompressionMethods[i])
                } catch (t : Throwable) {}
            }
            throw ParserException("File '${Ar.fileName} has an unknown format")
        }
    }
    var encryptionKeyGuid : FGuid
    var encryptedIndex : Boolean
    var version : Int
    var indexOffset : Long
    var indexSize : Long
    var indexHash : ByteArray
    var compressionMethods : MutableList<String>
    var indexIsFrozen : Boolean = false

    constructor(Ar : FArchive, maxNumCompressionMethods : Int = 4) {
        super.init(Ar)

        val newFieldsPos = Ar.pos()
        Ar.skip(17)

        // Old FPakInfoFields
        val magic = Ar.readUInt32()
        if (magic != PAK_MAGIC)
            throw ParserException("Invalid pak file magic", Ar)
        version = Ar.readInt32()

        val pos = Ar.pos()
        Ar.seek(newFieldsPos)
        // Seek back to read new pak fields that we couldn't read before because we need to know the version first
        // New FPakInfo fields
        encryptionKeyGuid = if (version >= PakVersion_EncryptionKeyGuid)
            FGuid(Ar)
        else
            FGuid.mainGuid.apply { Ar.skip(16) }

        encryptedIndex = if (version >= PakVersion_IndexEncryption)
            Ar.readFlag()
        else
            false.apply { Ar.skip(1) }

        Ar.seek(pos)

        indexOffset = Ar.readInt64()
        indexSize = Ar.readInt64()
        indexHash = Ar.read(20)
        if (this.version in PakVersion_FrozenIndex until PakVersion_PathHashIndex) {
            indexIsFrozen = Ar.readBoolean()
            if (indexIsFrozen) {
                logger.warn { "Frozen PakFile Index" }
            }
        }
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