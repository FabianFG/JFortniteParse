package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.unprefix
import me.fungames.jfortniteparse.ue4.objects.FFieldPath
import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftClassPath
import me.fungames.jfortniteparse.ue4.objects.uobject.FSoftObjectPath
import java.lang.reflect.Field
import java.lang.reflect.GenericArrayType
import java.lang.reflect.ParameterizedType

class FPropertyData {
    @JvmField var name: String? = null
    @JvmField var type = FPropertyTypeData()
    @JvmField var arrayDim = 1

    var field: Field? = null

    constructor()

    constructor(field: Field, ann: UProperty?) {
        this.field = field

        if (ann != null) {
            name = ann.name.takeIf { it.isNotEmpty() }
            arrayDim = ann.arrayDim
            type.enumType = ann.enumType.takeIf { it.isNotEmpty() }
        }
        if (name == null) {
            name = field.name
        }

        type.setupWithField(field)
    }
}

@Suppress("UNCHECKED_CAST")
class FPropertyTypeData {
    @JvmField var type = FName.NAME_None
    @JvmField var structType = FName.NAME_None
    @JvmField var bool = false
    @JvmField var enumName = FName.NAME_None
    @JvmField var enumType: String? = null
    @JvmField var innerType: FPropertyTypeData? = null
    @JvmField var valueType: FPropertyTypeData? = null
    var structClass: Class<*>? = null
    var enumClass: Class<out Enum<*>>? = null

    constructor()

    constructor(tag: FPropertyTag) {
        type = tag.type
        structType = tag.structName
        bool = tag.boolVal
        enumName = tag.enumName
        enumType = tag.enumType
        innerType = FPropertyTypeData().apply {
            type = tag.innerType
        }
        valueType = FPropertyTypeData().apply {
            type = tag.valueType
        }
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
            valueType = FPropertyTypeData()
            valueType
        } else {
            innerType = FPropertyTypeData()
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