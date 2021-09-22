package me.fungames.jfortniteparse.ue4.objects.levelsequence

import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FLevelSequenceLegacyObjectReference {
    var keyGuid: FGuid
    var objectId: FGuid
    var objectPath: String

    constructor(Ar: FArchive) {
        keyGuid = FGuid(Ar)
        objectId = FGuid(Ar)
        objectPath = Ar.readString()
    }

    fun serialize(Ar: FArchiveWriter) {
        keyGuid.serialize(Ar)
        objectId.serialize(Ar)
        Ar.writeString(objectPath)
    }

    constructor(keyGuid: FGuid, objectId: FGuid, objectPath: String) {
        this.keyGuid = keyGuid
        this.objectId = objectId
        this.objectPath = objectPath
    }
}

class FLevelSequenceObjectReferenceMap {
    var mapData: Array<FLevelSequenceLegacyObjectReference>

    constructor(Ar: FArchive) {
        mapData = Ar.readTArray { FLevelSequenceLegacyObjectReference(Ar) }
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeTArray(mapData) { it.serialize(Ar) }
    }

    constructor(mapData: Array<FLevelSequenceLegacyObjectReference>) {
        this.mapData = mapData
    }
}