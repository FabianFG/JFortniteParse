package me.fungames.jfortniteparse.ue4.assets.exports.blueprints

/*
@ExperimentalUnsignedTypes
class UFunction : UObject() {
    var functionFlags: EFunctionFlags

    var eventGraphFunction: UFunction
    var eventGraphCallOffset: Int

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        functionFlags = EFunctionFlags.valueOfFlag(Ar.readUInt32())

        // Replication info
        if (functionFlags.value and EFunctionFlags.FUNC_Net.value != 0u) {
            Ar.readInt16() //Unused RepOffset
        }

        eventGraphFunction = UFunction(Ar, exportObject)
        eventGraphCallOffset = Ar.readInt32()
        super.complete(Ar)
    }
}*/