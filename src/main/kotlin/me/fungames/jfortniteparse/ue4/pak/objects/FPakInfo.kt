package me.fungames.jfortniteparse.ue4.pak.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_FNameBasedCompressionMethod
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_FrozenIndex
import me.fungames.jfortniteparse.ue4.pak.enums.PakVersion_PathHashIndex
import me.fungames.jfortniteparse.ue4.pak.reader.FPakArchive

@ExperimentalUnsignedTypes
class FPakInfo : UClass {
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
    var indexIsFrozen : Boolean = false

    constructor(Ar : FPakArchive, maxNumCompressionMethods : Int = 4) {
        super.init(Ar)

        val data = Ar.readAndCreateReader(size + maxNumCompressionMethods * 32)
        // New FPakInfo fields
        encryptionKeyGuid = FGuid(data)
        encryptedIndex = data.readFlag()

        // Old FPakInfoFields
        val magic = data.readUInt32()
        if (magic != PAK_MAGIC)
            throw ParserException("Invalid pak file magic", data)
        version = data.readInt32()
        indexOffset = data.readInt64()
        indexSize = data.readInt64()
        indexHash = data.read(20)
        if (this.version in PakVersion_FrozenIndex until PakVersion_PathHashIndex) {
            indexIsFrozen = data.readBoolean()
            if (indexIsFrozen) {
                logger.warn { "Frozen PakFile Index" }
            }
        }
        compressionMethods = mutableListOf()
        if (this.version >= PakVersion_FNameBasedCompressionMethod) {
            compressionMethods.add("None")
            for (i in 0 until maxNumCompressionMethods) {
                val d = data.read(32)
                val str = d.takeWhile { it != 0.toByte() }.toByteArray().toString(Charsets.UTF_8)
                if (str.isBlank())
                    break
                compressionMethods.add(str)
            }
        }
        super.complete(Ar)
    }
}