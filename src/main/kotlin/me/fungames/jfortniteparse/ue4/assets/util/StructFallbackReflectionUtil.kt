package me.fungames.jfortniteparse.ue4.assets.util

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.internal.reflect.ReflectionAccessor
import com.google.gson.reflect.TypeToken
import me.fungames.jfortniteparse.LOG_JFP
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.UProperty
import me.fungames.jfortniteparse.ue4.assets.exports.UClassReal
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.objects.IPropertyHolder
import org.objenesis.ObjenesisStd
import java.lang.reflect.Array
import java.lang.reflect.Field
import java.lang.reflect.GenericArrayType
import java.util.*

inline fun <reified T> IPropertyHolder.mapToClass() = mapToClass(properties, T::class.java)
inline fun <T> IPropertyHolder.mapToClass(clazz: Class<T>): T = mapToClass(properties, clazz)

inline fun <T> mapToClass(properties: List<FPropertyTag>, clazz: Class<T>): T = mapToClass(properties, clazz, ObjenesisStd().newInstance(clazz))
fun <T> mapToClass(properties: List<FPropertyTag>, clazz: Class<T>, obj: T): T {
    if (properties.isEmpty()) {
        return obj
    }
    try {
        val boundFields = getBoundFields(TypeToken.get(clazz), clazz)
        for (prop in properties) {
            val field = boundFields[prop.name.text] ?: continue
            writePropertyToField(prop, field, obj)
        }
        return obj
    } catch (e: ReflectiveOperationException) {
        throw ParserException("Object mapping failure", e)
    }
}

fun <T> writePropertyToField(prop: FPropertyTag, field: Field, obj: T) {
    val fieldType = field.type
    if (fieldType.isArray) {
        val componentType = fieldType.componentType
        var array = field.get(obj)
        if (array == null) {
            array = Array.newInstance(componentType, field.getAnnotation(UProperty::class.java).arrayDim)
            field.set(obj, array)
        }
        val content = prop.getTagTypeValue(componentType, (field.genericType as? GenericArrayType)?.genericComponentType)
        if (isContentValid(content, prop.name.text, componentType)) {
            Array.set(array, prop.arrayIndex, content)
        }
    } else {
        val content = prop.getTagTypeValue(fieldType, field.genericType)
        if (isContentValid(content, prop.name.text, fieldType)) {
            field.set(obj, content)
        }
    }
}

private fun isContentValid(content: Any?, name: String, clazz: Class<*>): Boolean {
    if (content == null) {
        if (clazz != Lazy::class.java)
            LOG_JFP.warn { "Failed to get tag type value for field $name of type ${clazz.simpleName}" }
        return false
    }
    val isValid = when {
        clazz.isAssignableFrom(content::class.java) -> true
        content is Boolean && clazz == Boolean::class.javaPrimitiveType -> true
        content is Byte && clazz == Byte::class.javaPrimitiveType -> true
        content is Short && clazz == Short::class.javaPrimitiveType -> true
        content is Char && clazz == Char::class.javaPrimitiveType -> true
        content is Int && clazz == Int::class.javaPrimitiveType -> true
        content is Long && clazz == Long::class.javaPrimitiveType -> true
        content is Float && clazz == Float::class.javaPrimitiveType -> true
        content is Double && clazz == Double::class.javaPrimitiveType -> true
        else -> false
    }
    if (!isValid) {
        LOG_JFP.error { "Invalid type for field $name, ${content::class.java.simpleName} is not assignable from ${clazz.simpleName}" }
    }
    return isValid
}

/** first element holds the default name  */
private fun getFieldNames(f: Field): List<String> {
    val annotation = f.getAnnotation(SerializedName::class.java)
    if (annotation == null) {
        val propertyAnnotation = f.getAnnotation(UProperty::class.java)
        if (propertyAnnotation != null && propertyAnnotation.name.isNotEmpty()) {
            return Collections.singletonList(propertyAnnotation.name)
        }
    }
    if (annotation == null) {
        /*val name: String = fieldNamingPolicy.translateName(f)
        return Collections.singletonList(name)*/
        return Collections.singletonList(f.name)
    }
    val serializedName = annotation.value
    val alternates = annotation.alternate
    if (alternates.isEmpty()) {
        return Collections.singletonList(serializedName)
    }
    val fieldNames = ArrayList<String>(alternates.size + 1)
    fieldNames.add(serializedName)
    for (alternate in alternates) {
        fieldNames.add(alternate)
    }
    return fieldNames
}

private fun getBoundFields(type: TypeToken<*>, raw: Class<*>): Map<String, Field> {
    var type = type
    var raw = raw
    val result = LinkedHashMap<String, Field>()
    if (raw.isInterface) {
        return result
    }
    val declaredType = type.type
    while (raw != Any::class.java) {
        val fields = raw.declaredFields
        for (field in fields) {
            if (field.declaringClass == UClassReal::class.java || field.declaringClass == UObject::class.java || field.declaringClass == UObject::class.java) {
                continue
            }
            ReflectionAccessor.getInstance().makeAccessible(field)
            // val fieldType: Type = `$Gson$Types`.resolve(type.type, raw, field.genericType)
            val fieldNames = getFieldNames(field)
            var previous: Field? = null
            var i = 0
            val size = fieldNames.size
            while (i < size) {
                val name = fieldNames[i]
                // if (i != 0) serialize = false // only serialize the default name
                val boundField = field
                val replaced = result.put(name, boundField)
                if (previous == null) previous = replaced
                ++i
            }
            if (previous != null) {
                throw IllegalArgumentException("$declaredType declares multiple JSON fields named ${previous.name}")
            }
        }
        type = TypeToken.get(`$Gson$Types`.resolve(type.type, raw, raw.genericSuperclass))
        raw = type.rawType
    }
    return result
}