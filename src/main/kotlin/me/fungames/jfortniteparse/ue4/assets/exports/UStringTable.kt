package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

typealias FMetaDataMap = MutableMap<FName, String>

class UStringTable : UObject() {
    lateinit var tableNamespace: String
    lateinit var entries: MutableMap<String, String>
    lateinit var keysToMetadata: MutableMap<String, FMetaDataMap>

    override fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.deserialize(Ar, validPos)
        tableNamespace = Ar.readString()
        entries = Ar.readTMap { Ar.readString() to Ar.readString() }
        keysToMetadata = Ar.readTMap { Ar.readString() to Ar.readTMap { Ar.readFName() to Ar.readString() } }
    }

    override fun serialize(Ar: FAssetArchiveWriter) {
        super.serialize(Ar)
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
    }
}