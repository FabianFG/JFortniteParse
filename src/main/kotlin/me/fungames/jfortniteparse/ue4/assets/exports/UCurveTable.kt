package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.engine.curves.FRealCurve
import me.fungames.jfortniteparse.ue4.objects.engine.curves.SimpleCurve
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.deserializeUnversionedProperties
import mu.KotlinLogging
import kotlin.jvm.internal.Ref

/**
 * Whether the curve table contains simple, rich, or no curves
 */
enum class ECurveTableMode {
    Empty,
    SimpleCurves,
    RichCurves
}

@ExperimentalUnsignedTypes
class UCurveTable : UObject() {
    companion object {
        internal val LOGGER = KotlinLogging.logger("LogCurveTable")
    }
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
        curveTableMode = when (Ar.readUInt8().toInt()) {
            0 -> ECurveTableMode.Empty
            1 -> ECurveTableMode.SimpleCurves
            2 -> ECurveTableMode.RichCurves
            else -> throw ParserException("Unsupported curve mode", Ar)
        }
        rowMap = Ar.readTMap(numRows) {
            Ar.readFName() to when (curveTableMode) {
                ECurveTableMode.Empty -> TODO()
                ECurveTableMode.SimpleCurves -> SimpleCurve().apply { mapToClass(if (Ar.useUnversionedPropertySerialization) {
                    deserializeUnversionedProperties(javaClass, Ar)
                } else {
                    deserializeTaggedProperties(Ar)
                }, javaClass, this) }
                ECurveTableMode.RichCurves -> TODO() // RichCurve()
            }
        }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
        /*Ar.writeTMap(rowMap) { key, value ->
            Ar.writeFName(key)
            serializeProperties(Ar, value.properties)
        }*/
        super.completeWrite(Ar)
    }

    /** Function to find the row of a table given its name. */
    @JvmOverloads
    fun findCurve(RowName: FName, warnIfNotFound: Boolean = true): FRealCurve? {
        if (RowName == NAME_None) {
            if (warnIfNotFound) LOGGER.warn("UCurveTable::FindCurve : NAME_None is invalid row name for CurveTable '%s'.".format(pathName))
            return null
        }

        val foundCurve = rowMap[RowName]

        if (foundCurve == null) {
            if (warnIfNotFound) LOGGER.warn("UCurveTable::FindCurve : Row '%s' not found in CurveTable '%s'.".format(RowName.toString(), pathName))
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
@ExperimentalUnsignedTypes
class FCurveTableRowHandle {
    /** Pointer to table we want a row from */
    @UProperty(name = "CurveTable")
    var curveTable: UCurveTable? = null
    /** Name of row in the table that we want */
    @UProperty(name = "RowName")
    var rowName: FName = NAME_None

    /** Get the curve straight from the row handle */
    @JvmOverloads
    fun getCurve(warnIfNotFound: Boolean = true): FRealCurve? {
        if (curveTable == null) {
            if (rowName != NAME_None) {
                if (warnIfNotFound) UCurveTable.LOGGER.warn("LogCurveTable", "FCurveTableRowHandle::FindRow : No CurveTable for row %s.".format(rowName.toString()))
            }
            return null
        }

        return curveTable!!.findCurve(rowName, warnIfNotFound)
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