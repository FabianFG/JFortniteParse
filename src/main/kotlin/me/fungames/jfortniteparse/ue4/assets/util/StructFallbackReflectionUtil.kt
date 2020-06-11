@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.ue4.assets.util

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import org.objenesis.ObjenesisStd

inline fun <reified T> FStructFallback.mapToClass(Ar: FAssetArchive? = null) = mapToClass(T::class.java, Ar) as T

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class StructFieldName(val name : String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class StructFallbackClass

fun FStructFallback.mapToClass(clazz: Class<*>, Ar : FAssetArchive? = null): Any {
    val result = runCatching { ObjenesisStd().newInstance(clazz) }.getOrElse { throw ParserException("Failed to allocate instance of object", it) }
    val props = this.properties.associateBy { it.name.text }
    runCatching { clazz.declaredFields }.getOrElse { throw ParserException("Failed to get declared fields of class", it) }.forEach { field ->
        runCatching { field.isAccessible = true }.onFailure { throw ParserException("Failed to set field accessible", it) }
        val name = runCatching { if (field.isAnnotationPresent(StructFieldName::class.java))
            field.getAnnotation(StructFieldName::class.java).name
        else
            field.name }.getOrElse { throw ParserException("Failed to get name of fields", it) }
        val prop = props[name]
        if (prop != null) {
            val fieldType = field.type
            val content = prop.getTagTypeValue(fieldType, Ar)
            if (content == null)
                UClass.logger.warn { "Failed to get tag type value for field $name of type ${fieldType.simpleName}" }
            else {
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
                    runCatching { field.set(result, content) }.onFailure { throw ParserException("Failed to set content of field", it) }
                } else {
                    UClass.logger.error { "StuctFallbackClass has invalid type for field $name, ${content::class.java.simpleName} is not assignable from ${field.type.simpleName}" }
                }
            }
        }
    }
    return result
}

