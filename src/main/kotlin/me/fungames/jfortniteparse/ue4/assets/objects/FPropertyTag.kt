package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.PropertyInfo
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_ARRAY_PROPERTY_INNER_TAGS
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_PROPERTY_GUID_IN_PROPERTY_TAG
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_PROPERTY_TAG_SET_MAP_SUPPORT
import me.fungames.jfortniteparse.ue4.versions.VER_UE4_STRUCT_GUID_IN_PROPERTY_TAG
import me.fungames.jfortniteparse.util.INDEX_NONE

/**
 * A tag describing a class property, to aid in serialization.
 */
class FPropertyTag : UClass {
    // Transient.
    var prop: FPropertyTagType? = null // prop: FProperty

    // Variables.
    /** Type of property */
    lateinit var type: FName
    /** A boolean property's value (never need to serialize data for bool properties except here) */
    var boolVal: Boolean = false
    /** Name of property. */
    var name: FName
    /** Struct name if FStructProperty. */
    var structName = FName.NAME_None
    /** Enum name if FByteProperty or FEnumProperty */
    var enumName = FName.NAME_None
    var enumType: String? = null // custom
    /** Inner type if FArrayProperty, FSetProperty, or FMapProperty */
    var innerType = FName.NAME_None
    /** Value type if UMapProperty */
    var valueType = FName.NAME_None
    /** Property size. */
    var size: Int = 0
    /** Index if an array; else 0. */
    var arrayIndex = INDEX_NONE
    /** Location in stream of tag size member */
    var sizeOffset = -1L
    var structGuid: FGuid? = null
    var hasPropertyGuid: Boolean = false
    var propertyGuid: FGuid? = null

    var structClass: Class<*>? = null
    var enumClass: Class<out Enum<*>>? = null

    constructor(name: FName) {
        this.name = name
    }

    constructor(info: PropertyInfo) {
        name = FName.dummy(info.name!!)
        type = FName.dummy(info.type!!)
        info.structType?.let { structName = FName.dummy(it) }
        boolVal = info.bool ?: false
        info.enumName?.let { enumName = FName.dummy(it) }
        enumType = info.enumType
        info.innerType?.let { innerType = FName.dummy(it) }
        info.valueType?.let { valueType = FName.dummy(it) }
        structClass = info.structClass
        enumClass = info.enumClass
    }

    constructor(Ar: FAssetArchive, readData: Boolean) {
        super.init(Ar)
        name = Ar.readFName()
        if (!name.isNone()) {
            type = Ar.readFName()
            size = Ar.readInt32()
            arrayIndex = Ar.readInt32()
            val tagType = type.text

            if (tagType == "StructProperty") { // only need to serialize this for structs
                structName = Ar.readFName()
                if (Ar.ver >= VER_UE4_STRUCT_GUID_IN_PROPERTY_TAG)
                    structGuid = FGuid(Ar)
            } else if (tagType == "BoolProperty") { // only need to serialize this for bools
                boolVal = Ar.readFlag()
            } else if (tagType == "ByteProperty") { // only need to serialize this for bytes/enums
                enumName = Ar.readFName()
            } else if (tagType == "EnumProperty") {
                enumName = Ar.readFName()
            } else if (tagType == "ArrayProperty") { // only need to serialize this for arrays
                if (Ar.ver >= VER_UE4_ARRAY_PROPERTY_INNER_TAGS)
                    innerType = Ar.readFName()
            } else if (Ar.ver >= VER_UE4_PROPERTY_TAG_SET_MAP_SUPPORT) {
                if (tagType == "SetProperty") {
                    innerType = Ar.readFName()
                } else if (tagType == "MapProperty") {
                    innerType = Ar.readFName() // MapProperty doesn't seem to store the inner types as their types when they're UStructs.
                    valueType = Ar.readFName()
                }
            }

            // Property tags to handle renamed blueprint properties effectively.
            if (Ar.ver >= VER_UE4_PROPERTY_GUID_IN_PROPERTY_TAG) {
                hasPropertyGuid = Ar.readFlag()
                if (hasPropertyGuid)
                    propertyGuid = FGuid(Ar)
            }

            if (readData) {
                val pos = Ar.pos()
                val finalPos = pos + size
                try {
                    prop =
                        FPropertyTagType.readFPropertyTagType(
                            Ar, type.text, this,
                            FPropertyTagType.ReadType.NORMAL
                        )
                    if (finalPos != Ar.pos()) {
                        logger.warn("FPropertyTagType $name (${type}) was not read properly, pos ${Ar.pos()}, calculated pos $finalPos")
                    }
                    //Even if the property wasn't read properly
                    //we don't need to crash here because we know the expected size
                    Ar.seek(finalPos)
                } catch (e: ParserException) {
                    if (finalPos != Ar.pos()) {
                        logger.warn("Failed to read FPropertyTagType $name (${type}), skipping it, please report", e)
                    }
                    //Also no need to crash here, just seek to the desired offset
                    Ar.seek(finalPos)
                }
            }
        }
        super.complete(Ar)
    }

    fun <T> getTagTypeValue(clazz: Class<T>): T? {
        if (prop == null)
            throw IllegalArgumentException("This tag was read without data")
        return prop?.getTagTypeValue(clazz)
    }

    inline fun <reified T> getTagTypeValue(): T? {
        if (prop == null)
            throw IllegalArgumentException("This tag was read without data")
        return prop?.getTagTypeValue<T>()
    }

    //@Deprecated(message = "Should not be used anymore, since its not able to process arrays and struct fallback", replaceWith = ReplaceWith("getTagTypeValue<T>"))
    fun getTagTypeValueLegacy() = prop?.getTagTypeValueLegacy() ?: throw IllegalArgumentException("This tag was read without data")

    fun setTagTypeValue(value: Any?) = prop?.setTagTypeValue(value)

    fun serialize(Ar: FAssetArchiveWriter, writeData: Boolean) {
        super.initWrite(Ar)
        Ar.writeFName(name)
        if (name.text != "None") {
            Ar.writeFName(type)
            var tagTypeData: ByteArray? = null
            if (writeData) {
                //Recalculate the size of the tag and also save the serialized data
                val tempAr = Ar.setupByteArrayWriter()
                try {
                    FPropertyTagType.writeFPropertyTagType(
                        tempAr,
                        prop ?: throw ParserException("FPropertyTagType is needed when trying to write it"),
                        FPropertyTagType.ReadType.NORMAL
                    )
                    Ar.writeInt32(tempAr.pos() - Ar.pos())
                    tagTypeData = tempAr.toByteArray()
                } catch (e: ParserException) {
                    throw ParserException("Error occurred while writing the FPropertyTagType $name ($type) ", Ar, e)
                }
            } else {
                Ar.writeInt32(size)
            }
            Ar.writeInt32(arrayIndex)
            // TODO tagData?.serialize(Ar)

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

    override fun toString() = "${name.text}   -->   ${if (prop != null) getTagTypeValueLegacy() else "Failed to parse"}"
}