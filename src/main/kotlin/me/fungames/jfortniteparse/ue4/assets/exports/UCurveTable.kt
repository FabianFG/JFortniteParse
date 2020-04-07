package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class UCurveTable : UExport {
    override var baseObject: UObject
    var curveTableMode : ECurveTableMode
    var rows : MutableMap<FName, UObject>

    constructor(Ar: FAssetArchive, exportObject : FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        curveTableMode = when(Ar.readUInt8().toInt()) {
            0 -> ECurveTableMode.Empty
            1 -> ECurveTableMode.SimpleCurves
            2 -> ECurveTableMode.RichCurves
            else -> throw ParserException("Unsupported curve mode", Ar)
        }
        rows = Ar.readTMap {
            val rowType = when(curveTableMode) {
                ECurveTableMode.Empty -> "Empty"
                ECurveTableMode.SimpleCurves -> "SimpleCurveKey"
                ECurveTableMode.RichCurves -> "RichCurveKey"
            }
            Ar.readFName() to UObject(
                UObject.deserializeProperties(Ar),
                null,
                rowType
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

    constructor(exportType: String, baseObject : UObject, curveTableMode: ECurveTableMode, rows : MutableMap<FName, UObject>) : super(exportType) {
        this.baseObject = baseObject
        this.curveTableMode = curveTableMode
        this.rows = rows
    }

    enum class ECurveTableMode {
        Empty,
        SimpleCurves,
        RichCurves
    }

}