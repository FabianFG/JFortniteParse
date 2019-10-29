package me.fungames.jfortniteparse.ue4

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import mu.KotlinLogging

@ExperimentalUnsignedTypes
abstract class UEClass {
    companion object {
        val logger = KotlinLogging.logger("JFortniteParse")
    }

    var begin = -1
        private set
    var end = -1
        private set
    var writeBegin = -1
        private set
    var writeEnd = -1
        private set

    protected fun init(Ar: FArchive) {
        check(begin < 0 && end < 0) { "UE Class (${this::class.simpleName}) was started to be deserialized but not finished" }
        end = -1
        begin = Ar.pos()
        //logger.debug("[${this::class.simpleName}] Starting deserialization at ${Ar.pos()}")
    }

    protected fun complete(Ar: FArchive) {
        check(begin >= 0) { "UE Class (${this::class.simpleName}) was not initialized yet" }
        end = Ar.pos()
        //logger.debug("[${this::class.simpleName}] Finished deserialization from $begin to $end, ${end - begin} bytes total")
    }

    protected fun initWrite(Ar: FArchiveWriter) {
        writeEnd = -1
        writeBegin = Ar.pos()
        //logger.debug("[${this::class.simpleName}] Starting serialization at ${Ar.pos()}")
    }

    protected fun completeWrite(Ar: FArchiveWriter) {
        writeEnd = Ar.pos()
        //logger.debug("[${this::class.simpleName}] Finished serialization from $writeBegin to $writeEnd, ${writeEnd - writeBegin} bytes total")
    }
}

@ExperimentalUnsignedTypes
class FGuid : UEClass {
    var part1: UInt
    var part2: UInt
    var part3: UInt
    var part4: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        part1 = Ar.readUInt32()
        part2 = Ar.readUInt32()
        part3 = Ar.readUInt32()
        part4 = Ar.readUInt32()
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
    }
}