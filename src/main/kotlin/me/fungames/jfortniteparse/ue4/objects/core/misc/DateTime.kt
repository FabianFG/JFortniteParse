package me.fungames.jfortniteparse.ue4.objects.core.misc

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import java.util.*

class FDateTime {
    var date: Long

    constructor(Ar: FArchive) {
        date = Ar.readInt64()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt64(date)
    }

    constructor() : this(0)

    constructor(date: Long) {
        this.date = date
    }

    inline fun toDate() = Date(date)
}