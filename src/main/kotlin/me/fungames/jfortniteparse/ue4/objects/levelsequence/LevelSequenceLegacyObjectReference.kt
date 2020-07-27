package me.fungames.jfortniteparse.ue4.objects.levelsequence

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FLevelSequenceLegacyObjectReference : UClass {
    var keyGuid: FGuid
    var objectId: FGuid
    var objectPath: String

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

    constructor(keyGuid: FGuid, objectId: FGuid, objectPath: String) {
        this.keyGuid = keyGuid
        this.objectId = objectId
        this.objectPath = objectPath
    }
}

@ExperimentalUnsignedTypes
class FLevelSequenceObjectReferenceMap : UClass {
    var mapData: Array<FLevelSequenceLegacyObjectReference>

    constructor(Ar: FArchive) {
        super.init(Ar)
        mapData = Ar.readTArray { FLevelSequenceLegacyObjectReference(Ar) }
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeTArray(mapData) { it.serialize(Ar) }
        super.completeWrite(Ar)
    }

    constructor(mapData: Array<FLevelSequenceLegacyObjectReference>) {
        this.mapData = mapData
    }
}