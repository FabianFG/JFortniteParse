package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FCustomVersion : UClass {
    var key: FGuid
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