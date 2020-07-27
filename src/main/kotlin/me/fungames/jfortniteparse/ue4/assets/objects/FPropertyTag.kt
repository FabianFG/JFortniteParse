package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid

@ExperimentalUnsignedTypes
class FPropertyTag : UClass {
    var name: FName
    lateinit var propertyType: FName
    var size = -1
    var arrayIndex = -1
    var tagData: FPropertyTagData? = null
    var hasPropertyGuid = false
    var propertyGuid: FGuid? = null
    var tag: FPropertyTagType? = null

    constructor(Ar: FAssetArchive, readData: Boolean) {
        super.init(Ar)
        name = Ar.readFName()
        if (name.text != "None") {
            propertyType = Ar.readFName()
            size = Ar.readInt32()
            arrayIndex = Ar.readInt32()
            tagData = when (propertyType.text) {
                "StructProperty" -> FPropertyTagData.StructProperty(Ar)
                "BoolProperty" -> FPropertyTagData.BoolProperty(Ar)
                "EnumProperty" -> FPropertyTagData.EnumProperty(Ar)
                "ByteProperty" -> FPropertyTagData.ByteProperty(Ar)
                "ArrayProperty" -> FPropertyTagData.ArrayProperty(Ar)
                "MapProperty" -> FPropertyTagData.MapProperty(Ar)
                "SetProperty" -> FPropertyTagData.BoolProperty(Ar)
                else -> null
            }

            // MapProperty doesn't seem to store the inner types as their types when they're UStructs.
            hasPropertyGuid = Ar.readFlag()
            if (hasPropertyGuid)
                propertyGuid = FGuid(Ar)

            if (readData) {
                val pos = Ar.pos()
                val finalPos = pos + size
                try {
                    tag =
                        FPropertyTagType.readFPropertyTagType(
                            Ar,
                            propertyType.text,
                            tagData,
                            FPropertyTagType.Type.NORMAL
                        )
                    if (finalPos != Ar.pos()) {
                        logger.warn("FPropertyTagType $name (${tagData ?: propertyType}) was not read properly, pos ${Ar.pos()}, calculated pos $finalPos")
                    }
                    //Even if the property wasn't read properly
                    //we don't need to crash here because we know the expected size
                    Ar.seek(finalPos)
                } catch (e: ParserException) {
                    if (finalPos != Ar.pos()) {
                        logger.warn("Failed to read FPropertyTagType $name (${tagData ?: propertyType}), skipping it, please report", e)
                    }
                    //Also no need to crash here, just seek to the desired offset
                    Ar.seek(finalPos)
                }
            }
        }
        super.complete(Ar)
    }

    fun getTagTypeValue(clazz: Class<*>, Ar: FAssetArchive? = null) : Any? {
        if (tag == null)
            throw IllegalArgumentException("This tag was read without data")
        return tag?.getTagTypeValue(clazz, Ar)
    }

    inline fun <reified T> getTagTypeValue(Ar: FAssetArchive? = null) : T? {
        if (tag == null)
            throw IllegalArgumentException("This tag was read without data")
        return tag?.getTagTypeValue<T>(Ar)
    }

    @Deprecated(message = "Should not be used anymore, since its not able to process arrays and struct fallback", replaceWith = ReplaceWith("getTagTypeValue<T>"))
    fun getTagTypeValueLegacy() = tag?.getTagTypeValueLegacy() ?: throw IllegalArgumentException("This tag was read without data")

    fun setTagTypeValue(value : Any?) = tag?.setTagTypeValue(value)

    fun serialize(Ar: FAssetArchiveWriter, writeData : Boolean) {
        super.initWrite(Ar)
        Ar.writeFName(name)
        if (name.text != "None") {
            Ar.writeFName(propertyType)
            var tagTypeData : ByteArray? = null
            if (writeData) {
                //Recalculate the size of the tag and also save the serialized data
                val tempAr = Ar.setupByteArrayWriter()
                try {
                    FPropertyTagType.writeFPropertyTagType(
                        tempAr,
                        tag ?: throw ParserException("FPropertyTagType is needed when trying to write it"),
                        FPropertyTagType.Type.NORMAL
                    )
                    Ar.writeInt32(tempAr.pos() - Ar.pos())
                    tagTypeData = tempAr.toByteArray()
                } catch (e : ParserException) {
                    throw ParserException("Error occurred while writing the FPropertyTagType $name ($propertyType) ", Ar, e)
                }
            } else {
                Ar.writeInt32(size)
            }
            Ar.writeInt32(arrayIndex)
            tagData?.serialize(Ar)

            Ar.writeFlag(hasPropertyGuid)
            if (hasPropertyGuid)
                propertyGuid?.serialize(Ar)

            if (writeData) {
                if (tagTypeData != null) {
                    Ar.write(tagTypeData)
                }
            }
        }
        super.completeWrite(Ar)
    }

    override fun toString() = "${name.text}   -->   ${if (tag != null) getTagTypeValueLegacy() else "Failed to parse"}"
}