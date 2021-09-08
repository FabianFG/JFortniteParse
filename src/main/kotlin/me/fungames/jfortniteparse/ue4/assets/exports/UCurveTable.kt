package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.OnlyAnnotated
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FRealCurve
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FRichCurve
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FSimpleCurve
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.deserializeUnversionedProperties
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.jvm.internal.Ref

val LOG_CURVE_TABLE: Logger = LoggerFactory.getLogger("CurveTable")

/**
 * Whether the curve table contains simple, rich, or no curves
 */
enum class ECurveTableMode {
    Empty,
    SimpleCurves,
    RichCurves
}

@OnlyAnnotated
class UCurveTable : UObject() {
    /**
     * Map of name of row to row data structure.
     * If curveTableMode is SimpleCurves the value type will be FSimpleCurve
     * If curveTableMode is RichCurves the value type will be FRichCurve
     */
    lateinit var rowMap: Map<FName, FRealCurve>
    lateinit var curveTableMode: ECurveTableMode
        private set

    /*val richCurveRowMap get(): Map<FName, FRichCurve> {
        check(curveTableMode != ECurveTableMode.SimpleCurves)
        return rowMap as Map<FName, FRichCurve>
    }*/

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        // When loading, this should load our RowCurve!
        super.deserialize(Ar, validPos)
        val numRows = Ar.readInt32()
        curveTableMode = ECurveTableMode.values().getOrNull(Ar.read())
            ?: throw ParserException("Unsupported curve mode", Ar)
        val rowStruct = when (curveTableMode) {
            ECurveTableMode.Empty -> throw ParserException("CurveTableMode == ECurveTableMode::Empty, unsupported")
            ECurveTableMode.SimpleCurves -> UScriptStruct(FSimpleCurve::class.java)
            ECurveTableMode.RichCurves -> UScriptStruct(FRichCurve::class.java)
        }
        rowMap = Ar.readTMap(numRows) {
            Ar.readFName() to when (curveTableMode) {
                ECurveTableMode.Empty -> throw ParserException("CurveTableMode == ECurveTableMode::Empty, unsupported")
                ECurveTableMode.SimpleCurves -> FSimpleCurve()
                ECurveTableMode.RichCurves -> FRichCurve()
            }.apply {
                val properties = mutableListOf<FPropertyTag>()
                if (Ar.useUnversionedPropertySerialization) {
                    deserializeUnversionedProperties(properties, rowStruct, Ar)
                } else {
                    deserializeVersionedTaggedProperties(properties, Ar)
                }
                mapToClass(properties, javaClass, this)
            }
        }
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        /*Ar.writeTMap(rowMap) { key, value ->
            Ar.writeFName(key)
            serializeProperties(Ar, value.properties)
        }*/
    }

    /** Function to find the row of a table given its name. */
    @JvmOverloads
    fun findCurve(rowName: FName, warnIfNotFound: Boolean = true): FRealCurve? {
        if (rowName == NAME_None) {
            if (warnIfNotFound) LOG_CURVE_TABLE.warn("UCurveTable::FindCurve : NAME_None is invalid row name for CurveTable '${getPathName()}'.")
            return null
        }

        val foundCurve = rowMap[rowName]

        if (foundCurve == null) {
            if (warnIfNotFound) LOG_CURVE_TABLE.warn("UCurveTable::FindCurve : Row '$rowName' not found in CurveTable '${getPathName()}'.")
            return null
        }

        return foundCurve
    }

    fun toJson(): String {
        val data = rowMap.mapKeys { it.key.text }.mapValues { Package.gson.toJsonTree(it.value) }
        return Package.gson.toJson(data)
    }
}

/**
 * Handle to a particular row in a table.
 */
@UStruct
class FCurveTableRowHandle {
    /** Pointer to table we want a row from */
    @UProperty("CurveTable")
    var curveTable: Lazy<UCurveTable>? = null
    /** Name of row in the table that we want */
    @UProperty("RowName")
    var rowName: FName = NAME_None

    /** Get the curve straight from the row handle */
    @JvmOverloads
    fun getCurve(warnIfNotFound: Boolean = true): FRealCurve? {
        if (curveTable == null) {
            if (rowName != NAME_None) {
                if (warnIfNotFound) LOG_CURVE_TABLE.warn("FCurveTableRowHandle::FindRow : No CurveTable for row $rowName.")
            }
            return null
        }

        return curveTable?.value?.findCurve(rowName, warnIfNotFound)
    }

    /** Evaluate the curve if it is valid
     * @param xValue The input X value to the curve
     * @return The value of the curve if valid, 0 if not
     */
    fun eval(xValue: Float) = getCurve()?.eval(xValue) ?: 0f

    /** Evaluate the curve if it is valid
     * @param xValue The input X value to the curve
     * @param yValue The output Y value from the curve
     * @return True if it filled out yValue with a valid number, false otherwise
     */
    fun eval(xValue: Float, yValue: Ref.FloatRef?): Boolean {
        val curve = getCurve()
        if (curve != null && yValue != null) {
            yValue.element = curve.eval(xValue)
            return true
        }

        return false
    }
}