package me.fungames.jfortniteparse.ue4.objects.core.misc

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.writer.FByteArchiveWriter
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import me.fungames.jfortniteparse.util.parseHexBinary
import me.fungames.jfortniteparse.util.printHexBinary

@Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_UNSIGNED_LITERALS", "EXPERIMENTAL_OVERRIDE")
class FGuid : UClass {
    companion object {
        @JvmStatic
        val mainGuid: FGuid by lazy { FGuid(0u, 0u, 0u, 0u) }
    }

    var part1: UInt
    var part2: UInt
    var part3: UInt
    var part4: UInt
    var hexString: String

    constructor(Ar: FArchive) {
        super.init(Ar)
        val content = Ar.read(16)
        val ar = FByteArchive(content)
        part1 = ar.readUInt32()
        part2 = ar.readUInt32()
        part3 = ar.readUInt32()
        part4 = ar.readUInt32()
        hexString = content.printHexBinary()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(part1)
        Ar.writeUInt32(part2)
        Ar.writeUInt32(part3)
        Ar.writeUInt32(part4)
        super.completeWrite(Ar)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FGuid

        if (part1 != other.part1) return false
        if (part2 != other.part2) return false
        if (part3 != other.part3) return false
        if (part4 != other.part4) return false

        return true
    }

    override fun hashCode(): Int {
        var result = part1.hashCode()
        result = 31 * result + part2.hashCode()
        result = 31 * result + part3.hashCode()
        result = 31 * result + part4.hashCode()
        return result
    }


    constructor(part1: UInt, part2: UInt, part3: UInt, part4: UInt) {
        this.part1 = part1
        this.part2 = part2
        this.part3 = part3
        this.part4 = part4
        val ar = FByteArchiveWriter()
        ar.writeUInt32(part1)
        ar.writeUInt32(part2)
        ar.writeUInt32(part3)
        ar.writeUInt32(part4)
        this.hexString = ar.toByteArray().printHexBinary()
    }

    constructor(hexString: String) {
        this.hexString = hexString
        val ar = FByteArchive(hexString.parseHexBinary())
        part1 = ar.readUInt32()
        part2 = ar.readUInt32()
        part3 = ar.readUInt32()
        part4 = ar.readUInt32()
    }

    /**
     * @return a string representation of this Guid
     */
    override fun toString() = "%08X%08X%08X%08X".format(part1.toInt(), part2.toInt(), part3.toInt(), part4.toInt())
}