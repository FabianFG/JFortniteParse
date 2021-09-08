package me.fungames.jfortniteparse.ue4.objects.engine.animation

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FSmartName {
    var displayName: FName

    constructor(Ar: FArchive) {
        displayName = Ar.readFName()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeFName(displayName)
    }

    constructor(displayName: FName) {
        this.displayName = displayName
    }
}