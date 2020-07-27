package me.fungames.jfortniteparse.ue4.assets.exports.blueprints

/*
@ExperimentalUnsignedTypes
class UFunction : UExport {
    override var baseObject: UObject
    var functionFlags : EFunctionFlags

    var eventGraphFunction : UFunction
    var eventGraphCallOffset : Int

    constructor(Ar: FAssetArchive, exportObject : FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject, false)
        functionFlags = EFunctionFlags.valueOfFlag(Ar.readUInt32())

        // Replication info
        if (functionFlags.value and EFunctionFlags.FUNC_Net.value != 0u) {
            Ar.readInt16() //Unused RepOffset
        }

        eventGraphFunction = UFunction(Ar, exportObject)
        eventGraphCallOffset = Ar.readInt32()
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {

    }

}*/
