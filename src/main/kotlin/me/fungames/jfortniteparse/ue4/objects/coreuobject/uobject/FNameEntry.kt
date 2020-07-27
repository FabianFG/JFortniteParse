package me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FNameEntry : UClass {
    var name: String
    var nonCasePreservingHash: UShort
    var casePreservingHash: UShort

    constructor(Ar: FArchive) {
        super.init(Ar)
        name = Ar.readString()
        nonCasePreservingHash = Ar.readUInt16()
        casePreservingHash = Ar.readUInt16()
        super.complete(Ar)
    }

    constructor(name: String, nonCasePreservingHash: UShort, casePreservingHash: UShort) {
        this.name = name
        this.nonCasePreservingHash = nonCasePreservingHash
        this.casePreservingHash = casePreservingHash
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeString(name)
        Ar.writeUInt16(nonCasePreservingHash)
        Ar.writeUInt16(casePreservingHash)
        super.completeWrite(Ar)
    }

    override fun toString() = name
}