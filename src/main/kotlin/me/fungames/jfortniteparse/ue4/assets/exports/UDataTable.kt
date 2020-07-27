package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FObjectExport

@ExperimentalUnsignedTypes
class UDataTable : UObject {
    var rows: MutableMap<FName, UObject>

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(Ar, exportObject) {
        rows = Ar.readTMap {
            Ar.readFName() to UObject(deserializeProperties(Ar), null, "RowStruct")
        }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        Ar.writeTMap(rows) { key, value ->
            Ar.writeFName(key)
            serializeProperties(Ar, value.properties)
        }
        super.completeWrite(Ar)
    }

    fun toJson(): String {
        val data = rows.mapKeys { it.key.text }.mapValues { Package.gson.toJsonTree(it.value) }
        return Package.gson.toJson(data)
    }

    constructor(rows: MutableMap<FName, UObject>) : super(mutableListOf(), null, "DataTable") {
        this.rows = rows
    }
}