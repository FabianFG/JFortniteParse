package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.UObject
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class UDataTable : UEExport {
    override var baseObject: UObject
    var rows : MutableMap<FName, UObject>

    constructor(Ar: FAssetArchive, exportObject : FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        rows = Ar.readTMap { Ar.readFName() to UObject(UObject.deserializeProperties(Ar), false, null, "RowStruct") }
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

    constructor(exportObject: FObjectExport, baseObject : UObject, rows : MutableMap<FName, UObject>) : super(exportObject) {
        this.baseObject = baseObject
        this.rows = rows
    }

}