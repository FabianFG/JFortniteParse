package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FLevelSequenceObjectReferenceMap : UClass {
    var mapData : Array<FLevelSequenceLegacyObjectReference>

    constructor(Ar: FArchive) {
        super.init(Ar)
        mapData = Ar.readTArray {
            FLevelSequenceLegacyObjectReference(
                Ar
            )
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeTArray(mapData) {it.serialize(Ar)}
        super.completeWrite(Ar)
    }

    constructor(mapData : Array<FLevelSequenceLegacyObjectReference>) {
        this.mapData = mapData
    }
}