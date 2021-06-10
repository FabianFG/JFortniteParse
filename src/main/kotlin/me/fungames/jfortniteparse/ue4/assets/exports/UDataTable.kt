package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.deserializeUnversionedProperties

@OnlyAnnotated
open class UDataTable : UObject {
    @JvmField @UProperty var RowStruct: Lazy<UScriptStruct>? = null
    @JvmField @UProperty var bStripFromClientBuilds: Boolean? = null
    @JvmField @UProperty var bIgnoreExtraFields: Boolean? = null
    @JvmField @UProperty var bIgnoreMissingFields: Boolean? = null
    @JvmField @UProperty var ImportKeyField: String? = null

    var rows: MutableMap<FName, UObject>

    constructor() : this(mutableMapOf())

    constructor(rows: MutableMap<FName, UObject>) : super() {
        this.rows = rows
    }

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        rows = Ar.readTMap {
            val key = Ar.readFName()
            val rowProperties = mutableListOf<FPropertyTag>()
            if (Ar.useUnversionedPropertySerialization) {
                deserializeUnversionedProperties(rowProperties, RowStruct!!.value, Ar)
            } else {
                deserializeVersionedTaggedProperties(rowProperties, Ar)
            }
            val value = UObject(rowProperties)
            key to value
        }
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        Ar.writeTMap(rows) { key, value ->
            Ar.writeFName(key)
            serializeProperties(Ar, value.properties)
        }
    }

    fun findRow(rowName: String) = rows[FName.dummy(rowName)]
    fun findRow(rowName: FName) = rows[rowName]

    inline fun <reified T : FTableRowBase> findRowMapped(rowName: FName): T? =
        findRow(rowName)?.mapToClass(T::class.java)

    fun toJson(): String {
        val data = rows.mapKeys { it.key.text }.mapValues { Package.gson.toJsonTree(it.value) }
        return Package.gson.toJson(data)
    }
}