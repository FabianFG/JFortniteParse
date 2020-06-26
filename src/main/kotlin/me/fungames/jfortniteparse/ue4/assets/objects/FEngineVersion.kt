package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FEngineVersion : UClass {
    var major: UShort
    var minor: UShort
    var patch: UShort
    var changelist: UInt
    var branch: String

    constructor(Ar: FArchive) {
        super.init(Ar)
        major = Ar.readUInt16()
        minor = Ar.readUInt16()
        patch = Ar.readUInt16()
        changelist = Ar.readUInt32()
        branch = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt16(major)
        Ar.writeUInt16(minor)
        Ar.writeUInt16(patch)
        Ar.writeUInt32(changelist)
        Ar.writeString(branch)
        super.completeWrite(Ar)
    }

    constructor(major: UShort, minor: UShort, patch: UShort, changelist: UInt, branch: String) {
        this.major = major
        this.minor = minor
        this.patch = patch
        this.changelist = changelist
        this.branch = branch
    }
}