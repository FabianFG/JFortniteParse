package me.fungames.jfortniteparse.ue4.assets.exports

import com.github.salomonbrys.kotson.jsonObject
import com.github.salomonbrys.kotson.set
import com.google.gson.Gson
import com.google.gson.JsonObject
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.JsonSerializer.toJson
import me.fungames.jfortniteparse.ue4.assets.Package
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.objects.IPropertyHolder
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.unprefix
import me.fungames.jfortniteparse.ue4.assets.util.mapToClass
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FObjectExport
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.deserializeUnversionedProperties

open class UObject : UClass, IPropertyHolder {
    var name = ""
    var outer: UObject? = null
    var clazz: UStruct? = null
    final override var properties: MutableList<FPropertyTag>
    var objectGuid: FGuid? = null
    var flags = 0

    var export: FObjectExport? = null
    val owner: Package?
        get() {
            var current = outer
            var next = current?.outer
            while (next != null) {
                current = next
                next = current.outer
            }
            return current as? Package
        }
    val exportType get() = clazz?.name ?: javaClass.simpleName.unprefix()

    @JvmOverloads
    constructor(properties: MutableList<FPropertyTag> = mutableListOf()) {
        this.properties = properties
    }

    inline fun <reified T> set(name: String, value: T) {
        if (getOrNull<T>(name) != null)
            properties.first { it.name.text == name }.setTagTypeValue(value)
    }

    inline fun <reified T> getOrDefault(name: String, default: T): T {
        val value: T? = getOrNull(name)
        return value ?: default
    }

    fun <T> getOrNull(name: String, clazz: Class<T>): T? = properties.firstOrNull { it.name.text == name }?.getTagTypeValue(clazz)

    inline fun <reified T> getOrNull(name: String) = getOrNull(name, T::class.java)

    inline fun <reified T> get(name: String): T = getOrNull(name) ?: throw KotlinNullPointerException("$name must be not-null")

    open fun deserialize(Ar: FAssetArchive, validPos: Int) {
        super.init(Ar)
        properties = mutableListOf()
        if (javaClass != UClassReal::class.java) {
            if (Ar.useUnversionedPropertySerialization) {
                deserializeUnversionedProperties(properties, clazz!!, Ar)
            } else {
                deserializeVersionedTaggedProperties(properties, Ar)
            }
        }
        //FLazyObjectPtr::PossiblySerializeObjectGuid
        if (Ar.pos() + 4 <= validPos && Ar.readBoolean() && Ar.pos() + 16 <= validPos)
            objectGuid = FGuid(Ar)
        super.complete(Ar)
        mapToClass(properties, javaClass, this)
    }

    open fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        serializeProperties(Ar, properties)
        Ar.writeBoolean(objectGuid != null)
        objectGuid?.serialize(Ar)
        super.completeWrite(Ar)
    }

    open fun toJson(context: Gson = Package.gson, locres: Locres? = null): JsonObject {
        val ob = jsonObject("exportType" to exportType)
        properties.forEach { pTag ->
            val tagValue = pTag.prop ?: return@forEach
            ob[pTag.name.text] = tagValue.toJson(context, locres)
        }
        return ob
    }

    fun clearFlags(newFlags: Int) {
        flags = flags and newFlags.inv()
    }

    /**
     * Used to safely check whether any of the passed in flags are set.
     *
     * @param flagsToCheck    Object flags to check for.
     * @return                true if any of the passed in flags are set, false otherwise  (including no flags passed in).
     */
    fun hasAnyFlags(flagsToCheck: Int): Boolean {
        return (flags and flagsToCheck) != 0
    }

    /**
     * Returns the fully qualified pathname for this object as well as the name of the class, in the format:
     * 'ClassName Outermost.[Outer:]Name'.
     *
     * @param   stopOuter   if specified, indicates that the output string should be relative to this object.  if StopOuter
     *                      does not exist in this object's Outer chain, the result would be the same as passing NULL.
     */
    fun getFullName(stopOuter: UObject?, includeClassPackage: Boolean = false): String {
        val result = StringBuilder(128)
        getFullName(stopOuter, result, includeClassPackage)
        return result.toString()
    }

    fun getFullName(stopOuter: UObject?, resultString: StringBuilder, includeClassPackage: Boolean = false) {
        if (includeClassPackage) {
            resultString.append(clazz!!.getPathName())
        } else {
            resultString.append(clazz!!.name)
        }
        resultString.append(' ')
        getPathName(stopOuter, resultString)
    }

    /**
     * Returns the fully qualified pathname for this object, in the format:
     * 'Outermost[.Outer].Name'
     *
     * @param   stopOuter   if specified, indicates that the output string should be relative to this object.  if stopOuter
     *                      does not exist in this object's outer chain, the result would be the same as passing null.
     */
    @JvmOverloads
    fun getPathName(stopOuter: UObject? = null): String {
        val result = StringBuilder()
        getPathName(stopOuter, result)
        return result.toString()
    }

    /**
     * Versions of getPathName() that eliminates unnecessary copies and allocations.
     */
    fun getPathName(stopOuter: UObject?, resultString: StringBuilder) {
        if (this != stopOuter) {
            val objOuter = outer
            if (objOuter != null && objOuter != stopOuter) {
                objOuter.getPathName(stopOuter, resultString)

                // SUBOBJECT_DELIMITER_CHAR is used to indicate that this object's outer is not a UPackage
                if (objOuter.outer is Package) {
                    resultString.append(':')
                } else {
                    resultString.append('.')
                }
            }
            resultString.append(name)
        } else {
            resultString.append("None")
        }
    }

    override fun toString() = name
}

fun deserializeVersionedTaggedProperties(properties: MutableList<FPropertyTag>, Ar: FAssetArchive) {
    while (true) {
        val tag = FPropertyTag(Ar, true)
        if (tag.name.isNone())
            break
        properties.add(tag)
    }
}

fun serializeProperties(Ar: FAssetArchiveWriter, properties: List<FPropertyTag>) {
    properties.forEach { it.serialize(Ar, true) }
    Ar.writeFName(FName.getByNameMap("None", Ar.nameMap) ?: throw ParserException("NameMap must contain \"None\""))
}