package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@ExperimentalUnsignedTypes
class UCurveTable : UObject() {
    lateinit var curveTableMode: ECurveTableMode
    lateinit var rows: MutableMap<FName, UObject>

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
        rows = Ar.readTMap(numRows) {
            Ar.readFName() to UObject(deserializeProperties(Ar), null, when (curveTableMode) {
                ECurveTableMode.Empty -> "Empty"
                ECurveTableMode.SimpleCurves -> "SimpleCurveKey"
                ECurveTableMode.RichCurves -> "RichCurveKey"
            }).apply { mapToClass(properties, javaClass, this) }
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