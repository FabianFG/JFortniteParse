package me.fungames.jfortniteparse.ue4.objects.engine.animation

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FSmartName : UClass {
    var displayName: FName

    constructor(Ar: FArchive) {
        super.init(Ar)
        displayName = Ar.readFName()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(displayName)
        super.completeWrite(Ar)
    }

    constructor(displayName: FName) {
        this.displayName = displayName
    }
}