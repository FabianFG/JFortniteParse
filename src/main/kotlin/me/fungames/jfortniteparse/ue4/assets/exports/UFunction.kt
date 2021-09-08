package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.enums.EFunctionFlags
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive

@OnlyAnnotated
class UFunction : UStruct() {
    var functionFlags = 0u
    var eventGraphFunction: Lazy<UFunction>? = null
    var eventGraphCallOffset = 0

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        functionFlags = Ar.readUInt32()

        // Replication info
        if (functionFlags and EFunctionFlags.FUNC_Net.value != 0u) {
            // Unused.
            val repOffset = Ar.readInt16()
        }

        if (Ar.ver >= 451 /*VER_UE4_SERIALIZE_BLUEPRINT_EVENTGRAPH_FASTCALLS_IN_UFUNCTION*/) {
            eventGraphFunction = Ar.readObject()
            eventGraphCallOffset = Ar.readInt32()
        }
    }
}