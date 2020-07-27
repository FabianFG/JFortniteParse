package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FObjectExport

@ExperimentalUnsignedTypes
class UCurveTable : UObject {
    var curveTableMode: ECurveTableMode
    var rows: MutableMap<FName, UObject>

    constructor(Ar: FAssetArchive, exportObject: FObjectExport) : super(Ar, exportObject) { // When loading, this should load our RowCurve!
        val numRows = Ar.readInt32()
        curveTableMode = when (Ar.readUInt8().toInt()) {
            0 -> ECurveTableMode.Empty
            1 -> ECurveTableMode.SimpleCurves
            2 -> ECurveTableMode.RichCurves
            else -> throw ParserException("Unsupported curve mode", Ar)
        }
        rows = Ar.readTMap(numRows) {
            val rowType = when (curveTableMode) {
                ECurveTableMode.Empty -> "Empty"
                ECurveTableMode.SimpleCurves -> "SimpleCurveKey"
                ECurveTableMode.RichCurves -> "RichCurveKey"
            }
            Ar.readFName() to UObject(
                deserializeProperties(Ar),
                null,
                rowType
            )
        }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        Ar.writeTMap(rows) { key, value ->
            Ar.writeFName(key)
            serializeProperties(Ar, value.properties)
        }
        super.completeWrite(Ar)
    }

    /*constructor(exportType: String, baseObject: UObject, curveTableMode: ECurveTableMode, rows: MutableMap<FName, UObject>) : super(exportType) {
        this.baseObject = baseObject
        this.curveTableMode = curveTableMode
        this.rows = rows
    }*/

    fun toJson(): String {
        val data =
            rows.mapKeys { it.key.text }.mapValues { Package.gson.toJsonTree(it.value) }
        return Package.gson.toJson(data)
    }

    enum class ECurveTableMode {
        Empty,
        SimpleCurves,
        RichCurves
    }
}