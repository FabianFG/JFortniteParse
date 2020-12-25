package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.UStruct
import me.fungames.jfortniteparse.ue4.assets.enums.ETextHistoryType
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.reader.FExportArchive
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.FFieldPath
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FTextHistory
import me.fungames.jfortniteparse.ue4.objects.uobject.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

sealed class FProperty {
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
            value is UScriptArray && List::class.java.isAssignableFrom(clazz) && type != null -> {
                val typeArgs = (type as ParameterizedType).actualTypeArguments
                val innerType = typeArgs[0]
                value.contents.mapIndexed { i, tag ->
                    val mapped = if (innerType is ParameterizedType) {
                        tag.getTagTypeValue(innerType.rawType as Class<*>, innerType)
                    } else {
                        tag.getTagTypeValue(innerType as Class<Any>)
                    }
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
            value is FPackageIndex && Lazy::class.java.isAssignableFrom(clazz) -> value.owner?.findObject<UObject>(value)
            this is EnumProperty && clazz.isEnum ->
                if (enumConstant != null) {
                    enumConstant // already searched by the unversioned property serializer
                } else {
                    val storedEnum = name.text
                    val sep = storedEnum.indexOf("::")
                    if (sep != -1 && clazz.simpleName != storedEnum.substring(0, sep)) {
                        null
                    } else {
                        val search = storedEnum.substringAfter("::")
                        clazz.enumConstants.firstOrNull { (it as Enum<*>).name == search }
                    }
                }
            else -> null
        } as T
    }

    //@Deprecated(message = "Should not be used anymore, since its not able to process arrays and struct fallback", replaceWith = ReplaceWith("getTagTypeValue<T>"))
    fun getTagTypeValueLegacy() = when (this) {
        is BoolProperty -> this.bool
        is StructProperty -> this.struct.structType
        is ObjectProperty -> this.index
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
        is SoftClassProperty -> this.`object`
        is DelegateProperty -> this.delegate
        is MulticastDelegateProperty -> this.delegate
        is DoubleProperty -> this.number
        is Int8Property -> this.number
        is Int16Property -> this.number
        is Int64Property -> this.number
        is FieldPathProperty -> this.fieldPath
    }

    fun setTagTypeValue(value: Any?) {
        if (value == null)
            return
        when (this) {
            is BoolProperty -> this.bool = value as Boolean
            is StructProperty -> this.struct.structType = value
            is ObjectProperty -> this.index = value as FPackageIndex
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
            is SoftClassProperty -> this.`object` = value as FSoftClassPath
            is DelegateProperty -> this.delegate = value as FScriptDelegate
            is MulticastDelegateProperty -> this.delegate = value as FMulticastScriptDelegate
            is DoubleProperty -> this.number = value as Double
            is Int8Property -> this.number = value as Byte
            is Int16Property -> this.number = value as Short
            is Int64Property -> this.number = value as Long
            is FieldPathProperty -> this.fieldPath = value as FFieldPath
        }
    }

    companion object {
        fun readPropertyValue(Ar: FAssetArchive, typeData: PropertyType, type: ReadType) =
            when (val propertyType = typeData.type.text) {
                "BoolProperty" -> BoolProperty(when (type) {
                    ReadType.NORMAL -> if (Ar.useUnversionedPropertySerialization) Ar.readFlag() else typeData.bool
                    ReadType.MAP, ReadType.ARRAY -> Ar.readFlag()
                    ReadType.ZERO -> typeData.bool
                })
                "StructProperty" -> StructProperty(UScriptStruct(Ar, typeData, type))
                "ObjectProperty" -> ObjectProperty(valueOr({ FPackageIndex(Ar) }, { FPackageIndex(0, Ar.owner) }, type))
                "InterfaceProperty" -> InterfaceProperty(valueOr({ UInterfaceProperty(Ar) }, { UInterfaceProperty(0u) }, type))
                "FloatProperty" -> FloatProperty(valueOr({ Ar.readFloat32() }, { 0f }, type))
                "TextProperty" -> TextProperty(valueOr({ FText(Ar) }, { FText(0u, ETextHistoryType.None, FTextHistory.None()) }, type))
                "StrProperty" -> StrProperty(valueOr({ Ar.readString() }, { "" }, type))
                "NameProperty" -> NameProperty(valueOr({ Ar.readFName() }, { FName.NAME_None }, type))
                "IntProperty" -> IntProperty(valueOr({ Ar.readInt32() }, { 0 }, type))
                "UInt16Property" -> UInt16Property(valueOr({ Ar.readUInt16() }, { 0u }, type))
                "UInt32Property" -> UInt32Property(valueOr({ Ar.readUInt32() }, { 0u }, type))
                "UInt64Property" -> UInt64Property(valueOr({ Ar.readUInt64() }, { 0u }, type))
                "ArrayProperty" ->
                    ArrayProperty(if (type != ReadType.ZERO) {
                        UScriptArray(Ar, typeData)
                    } else {
                        UScriptArray(null, mutableListOf())
                    })
                "SetProperty" ->
                    SetProperty(if (type != ReadType.ZERO) {
                        UScriptArray(Ar, typeData)
                    } else {
                        UScriptArray(null, mutableListOf())
                    })
                "MapProperty" ->
                    MapProperty(if (type != ReadType.ZERO) {
                        UScriptMap(Ar, typeData)
                    } else {
                        UScriptMap(0, mutableMapOf())
                    })
                "ByteProperty" ->
                    if (Ar.useUnversionedPropertySerialization && type == ReadType.NORMAL) {
                        ByteProperty(Ar.readUInt8())
                    } else if (Ar.useUnversionedPropertySerialization && type == ReadType.ZERO) {
                        ByteProperty(0u)
                    } else if (type == ReadType.MAP || !typeData.enumName.isNone()) {
                        EnumProperty(Ar.readFName(), null) // TEnumAsByte
                    } else {
                        ByteProperty(Ar.readUInt8())
                    }
                "EnumProperty" ->
                    if (type == ReadType.NORMAL && typeData.enumName.isNone()) {
                        EnumProperty(FName.NAME_None, null)
                    } else if (type != ReadType.MAP && type != ReadType.ARRAY && Ar.useUnversionedPropertySerialization) {
                        val ordinal = valueOr({ if (typeData.isEnumAsByte) Ar.read() else Ar.readInt32() }, { 0 }, type)
                        val enumClass = typeData.enumClass
                        if (enumClass != null) { // reflection
                            val enumValue = enumClass.enumConstants.getOrNull(ordinal)
                                ?: throw ParserException("Failed to get enum index $ordinal for enum ${enumClass.simpleName}", Ar)
                            val fakeName = (typeData.enumName.text + "::" + enumValue).also((Ar as FExportArchive)::checkDummyName)
                            EnumProperty(FName.dummy(fakeName), enumValue)
                        } else { // loaded from mappings provider
                            val enumValue = Ar.provider!!.mappingsProvider.getEnum(typeData.enumName).getOrNull(ordinal)
                                ?: throw ParserException("Failed to get enum index $ordinal for enum ${typeData.enumName}", Ar)
                            val fakeName = (typeData.enumName.text + "::" + enumValue).also((Ar as FExportArchive)::checkDummyName)
                            EnumProperty(FName.dummy(fakeName), null)
                        }
                    } else {
                        EnumProperty(Ar.readFName(), null)
                    }
                "SoftObjectProperty" -> SoftObjectProperty(valueOr({ FSoftObjectPath(Ar) }, { FSoftObjectPath() }, type).apply { owner = Ar.owner })
                "SoftClassProperty" -> SoftClassProperty(valueOr({ FSoftClassPath(Ar) }, { FSoftClassPath() }, type).apply { owner = Ar.owner })
                "DelegateProperty" -> DelegateProperty(valueOr({ FScriptDelegate(Ar) }, { FScriptDelegate(FPackageIndex(), FName.NAME_None) }, type))
                "MulticastDelegateProperty" -> MulticastDelegateProperty(valueOr({ FMulticastScriptDelegate(Ar) }, { FMulticastScriptDelegate(mutableListOf()) }, type))
                "DoubleProperty" -> DoubleProperty(valueOr({ Ar.readDouble() }, { 0.0 }, type))
                "Int8Property" -> Int8Property(valueOr({ Ar.readInt8() }, { 0 }, type))
                "Int16Property" -> Int16Property(valueOr({ Ar.readInt16() }, { 0 }, type))
                "Int64Property" -> Int64Property(valueOr({ Ar.readInt64() }, { 0 }, type))
                "FieldPathProperty" -> FieldPathProperty(valueOr({ FFieldPath(Ar) }, { FFieldPath() }, type))
                //"LazyObjectProperty" -> throw ParserException("LazyObjectProperty not implemented yet")

                else -> {
                    UClass.logger.warn("Couldn't read property type $propertyType at ${Ar.pos()}")
                    null
                }
            }

        fun writePropertyValue(Ar: FAssetArchiveWriter, tag: FProperty, type: ReadType) {
            when (tag) {
                is StructProperty -> tag.struct.serialize(Ar)
                is ObjectProperty -> tag.index.serialize(Ar)
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
                is DelegateProperty -> tag.delegate.serialize(Ar)
                is MulticastDelegateProperty -> tag.delegate.serialize(Ar)
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

    class BoolProperty(var bool: Boolean) : FProperty()
    class StructProperty(var struct: UScriptStruct) : FProperty()
    class ObjectProperty(var index: FPackageIndex) : FProperty()
    class InterfaceProperty(var interfaceProperty: UInterfaceProperty) : FProperty()
    class FloatProperty(var float: Float) : FProperty()
    class TextProperty(var text: FText) : FProperty()
    class StrProperty(var str: String) : FProperty()
    class NameProperty(var name: FName) : FProperty()
    class IntProperty(var number: Int) : FProperty()
    class UInt16Property(var number: UShort) : FProperty()
    class UInt32Property(var number: UInt) : FProperty()
    class UInt64Property(var number: ULong) : FProperty()
    class ArrayProperty(var array: UScriptArray) : FProperty()
    class SetProperty(var array: UScriptArray) : FProperty()
    class MapProperty(var map: UScriptMap) : FProperty()
    class ByteProperty(var byte: UByte) : FProperty()
    class EnumProperty(var name: FName, var enumConstant: Enum<*>?) : FProperty()
    class SoftObjectProperty(var `object`: FSoftObjectPath) : FProperty()
    class SoftClassProperty(var `object`: FSoftClassPath) : FProperty()
    class DelegateProperty(var delegate: FScriptDelegate) : FProperty()
    class MulticastDelegateProperty(var delegate: FMulticastScriptDelegate) : FProperty()
    class DoubleProperty(var number: Double) : FProperty()
    class Int8Property(var number: Byte) : FProperty()
    class Int16Property(var number: Short) : FProperty()
    class Int64Property(var number: Long) : FProperty()
    class FieldPathProperty(var fieldPath: FFieldPath) : FProperty()

    enum class ReadType {
        NORMAL,
        MAP,
        ARRAY,
        ZERO
    }
}