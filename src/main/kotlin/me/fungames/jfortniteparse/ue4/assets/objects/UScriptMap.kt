package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class UScriptMap : UClass {
    var numKeysToRemove: Int
    val mapData: MutableMap<FPropertyTagType, FPropertyTagType>
    val keyType: String
    val valueType: String

    constructor(Ar: FAssetArchive, tag: FPropertyTag) {
        this.keyType = tag.innerType.text
        this.valueType = tag.valueType.text
        super.init(Ar)
        numKeysToRemove = Ar.readInt32()
        if (numKeysToRemove != 0) {
            for (i in 0 until numKeysToRemove) {
                FPropertyTagType.readFPropertyTagType(Ar, keyType, tag, FPropertyTagType.Type.MAP)
            }
        }
        val length = Ar.readInt32()
        mapData = mutableMapOf()
        for (i in 0 until length) {
            try {
                mapData[FPropertyTagType.readFPropertyTagType(Ar, keyType, tag, FPropertyTagType.Type.MAP)!!] =
                    FPropertyTagType.readFPropertyTagType(Ar, valueType, tag, FPropertyTagType.Type.MAP)!!
            } catch (e: ParserException) {
                throw ParserException("Failed to read key/value pair for index $i in map", Ar, e)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(numKeysToRemove)
        Ar.writeInt32(mapData.size)
        mapData.forEach {
            FPropertyTagType.writeFPropertyTagType(Ar, it.key, FPropertyTagType.Type.MAP)
            FPropertyTagType.writeFPropertyTagType(Ar, it.value, FPropertyTagType.Type.MAP)
        }
        super.completeWrite(Ar)
    }

    constructor(numKeyToRemove: Int, mapData: MutableMap<FPropertyTagType, FPropertyTagType>, keyType: String, valueType: String) {
        this.numKeysToRemove = numKeyToRemove
        this.mapData = mapData
        this.keyType = keyType
        this.valueType = valueType
    }
}