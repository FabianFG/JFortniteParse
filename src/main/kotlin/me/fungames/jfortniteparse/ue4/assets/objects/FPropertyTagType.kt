package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.enums.ETextHistoryType
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FTextHistory
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.objects.uobject.UInterfaceProperty
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.deserializeUnversionedProperties
import java.lang.reflect.Array
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

sealed class FPropertyTagType(val propertyType: String) {
    inline fun <reified T> getTagTypeValue(): T? {
        val value = getTagTypeValue(T::class.java)
        return if (value is T) value else null
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getTagTypeValue(clazz: Class<T>, type: Type? = null): T? {
        val value = getTagTypeValueLegacy()
        return when {
            clazz.isAssignableFrom(value::class.java) -> value
            value is Boolean && clazz == Boolean::class.javaPrimitiveType -> value
            value is Byte && clazz == Byte::class.javaPrimitiveType -> value
            value is Short && clazz == Short::class.javaPrimitiveType -> value
            value is Char && clazz == Char::class.javaPrimitiveType -> value
            value is Int && clazz == Int::class.javaPrimitiveType -> value
            value is Long && clazz == Long::class.javaPrimitiveType -> value
            value is Float && clazz == Float::class.javaPrimitiveType -> value
            value is Double && clazz == Double::class.javaPrimitiveType -> value
            value is FStructFallback && clazz.isAnnotationPresent(UStruct::class.java) -> value.mapToClass(clazz)
            value is UScriptArray && clazz.isArray -> {
                val content = clazz.componentType
                val array = Array.newInstance(content, value.contents.size)
                value.contents.forEachIndexed { i, tag ->
                    val data = tag.getTagTypeValue(content)
                    if (data != null)
                        Array.set(array, i, data)
                    else
                        UClass.logger.error { "Failed to get value at index $i in UScriptArray for content class ${content::class.java.simpleName}" }
                }
                array
            }
            value is UScriptArray && List::class.java.isAssignableFrom(clazz) && type != null -> {
                val typeArgs = (type as ParameterizedType).actualTypeArguments
                val innerType = typeArgs[0]
                value.contents.mapIndexed { i, tag ->
                    val mapped = if (innerType is ParameterizedType) {
                        tag.getTagTypeValue(innerType.rawType as Class<*>, innerType)
                    } else {
                        tag.getTagTypeValue(innerType as Class<Any>)
                    }
                    if (mapped == null)
                        UClass.logger.error { "Failed to get value at index $i in UScriptArray for content class ${innerType::class.java.simpleName}" }
                    mapped
                }
            }
            value is UScriptMap && Map::class.java.isAssignableFrom(clazz) && type != null -> {
                val typeArgs = (type as ParameterizedType).actualTypeArguments
                val keyType = typeArgs[0]
                val valueType = typeArgs[1]
                val map = linkedMapOf<Any?, Any?>()
                value.mapData.forEach { (k, v) ->
                    val mappedKey = if (keyType is ParameterizedType) {
                        k.getTagTypeValue(keyType.rawType as Class<*>, keyType)
                    } else {
                        k.getTagTypeValue(keyType as Class<Any>)
                    }
                    val mappedValue = if (valueType is ParameterizedType) {
                        v.getTagTypeValue(valueType.rawType as Class<*>, valueType)
                    } else {
                        v.getTagTypeValue(valueType as Class<Any>)
                    }
                    map[mappedKey] = mappedValue
                }
                map
            }
            value is FPackageIndex && UExport::class.java.isAssignableFrom(clazz) -> {
                val export = value.owner?.loadObjectGeneric(value)
                if (export != null && clazz.isAssignableFrom(export::class.java)) export else null
            }
            value is FSoftObjectPath && UExport::class.java.isAssignableFrom(clazz) -> {
                val export = value.owner?.provider?.loadObject(value)
                if (export != null && clazz.isAssignableFrom(export::class.java)) export else null
            }
            this is EnumProperty && clazz.isEnum -> {
                val storedEnum = this.name.text
                if (clazz.simpleName != storedEnum.substringBefore("::"))
                    null
                else {
                    val search = storedEnum.substringAfter("::")
                    val values = clazz.enumConstants
                    val names = clazz.fields.mapNotNull { if (!it.isSynthetic && it.name != "Companion") it.name else null }
                    val idx = names.indexOfFirst { it == search }
                    values.getOrNull(idx)
                }
            }
            else -> null
        } as T
    }

    //@Deprecated(message = "Should not be used anymore, since its not able to process arrays and struct fallback", replaceWith = ReplaceWith("getTagTypeValue<T>"))
    fun getTagTypeValueLegacy() = when (this) {
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
        is SetProperty -> this.array
        is MapProperty -> this.map
        is ByteProperty -> this.byte
        is EnumProperty -> this.name
        is SoftObjectProperty -> this.`object`
        is DelegateProperty -> this.name
        is DoubleProperty -> this.number
        is Int8Property -> this.number
        is Int16Property -> this.number
        is Int64Property -> this.number
    }

    fun setTagTypeValue(value: Any?) {
        if (value == null)
            return
        when (this) {
            is BoolProperty -> this.bool = value as Boolean
            is StructProperty -> this.struct.structType = value
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
            is SetProperty -> this.array = value as UScriptArray
            is MapProperty -> this.map = value as UScriptMap
            is ByteProperty -> this.byte = value as UByte
            is EnumProperty -> this.name = value as FName
            is SoftObjectProperty -> this.`object` = value as FSoftObjectPath
            is DelegateProperty -> this.name = value as FName
            is DoubleProperty -> this.number = value as Double
            is Int8Property -> this.number = value as Byte
            is Int16Property -> this.number = value as Short
            is Int64Property -> this.number = value as Long
        }
    }

    companion object {
        fun readFPropertyTagType(Ar: FAssetArchive, propertyType: String, tagData: FPropertyTag?, type: ReadType) =
            when (propertyType) {
                "BoolProperty" -> BoolProperty(when (type) {
                    ReadType.NORMAL -> if (Ar.useUnversionedPropertySerialization) Ar.readFlag() else tagData!!.boolVal
                    ReadType.MAP, ReadType.ARRAY -> Ar.readFlag()
                    ReadType.ZERO -> tagData!!.boolVal
                }, propertyType)
                "StructProperty" ->
                    if (Ar.useUnversionedPropertySerialization && tagData!!.structClass!!.isAnnotationPresent(UStruct::class.java)) {
                        //val struct = tagData.field!!.type.newInstance()
                        val properties = mutableListOf<FPropertyTag>()
                        if (type != ReadType.ZERO) {
                            deserializeUnversionedProperties(properties, tagData.structClass!!, Ar)
                        }
                        StructProperty(UScriptStruct(tagData.structName.text, FStructFallback(properties)), propertyType)
                    } else {
                        StructProperty(UScriptStruct(Ar, tagData!!.structName.text, type), propertyType)
                    }
                "ObjectProperty" -> ObjectProperty(valueOr({ FPackageIndex(Ar) }, { FPackageIndex(0, Ar.owner) }, type), propertyType)
                "InterfaceProperty" -> InterfaceProperty(valueOr({ UInterfaceProperty(Ar) }, { UInterfaceProperty(0u) }, type), propertyType)
                "FloatProperty" -> FloatProperty(valueOr({ Ar.readFloat32() }, { 0f }, type), propertyType)
                "TextProperty" -> TextProperty(valueOr({ FText(Ar) }, { FText(0u, ETextHistoryType.None, FTextHistory.None()) }, type), propertyType)
                "StrProperty" -> StrProperty(valueOr({ Ar.readString() }, { "" }, type), propertyType)
                "NameProperty" -> NameProperty(valueOr({ Ar.readFName() }, { FName.NAME_None }, type), propertyType)
                "IntProperty" -> IntProperty(valueOr({ Ar.readInt32() }, { 0 }, type), propertyType)
                "UInt16Property" -> UInt16Property(valueOr({ Ar.readUInt16() }, { 0u }, type), propertyType)
                "UInt32Property" -> UInt32Property(valueOr({ Ar.readUInt32() }, { 0u }, type), propertyType)
                "UInt64Property" -> UInt64Property(valueOr({ Ar.readUInt64() }, { 0u }, type), propertyType)
                "ArrayProperty" ->
                    ArrayProperty(if (type != ReadType.ZERO) {
                        UScriptArray(Ar, tagData!!)
                    } else {
                        UScriptArray(tagData, mutableListOf(), tagData?.innerType?.text ?: "ZeroUnknown")
                    }, propertyType)
                "SetProperty" ->
                    SetProperty(if (type != ReadType.ZERO) {
                        UScriptArray(Ar, tagData!!)
                    } else {
                        UScriptArray(tagData, mutableListOf(), tagData?.innerType?.text ?: "ZeroUnknown")
                    }, propertyType)
                "MapProperty" -> MapProperty(UScriptMap(Ar, tagData!!), propertyType)
                "ByteProperty" -> when (type) {
                    ReadType.NORMAL -> {
                        if (!Ar.useUnversionedPropertySerialization && tagData?.enumName != null && !tagData.enumName.isNone()) {
                            EnumProperty(Ar.readFName(), propertyType) // TEnumAsByte
                        } else {
                            ByteProperty(Ar.readUInt8(), propertyType)
                        }
                    }
                    ReadType.MAP -> ByteProperty(Ar.readUInt32().toUByte(), propertyType)
                    ReadType.ARRAY -> ByteProperty(Ar.readUInt8(), propertyType)
                    ReadType.ZERO -> ByteProperty(0u, propertyType)
                }
                "EnumProperty" ->
                    if (type == ReadType.NORMAL && (tagData == null || tagData.enumName.isNone())) {
                        EnumProperty(FName.NAME_None, propertyType)
                    } else if (Ar.useUnversionedPropertySerialization) {
                        val enumValue = if (type == ReadType.ZERO) {
                            tagData!!.enumClass!!.enumConstants[0]
                        } else {
                            tagData!!.enumClass!!.enumConstants[if (tagData.enumType == "IntProperty") Ar.readInt32() else Ar.read()]
                        }
                        EnumProperty(FName.dummy(tagData.enumName.text + "::" + enumValue), propertyType)
                    } else {
                        EnumProperty(Ar.readFName(), propertyType)
                    }
                "SoftObjectProperty" -> {
                    if (type == ReadType.ZERO) {
                        SoftObjectProperty(FSoftObjectPath(FName(), ""), propertyType)
                    } else {
                        val path = SoftObjectProperty(FSoftObjectPath(Ar), propertyType)
                        if (type == ReadType.MAP) {
                            Ar.skip(4)
                        }
                        path
                    }
                }
                "DelegateProperty" -> DelegateProperty(valueOr({ Ar.readInt32() }, { 0 }, type), valueOr({ Ar.readFName() }, { FName() }, type), propertyType)
                "DoubleProperty" -> DoubleProperty(valueOr({ Ar.readDouble() }, { 0.0 }, type), propertyType)
                "Int8Property" -> Int8Property(valueOr({ Ar.readInt8() }, { 0 }, type), propertyType)
                "Int16Property" -> Int16Property(valueOr({ Ar.readInt16() }, { 0 }, type), propertyType)
                "Int64Property" -> Int64Property(valueOr({ Ar.readInt64() }, { 0 }, type), propertyType)
                /*"MulticastDelegateProperty" -> throw ParserException("MulticastDelegateProperty not implemented yet")
                "LazyObjectProperty" -> throw ParserException("LazyObjectProperty not implemented yet")*/

                else -> {
                    UClass.logger.warn("Couldn't read property type $propertyType at ${Ar.pos()}")
                    null
                }
            }

        fun writeFPropertyTagType(Ar: FAssetArchiveWriter, tag: FPropertyTagType, type: ReadType) {
            when (tag) {
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
                is SetProperty -> tag.array.serialize(Ar)
                is MapProperty -> tag.map.serialize(Ar)
                is ByteProperty -> when (type) {
                    ReadType.NORMAL -> {
                        Ar.writeInt32(tag.byte.toInt())
                        Ar.writeInt32(0)
                    }
                    ReadType.MAP -> Ar.writeUInt32(tag.byte.toUInt())
                    ReadType.ARRAY -> Ar.writeUInt8(tag.byte)
                }
                is EnumProperty -> {
                    if (tag.name !is FName.FNameDummy)
                        Ar.writeFName(tag.name)
                }
                is SoftObjectProperty -> {
                    tag.`object`.serialize(Ar)
                    if (type == ReadType.MAP)
                        Ar.writeInt32(0)
                }
                is DelegateProperty -> {
                    Ar.writeInt32(tag.`object`)
                    Ar.writeFName(tag.name)
                }
                is DoubleProperty -> Ar.writeDouble(tag.number)
                is Int8Property -> Ar.writeInt8(tag.number)
                is Int16Property -> Ar.writeInt16(tag.number)
                is Int64Property -> Ar.writeInt64(tag.number)
                is BoolProperty -> {
                    if (type == ReadType.MAP || type == ReadType.ARRAY) Ar.writeFlag(tag.bool)
                }
            }
        }

        inline fun <T> valueOr(valueIfNonzero: () -> T, valueIfZero: () -> T, type: ReadType) =
            if (type != ReadType.ZERO) valueIfNonzero() else valueIfZero()
    }

    class BoolProperty(var bool: Boolean, propertyType: String) : FPropertyTagType(propertyType)
    class StructProperty(var struct: UScriptStruct, propertyType: String) : FPropertyTagType(propertyType)
    class ObjectProperty(var `object`: FPackageIndex, propertyType: String) : FPropertyTagType(propertyType)
    class InterfaceProperty(var interfaceProperty: UInterfaceProperty, propertyType: String) : FPropertyTagType(propertyType)
    class FloatProperty(var float: Float, propertyType: String) : FPropertyTagType(propertyType)
    class TextProperty(var text: FText, propertyType: String) : FPropertyTagType(propertyType)
    class StrProperty(var str: String, propertyType: String) : FPropertyTagType(propertyType)
    class NameProperty(var name: FName, propertyType: String) : FPropertyTagType(propertyType)
    class IntProperty(var number: Int, propertyType: String) : FPropertyTagType(propertyType)
    class UInt16Property(var number: UShort, propertyType: String) : FPropertyTagType(propertyType)
    class UInt32Property(var number: UInt, propertyType: String) : FPropertyTagType(propertyType)
    class UInt64Property(var number: ULong, propertyType: String) : FPropertyTagType(propertyType)
    class ArrayProperty(var array: UScriptArray, propertyType: String) : FPropertyTagType(propertyType)
    class SetProperty(var array: UScriptArray, propertyType: String) : FPropertyTagType(propertyType)
    class MapProperty(var map: UScriptMap, propertyType: String) : FPropertyTagType(propertyType)
    class ByteProperty(var byte: UByte, propertyType: String) : FPropertyTagType(propertyType)
    class EnumProperty(var name: FName, propertyType: String) : FPropertyTagType(propertyType)
    class SoftObjectProperty(var `object`: FSoftObjectPath, propertyType: String) : FPropertyTagType(propertyType)
    class DelegateProperty(var `object`: Int, var name: FName, propertyType: String) : FPropertyTagType(propertyType)
    class DoubleProperty(var number: Double, propertyType: String) : FPropertyTagType(propertyType)
    class Int8Property(var number: Byte, propertyType: String) : FPropertyTagType(propertyType)
    class Int16Property(var number: Short, propertyType: String) : FPropertyTagType(propertyType)
    class Int64Property(var number: Long, propertyType: String) : FPropertyTagType(propertyType)

    enum class ReadType {
        NORMAL,
        MAP,
        ARRAY,
        ZERO
    }
}