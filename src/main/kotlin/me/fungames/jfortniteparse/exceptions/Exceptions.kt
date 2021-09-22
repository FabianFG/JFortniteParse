package me.fungames.jfortniteparse.exceptions

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

open class ParserException(message: String?, cause: Throwable? = null) : Exception(message, cause) {
    constructor(message: String, Ar: FArchive, cause: Throwable? = null) :
        this("$message\n${Ar.printError()}", cause)

    constructor(message: String, Ar: FArchiveWriter, cause: Throwable? = null) :
        this("$message\n${Ar.printError()}", cause)
}

class InvalidAesKeyException(message: String?, cause: Throwable? = null) : ParserException(message, cause)
class MissingSchemaException(message: String?, cause: Throwable? = null) : ParserException(message, cause)
class UnknownPropertyException(message: String, Ar: FArchive, cause: Throwable? = null) : ParserException(message, Ar, cause)