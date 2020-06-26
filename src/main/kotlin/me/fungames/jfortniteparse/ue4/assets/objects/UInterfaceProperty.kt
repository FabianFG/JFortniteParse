package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class UInterfaceProperty : UClass {
    var interfaceNumber: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        interfaceNumber = Ar.readUInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(interfaceNumber)
        super.completeWrite(Ar)
    }

    constructor(interfaceNumber: UInt) {
        this.interfaceNumber = interfaceNumber
    }
}