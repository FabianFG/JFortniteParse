package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter


typealias FMetaDataMap = MutableMap<FName, String>

@ExperimentalUnsignedTypes
class UStringTable : UExport {
    override var baseObject: UObject

    var tableNamespace : String
    var entries: MutableMap<String, String>
    var keysToMetadata : MutableMap<String, FMetaDataMap>

    constructor(Ar: FAssetArchive, exportObject : FObjectExport) : super(exportObject) {
        super.init(Ar)
        baseObject = UObject(Ar, exportObject)
        tableNamespace = Ar.readString()
        entries = Ar.readTMap { Ar.readString() to Ar.readString() }
        keysToMetadata = Ar.readTMap { Ar.readString() to Ar.readTMap { Ar.readFName() to Ar.readString() } }
        super.complete(Ar)
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        baseObject.serialize(Ar)
        Ar.writeString(tableNamespace)
        Ar.writeTMap(entries) { key, value ->
            Ar.writeString(key)
            Ar.writeString(value)
        }
        Ar.writeTMap(keysToMetadata) { key, metadata ->
            Ar.writeString(key)
            Ar.writeTMap(metadata) { metaKey, metaValue ->
                Ar.writeFName(metaKey)
                Ar.writeString(metaValue)
            }
        }
        super.completeWrite(Ar)
    }

}