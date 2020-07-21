package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class UDataTable : UExport {
    override var baseObject: UObject
    var rows : MutableMap<FName, UObject>

    constructor(Ar: FAssetArchive, exportObject : FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        rows = Ar.readTMap { Ar.readFName() to UObject(
            UObject.deserializeProperties(Ar),
            null,
            "RowStruct"
        )
        }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        Ar.writeTMap(rows) {key, value ->
            Ar.writeFName(key)
            UObject.serializeProperties(Ar, value.properties)
        }
        super.completeWrite(Ar)
    }

    fun toJson(): String {
        val data =
            rows.mapKeys { it.key.text }.mapValues { Package.gson.toJsonTree(it.value) }
        return Package.gson.toJson(data)
    }

    constructor(exportObject: FObjectExport, baseObject : UObject, rows : MutableMap<FName, UObject>) : super(exportObject) {
        this.baseObject = baseObject
        this.rows = rows
    }

}