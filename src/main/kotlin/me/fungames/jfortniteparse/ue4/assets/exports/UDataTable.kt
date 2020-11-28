package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.MissingSchemaException
import me.fungames.jfortniteparse.ue4.assets.*
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.FTableRowBase
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.deserializeUnversionedProperties

@OnlyAnnotated
open class UDataTable : UObject {
    @JvmField @UProperty var RowStruct: FPackageIndex? = null // UScriptStruct
    @JvmField @UProperty var bStripFromClientBuilds: Boolean? = null
    @JvmField @UProperty var bIgnoreExtraFields: Boolean? = null
    @JvmField @UProperty var bIgnoreMissingFields: Boolean? = null
    @JvmField @UProperty var ImportKeyField: String? = null
    lateinit var rowStructName: FName

    var rows: MutableMap<FName, UObject>

    constructor() : this(mutableMapOf())

    constructor(rows: MutableMap<FName, UObject>) : super() {
        this.rows = rows
    }

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        if (!::rowStructName.isInitialized) {
            rowStructName = if (Ar.owner is IoPackage) {
                (Ar.owner as IoPackage).run { RowStruct?.getImportObject()?.findScriptObjectEntry()?.objectName?.toName() }
            } else {
                (Ar.owner as PakPackage).run { RowStruct?.getResource()?.objectName }
            } ?: FName.NAME_None
        }
        val clazz = ObjectTypeRegistry.structs[rowStructName.text]
        if (Ar.useUnversionedPropertySerialization && clazz == null) {
            throw MissingSchemaException("Missing schema for row struct $rowStructName")
        }
        rows = Ar.readTMap {
            val key = Ar.readFName()
            val rowProperties = mutableListOf<FPropertyTag>()
            if (Ar.useUnversionedPropertySerialization) {
                deserializeUnversionedProperties(rowProperties, clazz!!, Ar)
            } else {
                deserializeTaggedProperties(rowProperties, Ar)
            }
            val value = UObject(rowProperties, null, "RowStruct")
            key to value
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

    fun findRow(rowName: String) = rows[FName.dummy(rowName)]
    fun findRow(rowName: FName) = rows[rowName]

    @Suppress("UNCHECKED_CAST")
    fun <T> findRowMapped(rowName: FName): T? where T : FTableRowBase =
        findRow(rowName)?.mapToClass(ObjectTypeRegistry.structs[rowStructName.text] as Class<*>) as T?

    fun toJson(): String {
        val data = rows.mapKeys { it.key.text }.mapValues { Package.gson.toJsonTree(it.value) }
        return Package.gson.toJson(data)
    }
}