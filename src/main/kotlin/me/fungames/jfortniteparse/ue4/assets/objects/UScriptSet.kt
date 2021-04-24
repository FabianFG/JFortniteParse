package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

class UScriptSet : UClass {
    var elementsToRemove: MutableList<FProperty>
    val elements: MutableList<FProperty>

    constructor(Ar: FAssetArchive, typeData: PropertyType) {
        super.init(Ar)
        val numElementsToRemove = Ar.readInt32()
        elementsToRemove = ArrayList(numElementsToRemove)
        repeat(numElementsToRemove) {
            try {
                elementsToRemove[it] = FProperty.readPropertyValue(Ar, typeData.innerType!!, FProperty.ReadType.ARRAY)!!
            } catch (e: ParserException) {
                throw ParserException("Failed to read property for index $it in set elements to remove", Ar, e)
            }
        }
        val num = Ar.readInt32()
        elements = ArrayList(num)
        repeat(num) {
            try {
                val element = FProperty.readPropertyValue(Ar, typeData.innerType!!, FProperty.ReadType.ARRAY)!!
                elements.add(element)
            } catch (e: ParserException) {
                throw ParserException("Failed to read element for index $it in set", Ar, e)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(elementsToRemove.size)
        elementsToRemove.forEach { FProperty.writePropertyValue(Ar, it, FProperty.ReadType.ARRAY) }
        Ar.writeInt32(elements.size)
        elements.forEach {
            FProperty.writePropertyValue(Ar, it, FProperty.ReadType.ARRAY)
        }
        super.completeWrite(Ar)
    }

    constructor(elementsToRemove: MutableList<FProperty>, elements: MutableList<FProperty>) {
        this.elementsToRemove = elementsToRemove
        this.elements = elements
    }
}