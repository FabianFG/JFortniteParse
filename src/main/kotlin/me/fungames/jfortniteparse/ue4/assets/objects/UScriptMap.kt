package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

class UScriptMap {
    var keysToRemove: MutableList<FProperty>
    val entries: MutableMap<FProperty, FProperty>

    constructor(Ar: FAssetArchive, typeData: PropertyType) {
        val numKeysToRemove = Ar.readInt32()
        keysToRemove = ArrayList(numKeysToRemove)
        repeat(numKeysToRemove) {
            try {
                keysToRemove[it] = FProperty.readPropertyValue(Ar, typeData.innerType!!, FProperty.ReadType.MAP)!!
            } catch (e: ParserException) {
                throw ParserException("Failed to read property for index $it in map keys to remove", Ar, e)
            }
        }
        val numEntries = Ar.readInt32()
        entries = mutableMapOf()
        repeat(numEntries) {
            var isReadingValue = false
            try {
                val key = FProperty.readPropertyValue(Ar, typeData.innerType!!, FProperty.ReadType.MAP)!!
                isReadingValue = true
                val value = FProperty.readPropertyValue(Ar, typeData.valueType!!, FProperty.ReadType.MAP)!!
                entries[key] = value
            } catch (e: ParserException) {
                throw ParserException("Failed to read ${if (isReadingValue) "value" else "key"} for index $it in map", Ar, e)
            }
        }
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        Ar.writeInt32(keysToRemove.size)
        keysToRemove.forEach { FProperty.writePropertyValue(Ar, it, FProperty.ReadType.MAP) }
        Ar.writeInt32(entries.size)
        entries.forEach {
            FProperty.writePropertyValue(Ar, it.key, FProperty.ReadType.MAP)
            FProperty.writePropertyValue(Ar, it.value, FProperty.ReadType.MAP)
        }
    }

    constructor(keysToRemove: MutableList<FProperty>, entries: MutableMap<FProperty, FProperty>) {
        this.keysToRemove = keysToRemove
        this.entries = entries
    }
}