package me.fungames.jfortniteparse.ue4.assets.objects

import com.google.gson.JsonObject
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.unprefix
import me.fungames.jfortniteparse.ue4.objects.FFieldPath
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName.Companion.NAME_None
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftClassPath
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import java.lang.reflect.Field
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType

@Suppress("UNCHECKED_CAST")
class PropertyType(
    @JvmField var type: FName) {
    @JvmField var structType = NAME_None
    @JvmField var bool = false
    @JvmField var enumName = NAME_None
    @JvmField var isEnumAsByte = true
    @JvmField var innerType: PropertyType? = null
    @JvmField var valueType: PropertyType? = null
    var structClass: Class<*>? = null
    var enumClass: Class<out Enum<*>>? = null

    constructor() : this(NAME_None)

    constructor(json: JsonObject) : this() {

    }

    constructor(tag: FPropertyTag) : this(tag.name) {
        type = tag.type
        structType = tag.structName
        bool = tag.boolVal
        enumName = tag.enumName
        innerType = PropertyType(tag.innerType)
        valueType = PropertyType(tag.valueType)
    }

    fun setupWithField(field: Field) {
        var fieldType = field.type
        if (field.type.isArray) {
            fieldType = fieldType.componentType
        }
        type = classToPropertyType(fieldType)

        when (type.text) {
            "EnumProperty" -> {
                enumName = FName.dummy(fieldType.simpleName)
                enumClass = fieldType as Class<out Enum<*>>?
            }
            "StructProperty" -> {
                structType = FName.dummy(fieldType.simpleName.unprefix())
                structClass = fieldType
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
        val type = typeArgs[idx] as Class<*>
        val propertyType = classToPropertyType(type)
        if (applyToValue) {
            valueType = PropertyType()
            valueType
        } else {
            innerType = PropertyType()
            innerType
        }!!.apply {
            this.type = propertyType
            if (propertyType.text == "EnumProperty") {
                enumName = FName.dummy(type.simpleName.unprefix())
                enumClass = type as Class<out Enum<*>>?
            } else if (propertyType.text == "StructProperty") {
                structType = FName.dummy(type.simpleName.unprefix())
                structClass = type
            }
        }
    }

    private fun classToPropertyType(c: Class<*>) = FName.dummy(when {
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
        c == FSoftObjectPath::class.java -> "SoftObjectProperty"
        c == FSoftClassPath::class.java -> "SoftClassProperty"
        c == FFieldPath::class.java -> "FieldPathProperty"
        else -> "StructProperty"
    })
}