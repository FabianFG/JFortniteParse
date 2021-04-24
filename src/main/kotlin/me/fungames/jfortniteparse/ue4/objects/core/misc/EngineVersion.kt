package me.fungames.jfortniteparse.ue4.objects.core.misc

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

/** Utility functions. */
class FEngineVersion : FEngineVersionBase {
    /** Branch name. */
    var branch: String
        //get() = field.replace("+", "/")

    constructor(Ar: FArchive) {
        major = Ar.readUInt16()
        minor = Ar.readUInt16()
        patch = Ar.readUInt16()
        changelist = Ar.readUInt32()
        branch = Ar.readString()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt16(major)
        Ar.writeUInt16(minor)
        Ar.writeUInt16(patch)
        Ar.writeUInt32(changelist)
        Ar.writeString(branch)
    }

    constructor(major: UShort, minor: UShort, patch: UShort, changelist: UInt, branch: String) : super(major, minor, patch, changelist) {
        this.branch = branch
    }

    override fun toString() = toString(EVersionComponent.Branch)

    /** Generates a version string */
    fun toString(lastComponent: EVersionComponent = EVersionComponent.Branch): String {
        var result = "%d".format(major.toInt())
        if (lastComponent >= EVersionComponent.Minor) {
            result += ".%d".format(minor.toInt())
            if (lastComponent >= EVersionComponent.Patch) {
                result += ".%d".format(patch.toInt())
                if (lastComponent >= EVersionComponent.Changelist) {
                    result += "-%d".format(changelist.toInt())
                    if (lastComponent >= EVersionComponent.Branch && branch.isNotEmpty()) {
                        result += "+%s".format(branch)
                    }
                }
            }
        }
        return result;
    }
}