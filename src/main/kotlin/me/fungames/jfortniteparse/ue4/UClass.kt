package me.fungames.jfortniteparse.ue4

import me.fungames.jfortniteparse.ue4.pak.reader.FPakArchive
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import mu.KotlinLogging

@Suppress("UNUSED_PARAMETER")
@ExperimentalUnsignedTypes
abstract class UClass {
    companion object {
        val logger = KotlinLogging.logger("JFortniteParse")
    }

    /*@field:Transient
    var begin = -1
        private set
    @field:Transient
    var end = -1
        private set
    @field:Transient
    var writeBegin = -1
        private set
    @field:Transient
    var writeEnd = -1
        private set

     */

    protected fun init(Ar: FArchive) {
        /*if (Ar is FPakArchive)
            return
        check(begin < 0 && end < 0) { "UE Class (${this::class.java.simpleName}) was started to be deserialized but not finished" }
        end = -1
        begin = Ar.pos()
        logger.debug("[${this::class.simpleName}] Starting deserialization at ${Ar.pos()}")
         */
    }

    protected fun complete(Ar: FArchive) {
        /*if (Ar is FPakArchive)
            return
        check(begin >= 0) { "UE Class (${this::class.java.simpleName}) was not initialized yet" }
        end = Ar.pos()
        logger.debug("[${this::class.simpleName}] Finished deserialization from $begin to $end, ${end - begin} bytes total")
         */
    }

    protected fun initWrite(Ar: FArchiveWriter) {
        /*writeEnd = -1
        writeBegin = Ar.pos()
        logger.debug("[${this::class.simpleName}] Starting serialization at ${Ar.pos()}")
         */
    }

    protected fun completeWrite(Ar: FArchiveWriter) {
        /*
        writeEnd = Ar.pos()
        logger.debug("[${this::class.simpleName}] Finished serialization from $writeBegin to $writeEnd, ${writeEnd - writeBegin} bytes total")
         */
    }
}