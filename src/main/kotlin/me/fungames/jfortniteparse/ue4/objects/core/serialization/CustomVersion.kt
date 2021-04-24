package me.fungames.jfortniteparse.ue4.objects.core.serialization

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Structure to hold unique custom key with its version.
 */
class FCustomVersion {
    /** Unique custom key. */
    var key: FGuid

    /** Custom version. */
    var version: Int

    constructor(Ar: FArchive) {
        key = FGuid(Ar)
        version = Ar.readInt32()
    }

    fun serialize(Ar: FArchiveWriter) {
        key.serialize(Ar)
        Ar.writeInt32(version)
    }

    constructor(key: FGuid, version: Int) {
        this.key = key
        this.version = version
    }
}