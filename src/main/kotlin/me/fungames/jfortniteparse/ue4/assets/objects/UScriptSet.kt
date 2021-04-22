package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

class UScriptSet : UClass {
    var numKeysToRemove: Int
    val setData: MutableList<FProperty>

    constructor(Ar: FAssetArchive, typeData: PropertyType) {
        super.init(Ar)
        numKeysToRemove = Ar.readInt32()
        if (numKeysToRemove != 0) {
            for (i in 0 until numKeysToRemove) {
                FProperty.readPropertyValue(Ar, typeData.innerType!!, FProperty.ReadType.ARRAY)
            }
        }
        val length = Ar.readInt32()
        setData = mutableListOf()
        for (i in 0 until length) {
            try {
                val element = FProperty.readPropertyValue(Ar, typeData.innerType!!, FProperty.ReadType.ARRAY)!!
                setData.add(element)
            } catch (e: ParserException) {
                throw ParserException("Failed to read element for index $i in set", Ar, e)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(numKeysToRemove)
        Ar.writeInt32(setData.size)
        setData.forEach {
            FProperty.writePropertyValue(Ar, it, FProperty.ReadType.ARRAY)
        }
        super.completeWrite(Ar)
    }

    constructor(numKeysToRemove: Int, setData: MutableList<FProperty>) {
        this.numKeysToRemove = numKeysToRemove
        this.setData = setData
    }
}