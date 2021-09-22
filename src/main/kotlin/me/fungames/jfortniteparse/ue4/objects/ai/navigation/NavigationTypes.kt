package me.fungames.jfortniteparse.ue4.objects.ai.navigation

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FNavAgentSelector {
    var packedBits: UInt

    constructor(Ar: FArchive) {
        packedBits = Ar.readUInt32()
    }

    constructor(packedBits: UInt) {
        this.packedBits = packedBits
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt32(packedBits)
    }
}