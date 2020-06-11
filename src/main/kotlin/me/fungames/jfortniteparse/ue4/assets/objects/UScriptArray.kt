package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
class UScriptArray : UClass {
    var arrayTag: FPropertyTag? = null
    val contents: MutableList<FPropertyTagType>
    val data: MutableList<Any>
    var innerType: String

    constructor(Ar: FAssetArchive, innerType: String) {
        super.init(Ar)
        this.innerType = innerType
        val elementCount = Ar.readUInt32()
        if (innerType == "StructProperty" || innerType == "ArrayProperty") {
            arrayTag = FPropertyTag(Ar, false)
            if (arrayTag == null)
                throw ParserException("Couldn't read ArrayProperty with inner type $innerType")
        }
        val innerTagData = arrayTag?.tagData
        contents = mutableListOf()
        for (i in 0u until elementCount) {
            val content = FPropertyTagType.readFPropertyTagType(Ar, innerType, innerTagData, FPropertyTagType.Type.ARRAY)
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
        arrayTag?.serialize(Ar, false)
        contents.forEach {
            FPropertyTagType.writeFPropertyTagType(Ar, it, FPropertyTagType.Type.ARRAY)
        }
        super.completeWrite(Ar)
    }

    constructor(arrayTag: FPropertyTag?, contents: MutableList<FPropertyTagType>, innerType: String) {
        this.arrayTag = arrayTag
        this.contents = contents
        this.data = mutableListOf()
        contents.forEach { this.data.add(it.getTagTypeValueLegacy()) }
        this.innerType = innerType
    }
}
