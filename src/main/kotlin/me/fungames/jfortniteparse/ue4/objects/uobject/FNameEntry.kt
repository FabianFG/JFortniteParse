package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FNameEntry {
    var name: String
    var nonCasePreservingHash: UShort
    var casePreservingHash: UShort

    constructor(Ar: FArchive) {
        name = Ar.readString()
        nonCasePreservingHash = Ar.readUInt16()
        casePreservingHash = Ar.readUInt16()
    }

    constructor(name: String, nonCasePreservingHash: UShort, casePreservingHash: UShort) {
        this.name = name
        this.nonCasePreservingHash = nonCasePreservingHash
        this.casePreservingHash = casePreservingHash
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeString(name)
        Ar.writeUInt16(nonCasePreservingHash)
        Ar.writeUInt16(casePreservingHash)
    }

    override fun toString() = name
}