package me.fungames.jfortniteparse.exceptions

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

open class ParserException(override val message: String?, override val cause: Throwable? = null) : Exception() {
    @ExperimentalUnsignedTypes
    constructor(message: String, Ar : FArchive, cause: Throwable? = null) : this(
        """
            $message
            ${Ar.printError()}
        """.trimIndent(), cause
    )
    @ExperimentalUnsignedTypes
    constructor(message: String, Ar : FArchiveWriter, cause: Throwable? = null) : this(
        """
            $message
            ${Ar.printError()}
        """.trimIndent(), cause
    )
}

class InvalidAesKeyException(override val message: String?, override val cause: Throwable? = null) : ParserException(message, cause)
class UnknownCompressionMethodException(override val message: String?, override val cause: Throwable? = null) : RuntimeException()