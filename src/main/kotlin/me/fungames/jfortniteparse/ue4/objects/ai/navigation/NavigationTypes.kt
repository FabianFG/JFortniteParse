package me.fungames.jfortniteparse.ue4.objects.ai.navigation

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

class FNavAgentSelector {
    var packedBits: UInt

    constructor(Ar: FAssetArchive) {
        packedBits = Ar.readUInt32()
    }

    constructor(packedBits: UInt) {
        this.packedBits = packedBits
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        Ar.writeUInt32(packedBits)
    }
}