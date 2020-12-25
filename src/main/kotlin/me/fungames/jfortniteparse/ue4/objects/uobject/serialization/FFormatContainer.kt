package me.fungames.jfortniteparse.ue4.objects.uobject.serialization

import me.fungames.jfortniteparse.ue4.assets.objects.FByteBulkData
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

class FFormatContainer {
    var formats: MutableMap<FName, FByteBulkData>

    constructor(Ar: FAssetArchive) {
        formats = Ar.readTMap { Ar.readFName() to FByteBulkData(Ar) }
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        Ar.writeTMap(formats) { k, v -> Ar.writeFName(k); v.serialize(Ar) }
    }
}