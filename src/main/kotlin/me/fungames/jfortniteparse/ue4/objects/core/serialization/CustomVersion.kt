package me.fungames.jfortniteparse.ue4.objects.core.serialization

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/**
 * Structure to hold unique custom key with its version.
 */
@ExperimentalUnsignedTypes
class FCustomVersion : UClass {
    /** Unique custom key. */
    var key: FGuid

    /** Custom version. */
    var version: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        key = FGuid(Ar)
        version = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        key.serialize(Ar)
        Ar.writeInt32(version)
        super.completeWrite(Ar)
    }

    constructor(key: FGuid, version: Int) {
        this.key = key
        this.version = version
    }
}