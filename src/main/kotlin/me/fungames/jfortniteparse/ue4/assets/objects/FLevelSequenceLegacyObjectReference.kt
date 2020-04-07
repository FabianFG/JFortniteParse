package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FLevelSequenceLegacyObjectReference : UClass {
    var keyGuid : FGuid
    var objectId : FGuid
    var objectPath : String

    constructor(Ar: FArchive) {
        super.init(Ar)
        keyGuid = FGuid(Ar)
        objectId = FGuid(Ar)
        objectPath = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        keyGuid.serialize(Ar)
        objectId.serialize(Ar)
        Ar.writeString(objectPath)
        super.completeWrite(Ar)
    }

    constructor(keyGuid : FGuid, objectId : FGuid, objectPath : String) {
        this.keyGuid = keyGuid
        this.objectId = objectId
        this.objectPath = objectPath
    }
}