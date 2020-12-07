package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

class UScriptMap : UClass {
    var numKeysToRemove: Int
    val mapData: MutableMap<FProperty, FProperty>

    constructor(Ar: FAssetArchive, typeData: FPropertyTypeData) {
        super.init(Ar)
        numKeysToRemove = Ar.readInt32()
        if (numKeysToRemove != 0) {
            for (i in 0 until numKeysToRemove) {
                FProperty.readPropertyValue(Ar, typeData.innerType!!, FProperty.ReadType.MAP)
            }
        }
        val length = Ar.readInt32()
        mapData = mutableMapOf()
        for (i in 0 until length) {
            var isReadingValue = false
            try {
                val key = FProperty.readPropertyValue(Ar, typeData.innerType!!, FProperty.ReadType.MAP)!!
                isReadingValue = true
                val value = FProperty.readPropertyValue(Ar, typeData.valueType!!, FProperty.ReadType.MAP)!!
                mapData[key] = value
            } catch (e: ParserException) {
                throw ParserException("Failed to read ${if (isReadingValue) "value" else "key"} for index $i in map", Ar, e)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(numKeysToRemove)
        Ar.writeInt32(mapData.size)
        mapData.forEach {
            FProperty.writePropertyValue(Ar, it.key, FProperty.ReadType.MAP)
            FProperty.writePropertyValue(Ar, it.value, FProperty.ReadType.MAP)
        }
        super.completeWrite(Ar)
    }

    constructor(numKeysToRemove: Int, mapData: MutableMap<FProperty, FProperty>) {
        this.numKeysToRemove = numKeysToRemove
        this.mapData = mapData
    }
}