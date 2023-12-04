package me.fungames.jfortniteparse.ue4.assets.objects

import com.google.gson.JsonObject
import me.fungames.jfortniteparse.ue4.assets.exports.*
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.assets.unprefix
import me.fungames.jfortniteparse.ue4.objects.FFieldPath
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.*
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import java.lang.reflect.Field
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
class PropertyType {
    @JvmField var type: FName
    @JvmField var structName = NAME_None
    @JvmField var bool = false
    @JvmField var enumName = NAME_None
    @JvmField var isEnumAsByte = true
    @JvmField var innerType: PropertyType? = null
    @JvmField var valueType: PropertyType? = null
    var structClass: Lazy<UStruct?>? = null
    var enumClass: Lazy<UEnum?>? = null

    constructor() : this(NAME_None)

    constructor(type: FName) {
        this.type = type
    }

    constructor(json: JsonObject) {
        var type = json["type"].asString
        when (type) { // Replace unsupported property type names in usmap format
            "ClassProperty", "ObjectPtrProperty", "ClassPtrProperty" -> type = "ObjectProperty"
            "MulticastInlineDelegateProperty" -> type = "MulticastDelegateProperty"
            "MulticastSparseDelegateProperty" -> type = "MulticastDelegateProperty"
            "SoftClassProperty" -> type = "SoftObjectProperty"
        }
        this.type = FName(type)
        when (type) {
            "ByteProperty" -> {
                json["enumName"]?.run { enumName = FName(asString) }
            }
            "EnumProperty" -> {
                innerType = PropertyType(json["innerType"].asJsonObject).also {
                    isEnumAsByte = it.type.text == "ByteProperty"
                }
                enumName = FName(json["enumName"].asString)
            }
            "OptionalProperty" -> innerType = PropertyType(json["innerType"].asJsonObject)
            "StructProperty" -> structName = FName(json["structType"].asString) // key name should be structName
            "SetProperty", "ArrayProperty" -> innerType = PropertyType(json["innerType"].asJsonObject)
            "MapProperty" -> {
                innerType = PropertyType(json["innerType"].asJsonObject)
                valueType = PropertyType(json["valueType"].asJsonObject)
            }
        }
    }

    constructor(tag: FPropertyTag) : this(tag.name) {
        type = tag.type
        structName = tag.structName
        bool = tag.boolVal
        enumName = tag.enumName
        innerType = PropertyType(tag.innerType)
        valueType = PropertyType(tag.valueType)
    }

    constructor(prop: FPropertySerialized) : this(NAME_None) {
        type = FName(prop.javaClass.simpleName.unprefix())
        when (prop) {
            is FArrayProperty -> {
                innerType = prop.inner?.let { PropertyType(it) }
            }
            is FByteProperty -> applyEnum(prop, prop.enum)
            is FEnumProperty -> applyEnum(prop, prop.enum)
            is FMapProperty -> {
                innerType = prop.keyProp?.let { PropertyType(it) }
                valueType = prop.valueProp?.let { PropertyType(it) }
            }
            is FSetProperty -> {
                innerType = prop.elementProp?.let { PropertyType(it) }
            }
            is FStructProperty -> {
                structClass = prop.struct
                structName = structClass?.value?.name?.let { FName(it) } ?: NAME_None
            }
        }
    }

    private inline fun applyEnum(prop: FPropertySerialized, enum_: Lazy<UEnum>?) {
        val enum = enum_?.value
        if (enum != null) {
            enumClass = enum_
            enumName = FName(enum.name)
        }
        isEnumAsByte = prop.elementSize == 1
    }

    fun setupWithField(field: Field) {
        var fieldType = field.type
        if (field.type.isArray) {
            fieldType = fieldType.componentType
        }
        type = classToPropertyType(fieldType)

        when (type.text) {
            "EnumProperty" -> {
                enumName = FName(fieldType.simpleName)
                enumClass = enumClassToUEnum(fieldType)
            }
            "StructProperty" -> {
                structName = FName(fieldType.simpleName.unprefix())
                structClass = lazy { UScriptStruct(fieldType) }
            }
            "ArrayProperty", "SetProperty" -> applyInner(field, false)
            "MapProperty" -> {
                applyInner(field, false)
                applyInner(field, true)
            }
        }
    }

    private fun applyInner(field: Field, applyToValue: Boolean) {
        val typeArgs = ((if (field.type.isArray)
            (field.genericType as GenericArrayType).genericComponentType
        else
            field.genericType) as ParameterizedType).actualTypeArguments
        val idx = if (applyToValue) 1 else 0
        val type = typeArgs[idx]
        val clazz = (if (type is ParameterizedType) type.rawType else type) as Class<*>
        val propertyType = classToPropertyType(clazz)
        if (applyToValue) {
            valueType = PropertyType()
            valueType
        } else {
            innerType = PropertyType()
            innerType
        }!!.apply {
            this.type = propertyType
            if (propertyType.text == "EnumProperty") {
                enumName = FName(clazz.simpleName.unprefix())
                enumClass = enumClassToUEnum(clazz)
            } else if (propertyType.text == "StructProperty") {
                structName = FName(clazz.simpleName.unprefix())
                structClass = lazy { UScriptStruct(clazz) }
            }
        }
    }

    private fun classToPropertyType(c: Class<*>) = FName(when {
        c == Boolean::class.javaPrimitiveType || c == Boolean::class.javaObjectType -> "BoolProperty"
        c == Char::class.javaPrimitiveType || c == Char::class.javaObjectType -> "CharProperty"
        c == Double::class.javaPrimitiveType || c == Double::class.javaObjectType -> "DoubleProperty"
        c == Float::class.javaPrimitiveType || c == Float::class.javaObjectType -> "FloatProperty"
        c == Byte::class.javaPrimitiveType || c == Byte::class.javaObjectType -> "Int8Property"
        c == Short::class.javaPrimitiveType || c == Short::class.javaObjectType -> "Int16Property"
        c == Int::class.javaPrimitiveType || c == Int::class.javaObjectType -> "IntProperty"
        c == Long::class.javaPrimitiveType || c == Long::class.javaObjectType -> "Int64Property"
        c == UByte::class.java -> "ByteProperty"
        c == UShort::class.java -> "UInt16Property"
        c == UInt::class.java -> "UInt32Property"
        c == ULong::class.java -> "UInt64Property"
        c == String::class.java -> "StrProperty"
        c == FName::class.java -> "NameProperty"
        c == FText::class.java -> "TextProperty"
        c.isEnum -> "EnumProperty"
        List::class.java.isAssignableFrom(c) -> "ArrayProperty"
        Set::class.java.isAssignableFrom(c) -> "SetProperty"
        Map::class.java.isAssignableFrom(c) -> "MapProperty"
        c == FPackageIndex::class.java || UObject::class.java.isAssignableFrom(c) -> "ObjectProperty"
        c == Lazy::class.java -> "ObjectProperty"
        c == FSoftObjectPath::class.java -> "SoftObjectProperty"
        c == FSoftClassPath::class.java -> "SoftClassProperty"
        c == FFieldPath::class.java -> "FieldPathProperty"
        c == FScriptDelegate::class.java -> "DelegateProperty"
        c == FMulticastScriptDelegate::class.java -> "MulticastDelegateProperty"
        else -> "StructProperty"
    })

    private fun enumClassToUEnum(fieldType: Class<*>) = lazy {
        val enum = UEnum()
        enum.name = fieldType.simpleName
        val values = fieldType.enumConstants
        enum.names = Array(values.size) {
            val value = values[it] as Enum<*>
            FName(enum.name + "::" + value.name) to it.toLong()
        }
        enum
    }

    override fun toString(): String {
        val type = type.text
        val sb = StringBuilder(type)
        when {
            type == "StructProperty" -> sb.append('<').append(structName).append('>')
            type == "ByteProperty" && !enumName.isNone() || type == "EnumProperty" -> sb.append('<').append(enumName).append('>')
            type == "ArrayProperty" || type == "SetProperty" -> sb.append('<').append(innerType).append('>')
            type == "MapProperty" -> sb.append('<').append(innerType).append(", ").append(valueType).append('>')
        }
        return sb.toString()
    }
}