package me.fungames.jfortniteparse.ue4.assets.exports.blueprints

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.EFunctionFlags
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.reader.FArchive

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
