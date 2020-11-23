package me.fungames.jfortniteparse.ue4.assets.util

import com.google.gson.annotations.SerializedName
import com.google.gson.internal.`$Gson$Types`
import com.google.gson.internal.reflect.ReflectionAccessor
import com.google.gson.reflect.TypeToken
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.objects.IPropertyHolder
import org.objenesis.ObjenesisStd
import java.lang.reflect.Field
import java.util.*

inline fun <reified T> IPropertyHolder.mapToClass() = mapToClass(properties, T::class.java)
inline fun <T> IPropertyHolder.mapToClass(clazz: Class<T>): T = mapToClass(properties, clazz)

inline fun <T> mapToClass(properties: List<FPropertyTag>, clazz: Class<T>): T = mapToClass(properties, clazz, ObjenesisStd().newInstance(clazz))
fun <T> mapToClass(properties: List<FPropertyTag>, clazz: Class<T>, obj: T): T {
    try {
        val boundFields = getBoundFields(TypeToken.get(clazz), clazz)
        for (prop in properties) {
            val field = boundFields[prop.name.text] ?: continue
            val fieldType = field.type
            val content = prop.getTagTypeValue(fieldType, field.genericType)
            if (content == null) {
                UClass.logger.warn { "Failed to get tag type value for field ${prop.name} of type ${fieldType.simpleName}" }
            } else {
                val isValid = when {
                    fieldType.isAssignableFrom(content::class.java) -> true
                    content is Boolean && fieldType == Boolean::class.javaPrimitiveType -> true
                    content is Byte && fieldType == Byte::class.javaPrimitiveType -> true
                    content is Short && fieldType == Short::class.javaPrimitiveType -> true
                    content is Char && fieldType == Char::class.javaPrimitiveType -> true
                    content is Int && fieldType == Int::class.javaPrimitiveType -> true
                    content is Long && fieldType == Long::class.javaPrimitiveType -> true
                    content is Float && fieldType == Float::class.javaPrimitiveType -> true
                    content is Double && fieldType == Double::class.javaPrimitiveType -> true
                    else -> false
                }
                if (isValid) {
                    field.set(obj, content)
                } else {
                    UClass.logger.error { "StructFallbackClass has invalid type for field ${prop.name}, ${content::class.java.simpleName} is not assignable from ${field.type.simpleName}" }
                }
            }
        }
        return obj
    } catch (e: ReflectiveOperationException) {
        throw ParserException("Object mapping failure", e)
    }
}

/** first element holds the default name  */
private fun getFieldNames(f: Field): List<String> {
    val annotation = f.getAnnotation(SerializedName::class.java)
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
            if (field.declaringClass == UClass::class.java || field.declaringClass == UExport::class.java || field.declaringClass == UObject::class.java) {
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