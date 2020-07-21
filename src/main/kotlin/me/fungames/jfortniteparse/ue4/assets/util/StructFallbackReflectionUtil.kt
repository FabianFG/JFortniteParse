@file:Suppress("EXPERIMENTAL_API_USAGE")

package me.fungames.jfortniteparse.ue4.assets.util

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.objects.FStructFallback
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import org.objenesis.ObjenesisStd

inline fun <reified T> FStructFallback.mapToClass(Ar: FAssetArchive? = null) = mapToClass(properties, T::class.java, Ar)
inline fun <reified T> UObject.mapToClass(Ar: FAssetArchive? = null) = mapToClass(properties, T::class.java, Ar)
fun <T> FStructFallback.mapToClass(clazz: Class<T>, Ar : FAssetArchive? = null): T = mapToClass(properties, clazz, Ar)
fun <T> UObject.mapToClass(clazz: Class<T>, Ar : FAssetArchive? = null): T = mapToClass(properties, clazz, Ar)

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class StructFieldName(val name : String)

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class StructFallbackClass

fun <T> mapToClass(properties: List<FPropertyTag>, clazz: Class<T>, Ar : FAssetArchive? = null): T {
    try {
        val result = ObjenesisStd().newInstance(clazz)
        val props = properties.associateBy { it.name.text }
        for (field in clazz.declaredFields) {
            field.isAccessible = true
            val name =
                    if (field.isAnnotationPresent(StructFieldName::class.java))
                        field.getAnnotation(StructFieldName::class.java).name
                    else
                        field.name
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
                        field.set(result, content)
                    } else {
                        UClass.logger.error { "StructFallbackClass has invalid type for field $name, ${content::class.java.simpleName} is not assignable from ${field.type.simpleName}" }
                    }
                }
            }
        }
        return result
    } catch (e: ReflectiveOperationException) {
        throw ParserException("Object mapping failure", e)
    }
}

