package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FStripDataFlags : UClass {
    var globalStripFlags : UByte
    var classStripFlags : UByte

    constructor(Ar: FArchive) {
        super.init(Ar)
        globalStripFlags = Ar.readUInt8()
        classStripFlags = Ar.readUInt8()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt8(globalStripFlags)
        Ar.writeUInt8(classStripFlags)
        super.completeWrite(Ar)
    }

    fun isEditorDataStripped() = (globalStripFlags and 1u) != 0.toUByte()
    fun isDataStrippedForServer() = (globalStripFlags and 2u) != 0.toUByte()
    fun isClassDataStripped(flag : UByte) = (classStripFlags and flag) != 0.toUByte()

    constructor(globalStripFlags : UByte, classStripFlags : UByte) {
        this.globalStripFlags = globalStripFlags
        this.classStripFlags = classStripFlags
    }
}