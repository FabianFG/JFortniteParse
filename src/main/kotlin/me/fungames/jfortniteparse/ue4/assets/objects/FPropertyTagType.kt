package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.StructFallbackClass
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import me.fungames.jfortniteparse.ue4.objects.uobject.UInterfaceProperty
import java.lang.reflect.Array

@ExperimentalUnsignedTypes
sealed class FPropertyTagType(val propertyType: String) {
    inline fun <reified T> getTagTypeValue(): T? {
        val value = getTagTypeValue(T::class.java)
        return if (value is T) value else null
    }

    @Suppress("DEPRECATION", "UNCHECKED_CAST")
    fun <T> getTagTypeValue(clazz: Class<T>): T? {
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
            value is FStructFallback && clazz.isAnnotationPresent(StructFallbackClass::class.java) -> value.mapToClass(clazz)
            value is UScriptArray && clazz.isArray -> {
                val content = clazz.componentType
                val array = Array.newInstance(content, value.data.size)
                value.contents.forEachIndexed { i, tag ->
                    val data = tag.getTagTypeValue(content)
                    if (data != null)
                        Array.set(array, i, data)
                    else
                        UClass.logger.error { "Failed to get value at index $i in UScriptArray for content class ${content::class.java.simpleName}" }
                }
                array
            }
            value is FPackageIndex && UExport::class.java.isAssignableFrom(clazz) -> {
                val export = value.owner?.provider?.loadObject(value)
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
            //TODO maybe also add Map
            else -> null
        } as T
    }

    @Deprecated(message = "Should not be used anymore, since its not able to process arrays and struct fallback", replaceWith = ReplaceWith("getTagTypeValue<T>"))
    fun getTagTypeValueLegacy() : Any {
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
    }

    fun setTagTypeValue(value: Any?) {
        if (value == null)
            return
        when(this) {
            is BoolProperty -> this.bool = value as Boolean
            is StructProperty -> this.struct.structType = value as UClass
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
        fun readFPropertyTagType(
            Ar: FAssetArchive,
            propertyType: String,
            tagData: FPropertyTagData?,
            type : Type
        ): FPropertyTagType? {
            when (propertyType) {
                "BoolProperty" -> {
                    return when(type) {
                        Type.NORMAL -> BoolProperty(
                            tagData?.let { (it as? FPropertyTagData.BoolProperty)?.bool } == true,
                            propertyType
                        )
                        Type.MAP, Type.ARRAY -> BoolProperty(Ar.readFlag(), propertyType)
                    }
                }
                "StructProperty" -> return StructProperty(
                                    UScriptStruct(
                                        Ar,
                                        tagData?.let { (it as? FPropertyTagData.StructProperty)?.nameData?.text }
                                    ),
                                    propertyType
                                )
                "ObjectProperty" -> return ObjectProperty(
                    FPackageIndex(
                        Ar
                    ), propertyType
                )
                "InterfaceProperty" -> return InterfaceProperty(
                    UInterfaceProperty(Ar),
                    propertyType
                )
                "FloatProperty" -> return FloatProperty(
                    Ar.readFloat32(),
                    propertyType
                )
                "TextProperty" -> return TextProperty(
                    FText(Ar),
                    propertyType
                )
                "StrProperty" -> return StrProperty(
                    Ar.readString(),
                    propertyType
                )
                "NameProperty" -> return NameProperty(
                    Ar.readFName(),
                    propertyType
                )
                "IntProperty" -> return IntProperty(
                    Ar.readInt32(),
                    propertyType
                )
                "UInt16Property" -> return UInt16Property(
                    Ar.readUInt16(),
                    propertyType
                )
                "UInt32Property" -> return UInt32Property(
                    Ar.readUInt32(),
                    propertyType
                )
                "UInt64Property" -> return UInt64Property(
                    Ar.readUInt64(),
                    propertyType
                )
                "ArrayProperty" -> {
                    if (tagData != null)
                        return when (tagData) {
                            is FPropertyTagData.ArrayProperty -> ArrayProperty(
                                UScriptArray(
                                    Ar,
                                    tagData.property.text
                                ), propertyType
                            )
                            else -> {
                                UClass.logger.warn { "Cannot read array from given non-array" }
                                null
                            }
                        }
                    else {
                        UClass.logger.warn { "Array Property needs tag data" }
                        return null
                    }
                }
                "SetProperty" -> {
                    if (tagData != null)
                        return when (tagData) {
                            is FPropertyTagData.SetProperty -> SetProperty(
                                UScriptArray(
                                    Ar,
                                    tagData.property.text
                                ), propertyType
                            )
                            else -> {
                                UClass.logger.warn { "Cannot read set from given non-set" }
                                null
                            }
                        }
                    else {
                        UClass.logger.warn { "Set Property needs tag data" }
                        return null
                    }
                }
                "MapProperty" -> {
                    if (tagData != null)
                        return when (tagData) {
                            is FPropertyTagData.MapProperty -> {
                                MapProperty(
                                    UScriptMap(
                                        Ar,
                                        tagData
                                    ),
                                    propertyType
                                )
                            }
                            else -> {
                                UClass.logger.warn { "Cannot read map from given non-map" }
                                null
                            }
                        }
                    else {
                        UClass.logger.warn { "Map Property needs tag data" }
                        return null
                    }
                }
                "ByteProperty" -> {
                    return when(type) {
                        // Type.NORMAL -> ByteProperty(Ar.readFName().index.toUByte(), propertyType)
                        Type.NORMAL -> { // FIXME: this is a hack to match John Wick Parse's output
                            val nameIndex = Ar.readInt32()
                            if (nameIndex in Ar.owner.nameMap.indices)
                                NameProperty(FName(Ar.owner.nameMap, nameIndex, Ar.readInt32()), propertyType)
                            else
                                ByteProperty(nameIndex.toUByte(), propertyType)
                        }
                        Type.MAP -> ByteProperty(Ar.readUInt32().toUByte(), propertyType)
                        Type.ARRAY -> ByteProperty(Ar.readUInt8(), propertyType)
                    }
                }
                "EnumProperty" -> {
                    return if (type == Type.NORMAL && tagData?.let { (it as? FPropertyTagData.EnumProperty)?.enum?.text } == "None")
                        EnumProperty(FName.dummy("None"), propertyType)
                    else
                        EnumProperty(Ar.readFName(), propertyType)
                }
                "SoftObjectProperty" -> {
                    val path = SoftObjectProperty(
                        FSoftObjectPath(Ar),
                        propertyType
                    )
                    if (type == Type.MAP)
                        Ar.skip(4)
                    return path
                }
                "DelegateProperty" -> return DelegateProperty(Ar.readInt32(), Ar.readFName(), propertyType)
                "DoubleProperty" -> return DoubleProperty(Ar.readDouble(), propertyType)
                "Int8Property" -> return Int8Property(Ar.readInt8(), propertyType)
                "Int16Property" -> return Int16Property(Ar.readInt16(), propertyType)
                "Int64Property" -> return Int64Property(Ar.readInt64(), propertyType)
                /*"MulticastDelegateProperty" -> throw ParserException("MulticastDelegateProperty not implemented yet")
                "LazyObjectProperty" -> throw ParserException("LazyObjectProperty not implemented yet")*/

                else -> {
                    UClass.logger.warn("Couldn't read property type $propertyType at ${Ar.pos()}")
                    return null
                }
            }
        }
        fun writeFPropertyTagType(Ar: FAssetArchiveWriter, tag: FPropertyTagType, type: Type) {
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
                is SetProperty -> tag.array.serialize(Ar)
                is MapProperty -> tag.map.serialize(Ar)
                is ByteProperty -> when(type) {
                    Type.NORMAL -> {
                        Ar.writeInt32(tag.byte.toInt())
                        Ar.writeInt32(0)
                    }
                    Type.MAP -> Ar.writeUInt32(tag.byte.toUInt())
                    Type.ARRAY -> Ar.writeUInt8(tag.byte)
                }
                is EnumProperty -> {
                    if (tag.name !is FName.FNameDummy)
                        Ar.writeFName(tag.name)
                }
                is SoftObjectProperty -> {
                    tag.`object`.serialize(Ar)
                    if (type == Type.MAP)
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
                    if (type == Type.MAP || type == Type.ARRAY) Ar.writeFlag(tag.bool)
                }
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
    class SetProperty(var array: UScriptArray, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class MapProperty(var map: UScriptMap, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class ByteProperty(var byte: UByte, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class EnumProperty(var name: FName, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class SoftObjectProperty(var `object`: FSoftObjectPath, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class DelegateProperty(var `object`: Int, var name: FName, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class DoubleProperty(var number: Double, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class Int8Property(var number: Byte, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class Int16Property(var number: Short, propertyType: String) : FPropertyTagType(propertyType)

    @ExperimentalUnsignedTypes
    class Int64Property(var number: Long, propertyType: String) : FPropertyTagType(propertyType)

    enum class Type {
        NORMAL,
        MAP,
        ARRAY
    }
}