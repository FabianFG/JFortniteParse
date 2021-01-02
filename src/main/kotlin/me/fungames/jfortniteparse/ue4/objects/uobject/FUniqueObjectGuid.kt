package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FUniqueObjectGuid {
    val guid: FGuid

    constructor(Ar: FArchive) {
        guid = FGuid(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        guid.serialize(Ar)
    }

    constructor(guid: FGuid) {
        this.guid = guid
    }
}