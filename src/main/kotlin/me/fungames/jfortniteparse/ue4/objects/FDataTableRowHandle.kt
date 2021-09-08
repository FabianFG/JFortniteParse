package me.fungames.jfortniteparse.ue4.objects

import me.fungames.jfortniteparse.LOG_DATA_TABLE
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.exports.UDataTable
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import me.fungames.jfortniteparse.ue4.assets.UProperty as P
import kotlin.jvm.JvmField as F

/** Handle to a particular row in a table */
@UStruct
class FDataTableRowHandle {
    /** Pointer to table we want a row from */
    @F @P("DataTable")
    var dataTable: Lazy<UDataTable>? = null

    /** Name of row in the table that we want */
    @F @P("RowName")
    var rowName = NAME_None

    /** Returns true if this handle is specifically pointing to nothing */
    val isNull get() = dataTable == null && rowName.isNone()

    /** Get the row straight from the row handle */
    val row: UObject?
        get() {
            val dataTable = dataTable?.value
            if (dataTable == null) {
                if (!rowName.isNone()) {
                    LOG_DATA_TABLE.warn("FDataTableRowHandle::GetRow : No DataTable for row $rowName.")
                }
                return null
            }

            return dataTable.findRow(rowName)
        }

    /** Get the row straight from the row handle, mapped to a Java class */
    inline fun <reified T : FTableRowBase> getRowMapped() = row?.mapToClass(T::class.java)

    /** Get the row straight from the row handle, mapped to a Java class */
    fun <T : FTableRowBase> getRowMapped(clazz: Class<T>) = row?.mapToClass(clazz)

    override fun toString() = toString(false)

    fun toString(useFullPath: Boolean): String {
        val dataTable = dataTable?.value
        return if (dataTable == null) {
            "No Data Table Specified, Row: $rowName"
        } else {
            "Table: %s, Row: %s".format(if (useFullPath) dataTable.getPathName() else dataTable.name, rowName)
        }
    }
}