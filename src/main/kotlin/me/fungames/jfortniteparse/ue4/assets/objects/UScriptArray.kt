package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

class UScriptArray : UClass {
    var innerTag: FPropertyTag? = null
    val contents: MutableList<FProperty>

    constructor(Ar: FAssetArchive, typeData: PropertyType) {
        super.init(Ar)
        val elementCount = Ar.readInt32()
        val innerType = typeData.innerType!!
        val type = innerType.type.text
        if (!Ar.useUnversionedPropertySerialization && (type == "StructProperty" || type == "ArrayProperty")) {
            innerTag = FPropertyTag(Ar, false)
        }
        contents = ArrayList(elementCount)
        for (i in 0 until elementCount) {
            val content = FProperty.readPropertyValue(Ar, innerTag?.typeData ?: innerType, FProperty.ReadType.ARRAY)
            if (content != null)
                contents.add(content)
            else
                logger.warn("Failed to read array content of type $innerType at ${Ar.pos()}, index $i")
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(contents.size)
        innerTag?.serialize(Ar, false)
        contents.forEach {
            FProperty.writePropertyValue(Ar, it, FProperty.ReadType.ARRAY)
        }
        super.completeWrite(Ar)
    }

    override fun toString() = "UScriptArray{size=${contents.size}}"

    constructor(innerTag: FPropertyTag?, contents: MutableList<FProperty>) {
        this.innerTag = innerTag
        this.contents = contents
    }
}
