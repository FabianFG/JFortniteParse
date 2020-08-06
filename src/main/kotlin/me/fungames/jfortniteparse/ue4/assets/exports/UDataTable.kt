package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex

@ExperimentalUnsignedTypes
class UDataTable : UObject {
    var RowStruct: FPackageIndex? = null
    lateinit var rows: MutableMap<FName, UObject>

    constructor() : this(mutableMapOf())

    constructor(rows: MutableMap<FName, UObject>) : super() {
        this.rows = rows
    }

    constructor(exportObject: FObjectExport) : super(exportObject)

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        rows = Ar.readTMap {
            Ar.readFName() to UObject(deserializeProperties(Ar), null, "RowStruct")
                .apply { mapToClass(properties, javaClass, this) }
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
}