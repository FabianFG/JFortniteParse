package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

class UScriptArray : UClass {
    var innerTag: FPropertyTag? = null
    val contents: MutableList<FPropertyTagType>
    val data: MutableList<Any>
    var innerType: String

    constructor(Ar: FAssetArchive, tag: FPropertyTag) {
        super.init(Ar)
        this.innerType = tag.innerType.text
        val elementCount = Ar.readUInt32()
        if (innerType == "StructProperty" || innerType == "ArrayProperty") {
            innerTag = if (Ar.useUnversionedPropertySerialization) tag else FPropertyTag(Ar, false)
        }
        contents = mutableListOf()
        for (i in 0u until elementCount) {
            val content = FPropertyTagType.readFPropertyTagType(Ar, tag.innerType.text, innerTag, FPropertyTagType.ReadType.ARRAY)
            if (content != null)
                contents.add(content)
            else
                logger.warn("Failed to read array content of type $innerType at ${Ar.pos()}, index $i")
        }

        data = mutableListOf()
        contents.forEach { data.add(it.getTagTypeValueLegacy()) }

        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(contents.size)
        innerTag?.serialize(Ar, false)
        contents.forEach {
            FPropertyTagType.writeFPropertyTagType(Ar, it, FPropertyTagType.ReadType.ARRAY)
        }
        super.completeWrite(Ar)
    }

    override fun toString() = "UScriptArray{size=${contents.size}}"

    constructor(innerTag: FPropertyTag?, contents: MutableList<FPropertyTagType>, innerType: String) {
        this.innerTag = innerTag
        this.contents = contents
        this.data = mutableListOf()
        contents.forEach { this.data.add(it.getTagTypeValueLegacy()) }
        this.innerType = innerType
    }
}
