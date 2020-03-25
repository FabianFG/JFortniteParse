@file:Suppress("UNUSED")
package me.fungames.jfortniteparse.ue4.assets

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UEClass
import me.fungames.jfortniteparse.ue4.assets.exports.UEExport
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.util.PayloadType
import me.fungames.jfortniteparse.ue4.locres.Locres

@ExperimentalUnsignedTypes
class UObject : UEExport {
    override var baseObject = this
    var properties : MutableList<FPropertyTag>
    var serializeGuid : Boolean
    var objectGuid : FGuid? = null

    constructor(Ar: FAssetArchive, exportObject : FObjectExport) : super(exportObject) {
        properties = deserializeProperties(Ar)
        serializeGuid = Ar.readBoolean()
        if (serializeGuid)
            objectGuid = FGuid(Ar)
    }

    inline fun <reified T> set(name: String, value : T) {
        if(getOrNull<T>(name) != null)
            properties.first { it.name.text == name }.setTagTypeValue(value)
    }

    inline fun <reified T> getOrDefault(name : String, default : T) : T {
        val value : T? = getOrNull(name)
        return value ?: default
    }

    inline fun <reified T> getOrNull(name : String) : T? {
        val value = properties.firstOrNull { it.name.text == name }?.getTagTypeValue()
        return if (value is T)
            value
        else
            null
    }

    inline fun <reified T> get(name: String) : T = getOrNull(name) ?: throw KotlinNullPointerException("$name must be not-null")

    override fun serialize(Ar: FAssetArchiveWriter) {
        serializeProperties(Ar, properties)
        Ar.writeBoolean(serializeGuid)
        if (serializeGuid)
            objectGuid?.serialize(Ar)
    }

    companion object {
        fun serializeProperties(Ar: FAssetArchiveWriter, properties: List<FPropertyTag>) {
            properties.forEach {
                it.serialize(Ar, true)
            }
            Ar.writeFName(FName.getByNameMap("None", Ar.nameMap) ?: throw ParserException("NameMap must contain \"None\""))
        }

        fun deserializeProperties(Ar : FAssetArchive) : MutableList<FPropertyTag> {
            val properties = mutableListOf<FPropertyTag>()
            while (true) {
                val tag = FPropertyTag(Ar, true)
                if (tag.name.text == "None")
                    break
                properties.add(tag)
            }
            return properties
        }
    }

    constructor(properties : MutableList<FPropertyTag>, serializeGuid : Boolean, objectGuid : FGuid?, exportType: String) : super(exportType) {
        this.properties = properties
        this.serializeGuid = serializeGuid
        this.objectGuid = objectGuid
    }
}

@ExperimentalUnsignedTypes
class FPropertyTag : UEClass {
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
                try {
                    tag = FPropertyTagType.readFPropertyTagType(Ar, propertyType.text, tagData)
                    val finalPos = pos + size
                    if (finalPos != Ar.pos()) {
                        logger.debug("FPropertyTagType $name ($propertyType) was not read properly, pos ${Ar.pos()}, calculated pos $finalPos")
                    }
                    //Even if the property wasn't read properly
                    //we don't need to crash here because we know the expected size
                    Ar.seek(finalPos)
                } catch (e: ParserException) {
                    throw ParserException("Error occurred while reading the FPropertyTagType $name ($propertyType) ", Ar, e)
                }
            }
        }
        super.complete(Ar)
    }

    fun getTagTypeValue() = tag?.getTagTypeValue() ?: throw IllegalArgumentException("This tag was read without data")

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
                    FPropertyTagType.writeFPropertyTagType(tempAr, tag?: throw ParserException("FPropertyTagType is needed when trying to write it"))
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
}

@ExperimentalUnsignedTypes
sealed class FPropertyTagData : UEClass() {

    abstract fun serialize(Ar: FAssetArchiveWriter)

    class StructProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var nameData: FName
        var guid: FGuid

        init {
            super.init(Ar)
            nameData = Ar.readFName()
            guid = FGuid(Ar)
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(nameData)
            guid.serialize(Ar)
            super.completeWrite(Ar)
        }
    }

    class BoolProperty(Ar: FArchive) : FPropertyTagData() {
        var bool: Boolean

        init {
            super.init(Ar)
            bool = Ar.readFlag()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFlag(bool)
            super.completeWrite(Ar)
        }
    }

    class EnumProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var enum: FName

        init {
            super.init(Ar)
            enum = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(enum)
            super.completeWrite(Ar)
        }
    }

    class ByteProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var byte: FName

        init {
            super.init(Ar)
            byte = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(byte)
            super.completeWrite(Ar)
        }
    }

    class ArrayProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var property: FName

        init {
            super.init(Ar)
            property = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(property)
            super.completeWrite(Ar)
        }
    }

    class MapProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var key: FName
        var value: FName

        init {
            super.init(Ar)
            key = Ar.readFName()
            value = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(key)
            Ar.writeFName(value)
            super.completeWrite(Ar)
        }
    }

    class SetProperty(Ar: FAssetArchive) : FPropertyTagData() {
        var property: FName

        init {
            super.init(Ar)
            property = Ar.readFName()
            super.complete(Ar)
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(property)
            super.completeWrite(Ar)
        }
    }
}

@ExperimentalUnsignedTypes
sealed class FPropertyTagType(val propertyType: String) {

    fun getTagTypeValue() : Any {
        return when(this) {
            is BoolProperty -> this.bool
            is StructProperty -> this.struct.structType
            is ObjectProperty -> this.`object`
            is InterfaceProperty -> this.interfaceProperty
            is FloatProperty -> this.float
            is TextProperty -> this.text
            is StrProperty -> this.str
            is NameProperty -> this.name
            is IntProperty -> this.number
            is UInt16Property -> this.number
            is UInt32Property -> this.number
            is UInt64Property -> this.number
            is ArrayProperty -> this.array
            is MapProperty -> this.map
            is ByteProperty -> this.byte
            is EnumProperty -> this.name
            is SoftObjectProperty -> this.`object`
            is SoftObjectPropertyMap -> this.guid
        }
    }

    fun setTagTypeValue(value : Any?) {
        if (value == null)
            return
        when(this) {
            is BoolProperty -> this.bool = value as Boolean
            is StructProperty -> this.struct.structType = value as UEClass
            is ObjectProperty -> this.`object` = value as FPackageIndex
            is InterfaceProperty -> this.interfaceProperty = value as UInterfaceProperty
            is FloatProperty -> this.float = value as Float
            is TextProperty -> this.text = value as FText
            is StrProperty -> this.str = value as String
            is NameProperty -> this.name = value as FName
            is IntProperty -> this.number = value as Int
            is UInt16Property -> this.number = value as UShort
            is UInt32Property -> this.number = value as UInt
            is UInt64Property -> this.number = value as ULong
            is ArrayProperty -> this.array = value as UScriptArray
            is MapProperty -> this.map = value as UScriptMap
            is ByteProperty -> this.byte = value as UByte
            is EnumProperty -> this.name = value as FName
            is SoftObjectProperty -> this.`object` = value as FSoftObjectPath
            is SoftObjectPropertyMap -> this.guid = value as FGuid
        }
    }


    companion object {
        fun readFPropertyTagType(
            Ar: FAssetArchive,
            propertyType: String,
            tagData: FPropertyTagData?
        ): FPropertyTagType? {
            when (propertyType) {
                "BoolProperty" -> {
                    if (tagData != null)
                        when (tagData) {
                            is FPropertyTagData.BoolProperty -> {
                                return BoolProperty(tagData.bool, propertyType)
                            }
                            else -> throw ParserException("Given bool property does not have bool data", Ar)
                        }
                    else throw ParserException("Bool Property needs tag data", Ar)
                }
                "StructProperty" -> {
                    if (tagData != null)
                        when (tagData) {
                            is FPropertyTagData.StructProperty -> {
                                return StructProperty(UScriptStruct(Ar, tagData.nameData.text), propertyType)
                            }
                            else -> throw ParserException("Given struct property does not have struct data")
                        }
                    else throw ParserException("Struct Property needs tag data", Ar)
                }
                "ObjectProperty" -> return ObjectProperty(FPackageIndex(Ar), propertyType)
                "InterfaceProperty" -> return InterfaceProperty(UInterfaceProperty(Ar), propertyType)
                "FloatProperty" -> return FloatProperty(Ar.readFloat32(), propertyType)
                "TextProperty" -> return TextProperty(FText(Ar), propertyType)
                "StrProperty" -> return StrProperty(Ar.readString(), propertyType)
                "NameProperty" -> return NameProperty(Ar.readFName(), propertyType)
                "IntProperty" -> return IntProperty(Ar.readInt32(), propertyType)
                "UInt16Property" -> return UInt16Property(Ar.readUInt16(), propertyType)
                "UInt32Property" -> return UInt32Property(Ar.readUInt32(), propertyType)
                "UInt64Property" -> return UInt64Property(Ar.readUInt64(), propertyType)
                "ArrayProperty" -> {
                    if (tagData != null)
                        when (tagData) {
                            is FPropertyTagData.ArrayProperty -> return ArrayProperty(
                                UScriptArray(
                                    Ar,
                                    tagData.property.text
                                ), propertyType
                            )
                            else -> throw ParserException("Cannot read array from give non-array", Ar)
                        }
                    else throw ParserException("Array Property needs tag data", Ar)
                }
                "MapProperty" -> {
                    if (tagData != null)
                        when (tagData) {
                            is FPropertyTagData.MapProperty -> {
                                return MapProperty(UScriptMap(Ar, tagData.key.text, tagData.value.text), propertyType)
                            }
                            else -> throw ParserException("Given map data does not have map data")
                        }
                    else throw ParserException("Map Property needs tag data", Ar)
                }
                "ByteProperty" -> {
                    if (tagData != null)
                        when (tagData) {
                            is FPropertyTagData.ByteProperty -> {
                                return if (tagData.byte.text == "None")
                                    ByteProperty(Ar.readUInt8(), propertyType)
                                else
                                    NameProperty(Ar.readFName(), propertyType)
                            }
                            else -> throw ParserException("Given byte property does not have byte data", Ar)
                        }
                    else throw ParserException("Byte Property needs tag data", Ar)
                }
                "EnumProperty" -> {
                    if (tagData != null)
                        when (tagData) {
                            is FPropertyTagData.EnumProperty -> {
                                return if (tagData.enum.text == "None")
                                    EnumProperty(FName.dummy("None"), propertyType)
                                else
                                    EnumProperty(Ar.readFName(), propertyType)
                            }
                            else -> throw ParserException("Given enum property does not have enum data")
                        }
                    else throw ParserException("Enum Property needs tag data", Ar)
                }
                "SoftObjectProperty" -> {
                    return SoftObjectProperty(FSoftObjectPath(Ar), propertyType)
                }
                else -> {
                    UEClass.logger.warn("Couldn't read property type $propertyType at ${Ar.pos()}")
                    return null
                }
            }
        }
        fun writeFPropertyTagType(Ar: FAssetArchiveWriter, tag: FPropertyTagType) {
            when(tag) {
                is StructProperty -> tag.struct.serialize(Ar)
                is ObjectProperty -> tag.`object`.serialize(Ar)
                is InterfaceProperty -> tag.interfaceProperty.serialize(Ar)
                is FloatProperty -> Ar.writeFloat32(tag.float)
                is TextProperty -> tag.text.serialize(Ar)
                is StrProperty -> Ar.writeString(tag.str)
                is NameProperty -> Ar.writeFName(tag.name)
                is IntProperty -> Ar.writeInt32(tag.number)
                is UInt16Property -> Ar.writeUInt16(tag.number)
                is UInt32Property -> Ar.writeUInt32(tag.number)
                is UInt64Property -> Ar.writeUInt64(tag.number)
                is ArrayProperty -> tag.array.serialize(Ar)
                is MapProperty -> tag.map.serialize(Ar)
                is ByteProperty -> Ar.writeUInt8(tag.byte)
                is EnumProperty -> {
                    if (tag.name !is FName.FNameDummy)
                        Ar.writeFName(tag.name)
                }
                is SoftObjectProperty -> tag.`object`.serialize(Ar)
            }
        }
    }


    @ExperimentalUnsignedTypes
    class BoolProperty(var bool: Boolean, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class StructProperty(var struct: UScriptStruct, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class ObjectProperty(var `object`: FPackageIndex, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class InterfaceProperty(var interfaceProperty: UInterfaceProperty, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class FloatProperty(var float: Float, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class TextProperty(var text: FText, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class StrProperty(var str: String, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class NameProperty(var name: FName, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class IntProperty(var number: Int, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class UInt16Property(var number: UShort, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class UInt32Property(var number: UInt, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class UInt64Property(var number: ULong, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class ArrayProperty(var array: UScriptArray, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class MapProperty(var map: UScriptMap, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class ByteProperty(var byte: UByte, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class EnumProperty(var name: FName, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class SoftObjectProperty(var `object`: FSoftObjectPath, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class SoftObjectPropertyMap(var guid: FGuid, propertyType: String) : FPropertyTagType(propertyType)


}

@ExperimentalUnsignedTypes
class UScriptArray : UEClass {
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
            if (innerType == "BoolProperty")
                contents.add(FPropertyTagType.BoolProperty(Ar.readFlag(), innerType))
            else if (innerType == "ByteProperty")
                contents.add(FPropertyTagType.ByteProperty(Ar.readUInt8(), innerType))
            else {
                val content = FPropertyTagType.readFPropertyTagType(Ar, innerType, innerTagData)
                if (content != null)
                    contents.add(content)
                else
                    logger.warn("Failed to read array content of type $innerType at ${Ar.pos()}, index $i")
            }
        }

        data = mutableListOf()
        contents.forEach { data.add(it.getTagTypeValue()) }

        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(contents.size)
        arrayTag?.serialize(Ar, false)
        contents.forEach {
            when(it) {
                is FPropertyTagType.BoolProperty -> Ar.writeFlag(it.bool)
                is FPropertyTagType.ByteProperty -> Ar.writeUInt8(it.byte)
                else -> FPropertyTagType.writeFPropertyTagType(Ar, it)
            }
        }
        super.completeWrite(Ar)
    }

    constructor(arrayTag: FPropertyTag?, contents: MutableList<FPropertyTagType>, innerType: String) {
        this.arrayTag = arrayTag
        this.contents = contents
        this.data = mutableListOf()
        contents.forEach { this.data.add(it.getTagTypeValue()) }
        this.innerType = innerType
    }
}

@ExperimentalUnsignedTypes
class UScriptMap : UEClass {
    var numKeyToRemove: Int
    val mapData: MutableMap<FPropertyTagType, FPropertyTagType>
    val keyType : String
    val valueType : String

    constructor(Ar: FAssetArchive, keyType: String, valueType: String) {
        this.keyType = keyType
        this.valueType = valueType
        super.init(Ar)
        numKeyToRemove = Ar.readInt32()
        if (numKeyToRemove != 0)
            throw ParserException(
                "Could not read MapProperty with types $keyType (key) $valueType (value), numKeyToRemove is not supported",
                Ar
            )
        val length = Ar.readInt32()
        mapData = mutableMapOf()
        for (i in 0 until length) {
            try {
                val key = readMapValue(Ar, keyType, "StructProperty")
                val value = readMapValue(Ar, valueType, "StructProperty")
                mapData[key] = value
            } catch (e: ParserException) {
                throw ParserException("Failed to read key/value pair for index $i in map", Ar, e)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar : FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(numKeyToRemove)
        Ar.writeInt32(mapData.size)
        mapData.forEach {
            writeMapValue(Ar, it.key)
            writeMapValue(Ar, it.value)
        }
        super.completeWrite(Ar)
    }

    private fun writeMapValue(Ar: FAssetArchiveWriter, tagType: FPropertyTagType) {
        when (tagType) {
            is FPropertyTagType.ByteProperty -> Ar.writeUInt32(tagType.byte.toUInt())
            is FPropertyTagType.BoolProperty -> Ar.writeFlag(tagType.bool)
            is FPropertyTagType.EnumProperty -> Ar.writeFName(tagType.name)
            is FPropertyTagType.UInt32Property -> Ar.writeUInt32(tagType.number)
            is FPropertyTagType.UInt16Property -> Ar.writeUInt16(tagType.number)
            is FPropertyTagType.UInt64Property -> Ar.writeUInt64(tagType.number)
            is FPropertyTagType.IntProperty -> Ar.writeInt32(tagType.number)
            is FPropertyTagType.StructProperty -> tagType.struct.serialize(Ar)
            is FPropertyTagType.NameProperty -> Ar.writeFName(tagType.name)
            is FPropertyTagType.ObjectProperty -> tagType.`object`.serialize(Ar)
            is FPropertyTagType.TextProperty -> tagType.text.serialize(Ar)
            is FPropertyTagType.StrProperty -> Ar.writeString(tagType.str)
            is FPropertyTagType.SoftObjectPropertyMap -> tagType.guid.serialize(Ar)
            else -> throw ParserException("Invalid map key/value of class ${tagType::class.java.simpleName}")
        }
    }

    private fun readMapValue(Ar: FAssetArchive, innerType: String, structType: String): FPropertyTagType {
        return when (innerType) {
            "ByteProperty" -> FPropertyTagType.ByteProperty(Ar.readUInt32().toUByte(), innerType)
            "BoolProperty" -> FPropertyTagType.BoolProperty(Ar.readFlag(), innerType)
            "EnumProperty" -> FPropertyTagType.EnumProperty(Ar.readFName(), innerType)
            "UInt32Property" -> FPropertyTagType.UInt32Property(Ar.readUInt32(), innerType)
            "UInt16Property" -> FPropertyTagType.UInt16Property(Ar.readUInt16(), innerType)
            "UInt64Property" -> FPropertyTagType.UInt64Property(Ar.readUInt64(), innerType)
            "IntProperty" -> FPropertyTagType.IntProperty(Ar.readInt32(), innerType)
            "StructProperty" -> FPropertyTagType.StructProperty(UScriptStruct(Ar, structType), innerType)
            "NameProperty" -> FPropertyTagType.NameProperty(Ar.readFName(), innerType)
            "ObjectProperty" -> FPropertyTagType.ObjectProperty(FPackageIndex(Ar), innerType)
            "TextProperty" -> FPropertyTagType.TextProperty(FText(Ar), innerType)
            "StrProperty" -> FPropertyTagType.StrProperty(Ar.readString(), innerType)
            "SoftObjectProperty" -> FPropertyTagType.SoftObjectPropertyMap(FGuid(Ar), innerType)
            else -> FPropertyTagType.StructProperty(UScriptStruct(Ar, innerType), innerType)
        }
    }

    constructor(numKeyToRemove : Int, mapData : MutableMap<FPropertyTagType, FPropertyTagType>, keyType: String, valueType: String) {
        this.numKeyToRemove = numKeyToRemove
        this.mapData = mapData
        this.keyType = keyType
        this.valueType = valueType
    }
}

@ExperimentalUnsignedTypes
class UScriptStruct : UEClass {
    val structName: String
    var structType: UEClass

    constructor(Ar: FAssetArchive, structName: String) {
        super.init(Ar)
        this.structName = structName
        structType = when (structName) {
            "IntPoint" -> FIntPoint(Ar)
            "Guid" -> FGuid(Ar)
            "GameplayTagContainer" -> FGameplayTagContainer(Ar)
            "Color" -> FColor(Ar)
            "LinearColor" -> FLinearColor(Ar)
            "SoftObjectPath" -> FSoftObjectPath(Ar)
            "Vector2D", "Box2D" -> FVector2D(Ar)
            "Quat" -> FQuat(Ar)
            "Vector" -> FVector(Ar)
            "Rotator" -> FRotator(Ar)
            "PerPlatformFloat" -> FPerPlatformFloat(Ar)
            "PerPlatformInt" -> FPerPlatformInt(Ar)
            "SkeletalMeshSamplingLODBuiltData" -> FWeightedRandomSampler(Ar)
            "LevelSequenceObjectReferenceMap" -> FLevelSequenceObjectReferenceMap(Ar)
            "FrameNumber" -> FFrameNumber(Ar)
            "SmartName" -> FSmartName(Ar)
            "RichCurveKey" -> FRichCurveKey(Ar)
            "SimpleCurveKey" -> FSimpleCurveKey(Ar)
            "Timespan", "DateTime" -> FDateTime(Ar)
            else -> {
                logger.debug("Unknown struct type $structName, using FStructFallback")
                FStructFallback(Ar)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        when(val structType = structType) {
            is FIntPoint -> structType.serialize(Ar)
            is FGuid -> structType.serialize(Ar)
            is FGameplayTagContainer -> structType.serialize(Ar)
            is FColor -> structType.serialize(Ar)
            is FLinearColor -> structType.serialize(Ar)
            is FSoftObjectPath -> structType.serialize(Ar)
            is FVector2D -> structType.serialize(Ar)
            is FQuat -> structType.serialize(Ar)
            is FVector -> structType.serialize(Ar)
            is FRotator -> structType.serialize(Ar)
            is FPerPlatformFloat -> structType.serialize(Ar)
            is FPerPlatformInt -> structType.serialize(Ar)
            is FWeightedRandomSampler -> structType.serialize(Ar)
            is FLevelSequenceObjectReferenceMap -> structType.serialize(Ar)
            is FStructFallback -> structType.serialize(Ar)
            is FFrameNumber -> structType.serialize(Ar)
            is FSmartName -> structType.serialize(Ar)
            is FRichCurveKey -> structType.serialize(Ar)
            is FSimpleCurveKey -> structType.serialize(Ar)
            is FDateTime -> structType.serialize(Ar)
        }
        super.completeWrite(Ar)
    }

    constructor(structName: String, structType: UEClass) {
        this.structName = structName
        this.structType = structType
    }
}

@ExperimentalUnsignedTypes
class FStructFallback : UEClass {
    var properties : MutableList<FPropertyTag>

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        properties = mutableListOf()
        while (true) {
            val tag = FPropertyTag(Ar, true)
            if (tag.name.text == "None")
                break
            properties.add(tag)
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        properties.forEach {
            it.serialize(Ar, true)
        }
        Ar.writeFName(FName.getByNameMap("None", Ar.nameMap) ?: throw ParserException("NameMap must contain \"None\""))
        super.completeWrite(Ar)
    }

    inline fun <reified T> set(name: String, value : T) {
        if(getOrNull<T>(name) != null)
            properties.first { it.name.text == name }.setTagTypeValue(value)
    }

    inline fun <reified T> getOrDefault(name : String, default : T) : T {
        val value : T? = getOrNull(name)
        return value ?: default
    }

    inline fun <reified T> getOrNull(name : String) : T? {
        val value = properties.firstOrNull { it.name.text == name }?.getTagTypeValue()
        return if (value is T)
            value
        else
            null
    }

    inline fun <reified T> get(name: String) : T = getOrNull(name) ?: throw KotlinNullPointerException("$name must be not-null")

    constructor(properties: MutableList<FPropertyTag>) {
        this.properties = properties
    }
}
/* too much shit
@ExperimentalUnsignedTypes
class FSectionEvaluationDataTree : UEClass {
    var tree : TMovieSceneEvaluationTree<FStructFallback>

    constructor(Ar : FArchive) {
        super.init(Ar)
        tree = TMovieSceneEvaluationTree(Ar)
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        tree.serialize(Ar)
        super.completeWrite(Ar)
    }

    constructor(tree: TMovieSceneEvaluationTree<FStructFallback>) {
        this.tree = tree
    }
}

@ExperimentalUnsignedTypes
class TMovieSceneEvaluationTree<T> : UEClass {
    var baseTree : FMovieSceneEvaluationTree
    var data : TEvaluationTreeEntryContainer<T>

    constructor(Ar : FArchive) {
        super.init(Ar)
        baseTree = FMovieSceneEvaluationTree(Ar)
        data = TEvaluationTreeEntryContainer(Ar)
        super.complete(Ar)
    }

    fun serialize(Ar : FArchiveWriter) {
        super.initWrite(Ar)
        baseTree.serialize(Ar)
        data.serialize(Ar)
        super.completeWrite(Ar)
    }

    constructor(baseTree: FMovieSceneEvaluationTree, data: TEvaluationTreeEntryContainer<T>) {
        this.baseTree = baseTree
        this.data = data
    }
}*/

@ExperimentalUnsignedTypes
class FDateTime : UEClass {
    var date : Long

    constructor(Ar: FArchive) {
        super.init(Ar)
        date = Ar.readInt64()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt64(date)
        super.completeWrite(Ar)
    }

    constructor(date: Long) {
        this.date = date
    }
}

@ExperimentalUnsignedTypes
class FSimpleCurveKey : UEClass {
    var time : Float
    var value : Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        time = Ar.readFloat32()
        value = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(time)
        Ar.writeFloat32(value)
        super.completeWrite(Ar)
    }

    constructor(time : Float, value: Float) {
        this.time = time
        this.value = value
    }
}

@ExperimentalUnsignedTypes
class FRichCurveKey : UEClass {
    var interpMode : Byte
    var tangentMode : Byte
    var tangentWeightMode : Byte
    var time : Float
    var arriveTangent : Float
    var arriveTangentWeight : Float
    var leaveTangent : Float
    var leaveTangentWeight : Float

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        interpMode = Ar.readInt8()
        tangentMode = Ar.readInt8()
        tangentWeightMode = Ar.readInt8()
        time = Ar.readFloat32()
        arriveTangent = Ar.readFloat32()
        arriveTangentWeight = Ar.readFloat32()
        leaveTangent = Ar.readFloat32()
        leaveTangentWeight = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt8(interpMode)
        Ar.writeInt8(tangentMode)
        Ar.writeInt8(tangentWeightMode)
        Ar.writeFloat32(time)
        Ar.writeFloat32(arriveTangent)
        Ar.writeFloat32(arriveTangentWeight)
        Ar.writeFloat32(leaveTangent)
        Ar.writeFloat32(leaveTangentWeight)
        super.completeWrite(Ar)
    }

    constructor(
        interpMode: Byte,
        tangentMode: Byte,
        tangentWeightMode: Byte,
        time: Float,
        arriveTangent: Float,
        arriveTangentWeight: Float,
        leaveTangent: Float,
        leaveTangentWeight: Float
    ) {
        this.interpMode = interpMode
        this.tangentMode = tangentMode
        this.tangentWeightMode = tangentWeightMode
        this.time = time
        this.arriveTangent = arriveTangent
        this.arriveTangentWeight = arriveTangentWeight
        this.leaveTangent = leaveTangent
        this.leaveTangentWeight = leaveTangentWeight
    }
}

@ExperimentalUnsignedTypes
class FSmartName : UEClass {
    var displayName : FName

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        displayName = Ar.readFName()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(displayName)
        super.completeWrite(Ar)
    }

    constructor(displayName: FName) {
        this.displayName = displayName
    }
}

@ExperimentalUnsignedTypes
class FFrameNumber : UEClass {
    var value : Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        value = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(value)
        super.completeWrite(Ar)
    }

    constructor(value: Float) {
        this.value = value
    }
}

@ExperimentalUnsignedTypes
class FLevelSequenceObjectReferenceMap : UEClass {
    var mapData : Array<FLevelSequenceLegacyObjectReference>

    constructor(Ar: FArchive) {
        super.init(Ar)
        mapData = Ar.readTArray { FLevelSequenceLegacyObjectReference(Ar) }
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeTArray(mapData) {it.serialize(Ar)}
        super.completeWrite(Ar)
    }

    constructor(mapData : Array<FLevelSequenceLegacyObjectReference>) {
        this.mapData = mapData
    }
}

@ExperimentalUnsignedTypes
class FLevelSequenceLegacyObjectReference : UEClass {
    var keyGuid : FGuid
    var objectId : FGuid
    var objectPath : String

    constructor(Ar: FArchive) {
        super.init(Ar)
        keyGuid = FGuid(Ar)
        objectId = FGuid(Ar)
        objectPath = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        keyGuid.serialize(Ar)
        objectId.serialize(Ar)
        Ar.writeString(objectPath)
        super.completeWrite(Ar)
    }

    constructor(keyGuid : FGuid, objectId : FGuid, objectPath : String) {
        this.keyGuid = keyGuid
        this.objectId = objectId
        this.objectPath = objectPath
    }
}

@ExperimentalUnsignedTypes
class FWeightedRandomSampler : UEClass {
    var prob : Array<Float>
    var alias : Array<Int>
    var totalWeight : Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        prob = Ar.readTArray { Ar.readFloat32() }
        alias = Ar.readTArray { Ar.readInt32() }
        totalWeight = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeTArray(prob) {Ar.writeFloat32(it)}
        Ar.writeTArray(alias) {Ar.writeInt32(it)}
        Ar.writeFloat32(totalWeight)
        super.completeWrite(Ar)
    }

    constructor(prob : Array<Float>, alias : Array<Int>, totalWeight : Float) {
        this.prob = prob
        this.alias = alias
        this.totalWeight = totalWeight
    }
}

@ExperimentalUnsignedTypes
class FPerPlatformInt : UEClass {
    var cooked : Boolean
    var value: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        cooked = Ar.readFlag()
        value = Ar.readUInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFlag(cooked)
        Ar.writeUInt32(value)
        super.completeWrite(Ar)
    }

    constructor(cooked : Boolean, value : UInt) {
        this.cooked = cooked
        this.value = value
    }
}

@ExperimentalUnsignedTypes
class FPerPlatformFloat : UEClass {
    var cooked : Boolean
    var value: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        cooked = Ar.readFlag()
        value = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFlag(cooked)
        Ar.writeFloat32(value)
        super.completeWrite(Ar)
    }

    constructor(cooked : Boolean, value : Float) {
        this.cooked = cooked
        this.value = value
    }
}

@ExperimentalUnsignedTypes
class FRotator : UEClass {
    var pitch: Float
    var yaw: Float
    var roll: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        pitch = Ar.readFloat32()
        yaw = Ar.readFloat32()
        roll = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(pitch)
        Ar.writeFloat32(yaw)
        Ar.writeFloat32(roll)
        super.completeWrite(Ar)
    }

    constructor(pitch: Float, yaw: Float, roll: Float) {
        this.pitch = pitch
        this.yaw = yaw
        this.roll = roll
    }
}

@ExperimentalUnsignedTypes
class FVector : UEClass {
    var x: Float
    var y: Float
    var z: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
        super.completeWrite(Ar)
    }

    constructor(x: Float, y: Float, z: Float) {
        this.x = x
        this.y = y
        this.z = z
    }
}

@ExperimentalUnsignedTypes
class FQuat : UEClass {
    var x: Float
    var y: Float
    var z: Float
    var w: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        z = Ar.readFloat32()
        w = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        Ar.writeFloat32(z)
        Ar.writeFloat32(w)
        super.completeWrite(Ar)
    }

    constructor(x: Float, y: Float, z: Float, w: Float) {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
    }
}

@ExperimentalUnsignedTypes
class FVector2D : UEClass {
    var x: Float
    var y: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readFloat32()
        y = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(x)
        Ar.writeFloat32(y)
        super.completeWrite(Ar)
    }

    constructor(x: Float, y: Float) {
        this.x = x
        this.y = y
    }
}

@ExperimentalUnsignedTypes
class FLinearColor : UEClass {
    var r: Float
    var g: Float
    var b: Float
    var a: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        r = Ar.readFloat32()
        g = Ar.readFloat32()
        b = Ar.readFloat32()
        a = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFloat32(r)
        Ar.writeFloat32(g)
        Ar.writeFloat32(b)
        Ar.writeFloat32(a)
        super.completeWrite(Ar)
    }

    constructor(r: Float, g: Float, b: Float, a: Float) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }
}

@ExperimentalUnsignedTypes
class FColor : UEClass {
    var r: UByte
    var g: UByte
    var b: UByte
    var a: UByte

    constructor(Ar: FArchive) {
        super.init(Ar)
        r = Ar.readUInt8()
        g = Ar.readUInt8()
        b = Ar.readUInt8()
        a = Ar.readUInt8()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt8(r)
        Ar.writeUInt8(g)
        Ar.writeUInt8(b)
        Ar.writeUInt8(a)
        super.completeWrite(Ar)
    }

    constructor(r: UByte, g: UByte, b: UByte, a: UByte) {
        this.r = r
        this.g = g
        this.b = b
        this.a = a
    }
}

@ExperimentalUnsignedTypes
class FGameplayTagContainer : UEClass {
    var gameplayTags: MutableList<FName>

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        val length = Ar.readUInt32()
        gameplayTags = mutableListOf()
        for (i in 0u until length) {
            gameplayTags.add(Ar.readFName())
        }
        super.complete(Ar)
    }

    fun getValue(category : String) = gameplayTags.firstOrNull { it.text.startsWith(category) }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(gameplayTags.size.toUInt())
        gameplayTags.forEach {
            Ar.writeFName(it)
        }
        super.completeWrite(Ar)
    }

    constructor(gameplayTags: MutableList<FName>) {
        this.gameplayTags = gameplayTags
    }
}

@ExperimentalUnsignedTypes
class FIntPoint : UEClass {
    var x: UInt
    var y: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        x = Ar.readUInt32()
        y = Ar.readUInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(x)
        Ar.writeUInt32(y)
        super.completeWrite(Ar)
    }

    constructor(x: UInt, y: UInt) {
        this.x = x
        this.y = y
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
class FSoftObjectPath : UEClass {
    var assetPathName: FName
    var subPathString: String

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        assetPathName = Ar.readFName()
        subPathString = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(assetPathName)
        Ar.writeString(subPathString)
        super.completeWrite(Ar)
    }

    constructor(assetPathName: FName, subPathString: String) {
        this.assetPathName = assetPathName
        this.subPathString = subPathString
    }
}

@Suppress("EXPERIMENTAL_API_USAGE")
class FText : UEClass {
    var flags: UInt
    var historyType: Byte
    var nameSpace: String
    var key: String
    var sourceString: String
    var text: String

    constructor(Ar: FArchive) {
        super.init(Ar)
        flags = Ar.readUInt32()
        historyType = Ar.readInt8()
        when (historyType.toInt()) {
            -1 -> {
                nameSpace = ""
                key = ""
                sourceString = ""
            }
            0 -> {
                nameSpace = Ar.readString()
                key = Ar.readString()
                sourceString = Ar.readString()
            }
            else -> throw ParserException("Couldn't read history type $historyType")
        }
        text = sourceString
        super.complete(Ar)
    }

    fun copy() = FText(nameSpace, key, sourceString, flags, historyType)

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(flags)
        Ar.writeInt8(historyType)
        when (historyType.toInt()) {
            -1 -> { }
            0 -> {
                Ar.writeString(nameSpace)
                Ar.writeString(key)
                Ar.writeString(sourceString)
            }
            else -> throw ParserException("Invalid history type $historyType")
        }
        super.completeWrite(Ar)
    }

    @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
    constructor(nameSpace: String, key: String, sourceString: String, flags: UInt = 0u, historyType: Byte = 0) {
        this.nameSpace = nameSpace
        this.key = key
        this.sourceString = sourceString
        this.flags = flags
        this.historyType = historyType
        this.text = sourceString
    }

    fun applyLocres(locres: Locres?) {
        if (locres != null)
            text = locres.texts.stringData[nameSpace]?.get(key) ?: return
    }

    fun textForLocres(locres: Locres?) = locres?.texts?.stringData?.get(nameSpace)?.get(key) ?: text
}

@ExperimentalUnsignedTypes
class UInterfaceProperty : UEClass {
    var interfaceNumber: UInt

    constructor(Ar: FArchive) {
        super.init(Ar)
        interfaceNumber = Ar.readUInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(interfaceNumber)
        super.completeWrite(Ar)
    }

    constructor(interfaceNumber: UInt) {
        this.interfaceNumber = interfaceNumber
    }
}

@ExperimentalUnsignedTypes
class FNameEntry : UEClass {

    var name: String
    var nonCasePreservingHash: UShort
    var casePreservingHash: UShort

    constructor(Ar: FArchive) {
        super.init(Ar)
        name = Ar.readString()
        nonCasePreservingHash = Ar.readUInt16()
        casePreservingHash = Ar.readUInt16()
        super.complete(Ar)
    }

    constructor(name: String, nonCasePreservingHash: UShort, casePreservingHash: UShort) {
        this.name = name
        this.nonCasePreservingHash = nonCasePreservingHash
        this.casePreservingHash = casePreservingHash
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeString(name)
        Ar.writeUInt16(nonCasePreservingHash)
        Ar.writeUInt16(casePreservingHash)
        super.completeWrite(Ar)
    }
}

@ExperimentalUnsignedTypes
class FObjectImport : UEClass {
    var classPackage: FName
    var className: FName
    var outerIndex: FPackageIndex
    var objectName: FName

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        classPackage = Ar.readFName()
        className = Ar.readFName()
        outerIndex = FPackageIndex(Ar)
        objectName = Ar.readFName()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeFName(classPackage)
        Ar.writeFName(className)
        outerIndex.serialize(Ar)
        Ar.writeFName(objectName)
        super.completeWrite(Ar)
    }

    constructor(classPackage: FName, className: FName, outerIndex: FPackageIndex, objectName: FName) {
        this.classPackage = classPackage
        this.className = className
        this.outerIndex = outerIndex
        this.objectName = objectName
    }

    override fun toString() = objectName.text
}

@ExperimentalUnsignedTypes
class FObjectExport : UEClass {
    var classIndex: FPackageIndex
    var superIndex: FPackageIndex
    var templateIndex: FPackageIndex
    var outerIndex: FPackageIndex
    var objectName: FName
    var save: UInt
    var serialSize: Long
    var serialOffset: Long
    var forcedExport: Boolean
    var notForClient: Boolean
    var notForServer: Boolean
    var packageGuid: FGuid
    var packageFlags: UInt
    var notAlwaysLoadedForEditorGame: Boolean
    var isAsset: Boolean
    var firstExportDependency: Int
    var serializationBeforeSerializationDependencies: Boolean
    var createBeforeSerializationDependencies: Boolean
    var serializationBeforeCreateDependencies: Boolean
    var createBeforeCreateDependencies: Boolean

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        classIndex = FPackageIndex(Ar)
        superIndex = FPackageIndex(Ar)
        templateIndex = FPackageIndex(Ar)
        outerIndex = FPackageIndex(Ar)
        objectName = Ar.readFName()
        save = Ar.readUInt32()
        serialSize = Ar.readInt64()
        serialOffset = Ar.readInt64()
        forcedExport = Ar.readBoolean()
        notForClient = Ar.readBoolean()
        notForServer = Ar.readBoolean()
        packageGuid = FGuid(Ar)
        packageFlags = Ar.readUInt32()
        notAlwaysLoadedForEditorGame = Ar.readBoolean()
        isAsset = Ar.readBoolean()
        firstExportDependency = Ar.readInt32()
        serializationBeforeSerializationDependencies = Ar.readBoolean()
        createBeforeSerializationDependencies = Ar.readBoolean()
        serializationBeforeCreateDependencies = Ar.readBoolean()
        createBeforeCreateDependencies = Ar.readBoolean()
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        classIndex.serialize(Ar)
        superIndex.serialize(Ar)
        templateIndex.serialize(Ar)
        outerIndex.serialize(Ar)
        Ar.writeFName(objectName)
        Ar.writeUInt32(save)
        Ar.writeInt64(serialSize)
        Ar.writeInt64(serialOffset)
        Ar.writeBoolean(forcedExport)
        Ar.writeBoolean(notForClient)
        Ar.writeBoolean(notForServer)
        packageGuid.serialize(Ar)
        Ar.writeUInt32(packageFlags)
        Ar.writeBoolean(notAlwaysLoadedForEditorGame)
        Ar.writeBoolean(isAsset)
        Ar.writeInt32(firstExportDependency)
        Ar.writeBoolean(serializationBeforeSerializationDependencies)
        Ar.writeBoolean(createBeforeSerializationDependencies)
        Ar.writeBoolean(serializationBeforeCreateDependencies)
        Ar.writeBoolean(createBeforeCreateDependencies)
        super.completeWrite(Ar)
    }

    constructor(
        classIndex: FPackageIndex,
        superIndex: FPackageIndex,
        templateIndex: FPackageIndex,
        outerIndex: FPackageIndex,
        objectName: FName,
        save: UInt,
        serialSize: Long,
        serialOffset: Long,
        forcedExport: Boolean,
        notForClient: Boolean,
        notForServer: Boolean,
        packageGuid: FGuid,
        packageFlags: UInt,
        notAlwaysLoadedForEditorGame: Boolean,
        isAsset: Boolean,
        firstExportDependency: Int,
        serializationBeforeSerializationDependencies: Boolean,
        createBeforeSerializationDependencies: Boolean,
        serializationBeforeCreateDependencies: Boolean,
        createBeforeCreateDependencies: Boolean
    ) {
        this.classIndex = classIndex
        this.superIndex = superIndex
        this.templateIndex = templateIndex
        this.outerIndex = outerIndex
        this.objectName = objectName
        this.save = save
        this.serialSize = serialSize
        this.serialOffset = serialOffset
        this.forcedExport = forcedExport
        this.notForClient = notForClient
        this.notForServer = notForServer
        this.packageGuid = packageGuid
        this.packageFlags = packageFlags
        this.notAlwaysLoadedForEditorGame = notAlwaysLoadedForEditorGame
        this.isAsset = isAsset
        this.firstExportDependency = firstExportDependency
        this.serializationBeforeSerializationDependencies = serializationBeforeSerializationDependencies
        this.createBeforeSerializationDependencies = createBeforeSerializationDependencies
        this.serializationBeforeCreateDependencies = serializationBeforeCreateDependencies
        this.createBeforeCreateDependencies = createBeforeCreateDependencies
    }
}

@ExperimentalUnsignedTypes
class FPackageFileSummary : UEClass {
    var tag: UInt
    var legacyFileVersion: Int
    var legacyUE3Version: Int
    var fileVersionUE4: Int
    var fileVersionLicenseUE4: Int
    var customVersionContainer: Array<FCustomVersion>
    var totalHeaderSize: Int
    var folderName: String
    var packageFlags: UInt
    var nameCount: Int
    var nameOffset: Int
    var gatherableTextDataCount: Int
    var gatherableTextDataOffset: Int
    var exportCount: Int
    var exportOffset: Int
    var importCount: Int
    var importOffset: Int
    var dependsOffset: Int
    var stringAssetReferencesCount: Int
    var stringAssetReferencesOffset: Int
    var searchableNamesOffset: Int
    var thumbnailTableOffset: Int
    var guid: FGuid
    var generations: Array<FGenerationInfo>
    var savedByEngineVersion: FEngineVersion
    var compatibleWithEngineVersion: FEngineVersion
    var compressionFlags: UInt
    var compressedChunks: Array<FCompressedChunk>
    var packageSource: UInt
    var additionalPackagesToCook: Array<String>
    var assetRegistryDataOffset: Int
    var bulkDataStartOffset: Int
    var worldTileInfoDataOffset: Int
    var chunkIds: Array<Int>
    var preloadDependencyCount: Int
    var preloadDependencyOffset: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        tag = Ar.readUInt32()
        legacyFileVersion = Ar.readInt32()
        legacyUE3Version = Ar.readInt32()
        fileVersionUE4 = Ar.readInt32()
        fileVersionLicenseUE4 = Ar.readInt32()
        customVersionContainer = Ar.readTArray { FCustomVersion(it) }
        totalHeaderSize = Ar.readInt32()
        folderName = Ar.readString()
        packageFlags = Ar.readUInt32()
        nameCount = Ar.readInt32()
        nameOffset = Ar.readInt32()
        gatherableTextDataCount = Ar.readInt32()
        gatherableTextDataOffset = Ar.readInt32()
        exportCount = Ar.readInt32()
        exportOffset = Ar.readInt32()
        importCount = Ar.readInt32()
        importOffset = Ar.readInt32()
        dependsOffset = Ar.readInt32()
        stringAssetReferencesCount = Ar.readInt32()
        stringAssetReferencesOffset = Ar.readInt32()
        searchableNamesOffset = Ar.readInt32()
        thumbnailTableOffset = Ar.readInt32()
        guid = FGuid(Ar)
        generations = Ar.readTArray { FGenerationInfo(it) }
        savedByEngineVersion = FEngineVersion(Ar)
        compatibleWithEngineVersion = FEngineVersion(Ar)
        compressionFlags = Ar.readUInt32()
        compressedChunks = Ar.readTArray { FCompressedChunk(it) }
        packageSource = Ar.readUInt32()
        additionalPackagesToCook = Ar.readTArray { it.readString() }
        assetRegistryDataOffset = Ar.readInt32()
        bulkDataStartOffset = Ar.readInt32()
        worldTileInfoDataOffset = Ar.readInt32()
        chunkIds = Ar.readTArray { it.readInt32() }
        preloadDependencyCount = Ar.readInt32()
        preloadDependencyOffset = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(tag)
        Ar.writeInt32(legacyFileVersion)
        Ar.writeInt32(legacyUE3Version)
        Ar.writeInt32(fileVersionUE4)
        Ar.writeInt32(fileVersionLicenseUE4)
        Ar.writeTArray(customVersionContainer) { it.serialize(Ar) }
        Ar.writeInt32(totalHeaderSize)
        Ar.writeString(folderName)
        Ar.writeUInt32(packageFlags)
        Ar.writeInt32(nameCount)
        Ar.writeInt32(nameOffset)
        Ar.writeInt32(gatherableTextDataCount)
        Ar.writeInt32(gatherableTextDataOffset)
        Ar.writeInt32(exportCount)
        Ar.writeInt32(exportOffset)
        Ar.writeInt32(importCount)
        Ar.writeInt32(importOffset)
        Ar.writeInt32(dependsOffset)
        Ar.writeInt32(stringAssetReferencesCount)
        Ar.writeInt32(stringAssetReferencesOffset)
        Ar.writeInt32(searchableNamesOffset)
        Ar.writeInt32(thumbnailTableOffset)
        guid.serialize(Ar)
        Ar.writeTArray(generations) { it.serialize(Ar) }
        savedByEngineVersion.serialize(Ar)
        compatibleWithEngineVersion.serialize(Ar)
        Ar.writeUInt32(compressionFlags)
        Ar.writeTArray(compressedChunks) { it.serialize(Ar) }
        Ar.writeUInt32(packageSource)
        Ar.writeTArray(additionalPackagesToCook) { Ar.writeString(it) }
        Ar.writeInt32(assetRegistryDataOffset)
        Ar.writeInt32(bulkDataStartOffset)
        Ar.writeInt32(worldTileInfoDataOffset)
        Ar.writeTArray(chunkIds) { Ar.writeInt32(it) }
        Ar.writeInt32(preloadDependencyCount)
        Ar.writeInt32(preloadDependencyOffset)
        super.completeWrite(Ar)
    }

    constructor(
        tag: UInt,
        legacyFileVersion: Int,
        legacyUE3Version: Int,
        fileVersionUE4: Int,
        fileVersionLicenseUE4: Int,
        customVersionContainer: Array<FCustomVersion>,
        totalHeaderSize: Int,
        folderName: String,
        packageFlags: UInt,
        nameCount: Int,
        nameOffset: Int,
        gatherableTextDataCount: Int,
        gatherableTextDataOffset: Int,
        exportCount: Int,
        exportOffset: Int,
        importCount: Int,
        importOffset: Int,
        dependsOffset: Int,
        stringAssetReferencesCount: Int,
        stringAssetReferencesOffset: Int,
        searchableNamesOffset: Int,
        thumbnailTableOffset: Int,
        guid: FGuid,
        generations: Array<FGenerationInfo>,
        savedByEngineVersion: FEngineVersion,
        compatibleWithEngineVersion: FEngineVersion,
        compressionFlags: UInt,
        compressedChunks: Array<FCompressedChunk>,
        packageSource: UInt,
        additionalPackagesToCook: Array<String>,
        assetRegistryDataOffset: Int,
        bulkDataStartOffset: Int,
        worldTileInfoDataOffset: Int,
        chunkIds: Array<Int>,
        preloadDependencyCount: Int,
        preloadDependencyOffset: Int
    ) {
        this.tag = tag
        this.legacyFileVersion = legacyFileVersion
        this.legacyUE3Version = legacyUE3Version
        this.fileVersionUE4 = fileVersionUE4
        this.fileVersionLicenseUE4 = fileVersionLicenseUE4
        this.customVersionContainer = customVersionContainer
        this.totalHeaderSize = totalHeaderSize
        this.folderName = folderName
        this.packageFlags = packageFlags
        this.nameCount = nameCount
        this.nameOffset = nameOffset
        this.gatherableTextDataCount = gatherableTextDataCount
        this.gatherableTextDataOffset = gatherableTextDataOffset
        this.exportCount = exportCount
        this.exportOffset = exportOffset
        this.importCount = importCount
        this.importOffset = importOffset
        this.dependsOffset = dependsOffset
        this.stringAssetReferencesCount = stringAssetReferencesCount
        this.stringAssetReferencesOffset = stringAssetReferencesOffset
        this.searchableNamesOffset = searchableNamesOffset
        this.thumbnailTableOffset = thumbnailTableOffset
        this.guid = guid
        this.generations = generations
        this.savedByEngineVersion = savedByEngineVersion
        this.compatibleWithEngineVersion = compatibleWithEngineVersion
        this.compressionFlags = compressionFlags
        this.compressedChunks = compressedChunks
        this.packageSource = packageSource
        this.additionalPackagesToCook = additionalPackagesToCook
        this.assetRegistryDataOffset = assetRegistryDataOffset
        this.bulkDataStartOffset = bulkDataStartOffset
        this.worldTileInfoDataOffset = worldTileInfoDataOffset
        this.chunkIds = chunkIds
        this.preloadDependencyCount = preloadDependencyCount
        this.preloadDependencyOffset = preloadDependencyOffset
    }
}

@ExperimentalUnsignedTypes
class FPackageIndex : UEClass {
    var index: Int
    var importMap : List<FObjectImport>
    val importObject : FObjectImport?
        get() = when {
            index < 0 -> importMap.getOrNull((index * -1) - 1)
            index > 0 -> importMap.getOrNull(index - 1)
            else -> null
        }
    val outerImportObject : FObjectImport?
        get() = this.importObject?.outerIndex?.importObject ?: this.importObject

    val importName: String
        get() = importObject?.objectName?.text ?: index.toString()

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        index = Ar.readInt32()
        super.complete(Ar)
        importMap = Ar.importMap
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(index)
        super.completeWrite(Ar)
    }

    constructor(index: Int, importMap: List<FObjectImport>) {
        this.index = index
        this.importMap = importMap
    }

    private fun getPackage(index: Int, importMap: List<FObjectImport>): FObjectImport? {
        return when {
            index < 0 -> importMap.getOrNull((index * -1) - 1)
            index > 0 -> importMap.getOrNull(index - 1)
            else -> null
        }
    }
}

@ExperimentalUnsignedTypes
class FCustomVersion : UEClass {
    var key: FGuid
    var version: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        key = FGuid(Ar)
        version = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        key.serialize(Ar)
        Ar.writeInt32(version)
        super.completeWrite(Ar)
    }

    constructor(key: FGuid, version: Int) {
        this.key = key
        this.version = version
    }
}

@ExperimentalUnsignedTypes
class FGenerationInfo : UEClass {
    var exportCount: Int
    var nameCount: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        exportCount = Ar.readInt32()
        nameCount = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(exportCount)
        Ar.writeInt32(nameCount)
        super.completeWrite(Ar)
    }

    constructor(exportCount: Int, nameCount: Int) {
        this.exportCount = exportCount
        this.nameCount = nameCount
    }
}

@ExperimentalUnsignedTypes
class FEngineVersion : UEClass {
    var major: UShort
    var minor: UShort
    var patch: UShort
    var changelist: UInt
    var branch: String

    constructor(Ar: FArchive) {
        super.init(Ar)
        major = Ar.readUInt16()
        minor = Ar.readUInt16()
        patch = Ar.readUInt16()
        changelist = Ar.readUInt32()
        branch = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt16(major)
        Ar.writeUInt16(minor)
        Ar.writeUInt16(patch)
        Ar.writeUInt32(changelist)
        Ar.writeString(branch)
        super.completeWrite(Ar)
    }

    constructor(major: UShort, minor: UShort, patch: UShort, changelist: UInt, branch: String) {
        this.major = major
        this.minor = minor
        this.patch = patch
        this.changelist = changelist
        this.branch = branch
    }
}

@ExperimentalUnsignedTypes
class FCompressedChunk : UEClass {
    var uncompressedOffset: Int
    var uncompressedSize: Int
    var compressedOffset: Int
    var compressedSize: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        uncompressedOffset = Ar.readInt32()
        uncompressedSize = Ar.readInt32()
        compressedOffset = Ar.readInt32()
        compressedSize = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(uncompressedOffset)
        Ar.writeInt32(uncompressedSize)
        Ar.writeInt32(compressedOffset)
        Ar.writeInt32(compressedSize)
        super.completeWrite(Ar)
    }

    constructor(uncompressedOffset: Int, uncompressedSize: Int, compressedOffset: Int, compressedSize: Int) {
        this.uncompressedOffset = uncompressedOffset
        this.uncompressedSize = uncompressedSize
        this.compressedOffset = compressedOffset
        this.compressedSize = compressedSize
    }
}

@ExperimentalUnsignedTypes
class FStripDataFlags : UEClass {
    var globalStripFlags : UByte
    var classStripFlags : UByte

    constructor(Ar: FArchive) {
        super.init(Ar)
        globalStripFlags = Ar.readUInt8()
        classStripFlags = Ar.readUInt8()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt8(globalStripFlags)
        Ar.writeUInt8(classStripFlags)
        super.completeWrite(Ar)
    }

    fun isEditorDataStripped() = (globalStripFlags and 1u) != 0.toUByte()
    fun isDataStrippedForServer() = (globalStripFlags and 2u) != 0.toUByte()
    fun isClassDataStripped(flag : UByte) = (classStripFlags and flag) != 0.toUByte()

    constructor(globalStripFlags : UByte, classStripFlags : UByte) {
        this.globalStripFlags = globalStripFlags
        this.classStripFlags = classStripFlags
    }
}

@ExperimentalUnsignedTypes
class FByteBulkData : UEClass {
    var header : FByteBulkDataHeader
    var data : ByteArray

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        header = FByteBulkDataHeader(Ar)
        val bulkDataFlags = header.bulkDataFlags
        data = ByteArray(header.elementCount)
        when {
            EBulkData.BULKDATA_Unused.check(bulkDataFlags) -> {
                logger.warn("Bulk with no data")
            }
            EBulkData.BULKDATA_ForceInlinePayload.check(bulkDataFlags) -> {
                logger.debug("bulk data in .uexp file (Force Inline Payload) (flags=$bulkDataFlags, pos=${header.offsetInFile}, size=${header.sizeOnDisk})")
                Ar.read(data)
            }
            EBulkData.BULKDATA_PayloadInSeperateFile.check(bulkDataFlags) -> {
                logger.debug("bulk data in .ubulk file (Payload In Seperate File) (flags=$bulkDataFlags, pos=${header.offsetInFile}, size=${header.sizeOnDisk})")
                val ubulkAr = Ar.getPayload(PayloadType.UBULK)
                ubulkAr.seek(header.offsetInFile.toInt())
                ubulkAr.read(data)
            }
            EBulkData.BULKDATA_OptionalPayload.check(bulkDataFlags) -> {
                throw ParserException("TODO: Uptnl", Ar)
            }
            EBulkData.BULKDATA_PayloadAtEndOfFile.check(bulkDataFlags) -> {
                //stored in same file, but at different position
                //save archive position
                val savePos = Ar.pos()
                if (header.offsetInFile.toInt() + header.elementCount <= Ar.size()) {
                    Ar.seek(header.offsetInFile.toInt())
                    Ar.read(data)
                } else {
                    throw ParserException("Failed to read PayloadAtEndOfFile, ${header.offsetInFile} is out of range", Ar)
                }
                Ar.seek(savePos)
            }
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        val bulkDataFlags = header.bulkDataFlags
        when {
            EBulkData.BULKDATA_Unused.check(bulkDataFlags) -> {
                header.serialize(Ar)
            }
            EBulkData.BULKDATA_ForceInlinePayload.check(bulkDataFlags) -> {
                header.offsetInFile = (Ar.relativePos() + 28).toLong()
                header.elementCount = data.size
                header.sizeOnDisk = data.size
                header.serialize(Ar)
                Ar.write(data)
            }
            EBulkData.BULKDATA_PayloadInSeperateFile.check(bulkDataFlags) -> {
                val ubulkAr = Ar.getPayload(PayloadType.UBULK)
                header.offsetInFile = ubulkAr.relativePos().toLong()
                header.elementCount = data.size
                header.sizeOnDisk = data.size
                header.serialize(Ar)
                ubulkAr.write(data)
            }
            EBulkData.BULKDATA_OptionalPayload.check(bulkDataFlags) -> {
                throw ParserException("TODO: Uptnl")
            }
            else -> throw ParserException("Unsupported BulkData type $bulkDataFlags")
        }
        super.completeWrite(Ar)
    }

    constructor(header : FByteBulkDataHeader, data : ByteArray) {
        this.header = header
        this.data = data
    }


    enum class EBulkData(val bulkDataFlags : Int) {
        BULKDATA_PayloadAtEndOfFile(0x0001),           // bulk data stored at the end of this file, data offset added to global data offset in package
        BULKDATA_CompressedZlib(0x0002),               // the same value as for UE3
        BULKDATA_Unused(0x0020),                       // the same value as for UE3
        BULKDATA_ForceInlinePayload(0x0040),           // bulk data stored immediately after header
        BULKDATA_PayloadInSeperateFile(0x0100),        // data stored in .ubulk file near the asset (UE4.12+)
        BULKDATA_SerializeCompressedBitWindow(0x0200), // use platform-specific compression
        BULKDATA_OptionalPayload(0x0800);               // same as BULKDATA_PayloadInSeperateFile, but stored with .uptnl extension (UE4.20+)

        fun check(bulkDataFlags: Int) = (this.bulkDataFlags and bulkDataFlags) != 0
    }
}

@ExperimentalUnsignedTypes
class FByteBulkDataHeader : UEClass {
    var bulkDataFlags : Int
    var elementCount : Int
    var sizeOnDisk : Int
    var offsetInFile : Long

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        bulkDataFlags = Ar.readInt32()
        elementCount = Ar.readInt32()
        sizeOnDisk = Ar.readInt32()
        offsetInFile = Ar.readInt64() + (Ar.info?.bulkDataStartOffset ?: 0)
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeInt32(bulkDataFlags)
        Ar.writeInt32(elementCount)
        Ar.writeInt32(sizeOnDisk)
        Ar.writeInt64(offsetInFile)
        super.completeWrite(Ar)
    }

    constructor(bulkDataFlags: Int, elementCount : Int, sizeOnDisk : Int, offsetInFile : Long) {
        this.bulkDataFlags = bulkDataFlags
        this.elementCount = elementCount
        this.sizeOnDisk = sizeOnDisk
        this.offsetInFile = offsetInFile
    }
}

