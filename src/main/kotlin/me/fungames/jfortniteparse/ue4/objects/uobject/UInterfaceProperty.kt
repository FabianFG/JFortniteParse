package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class UInterfaceProperty {
    var interfaceNumber: UInt

    constructor(Ar: FArchive) {
        interfaceNumber = Ar.readUInt32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt32(interfaceNumber)
    }

    constructor(interfaceNumber: UInt) {
        this.interfaceNumber = interfaceNumber
    }
}