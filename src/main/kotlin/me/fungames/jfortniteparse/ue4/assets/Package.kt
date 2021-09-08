package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.jsonSerializer
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.assets.exports.UStruct
import me.fungames.jfortniteparse.ue4.asyncloading2.FPackageObjectIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.serialization.FNameMap
import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import java.math.BigInteger

abstract class Package(var fileName: String,
                       val provider: FileProvider? = null,
                       val versions: VersionContainer = provider?.versions ?: VersionContainer.DEFAULT) : UObject() {
    abstract val exportsLazy: List<Lazy<UObject>>
    open val exports: List<UObject>
        get() = exportsLazy.map { it.value }
    var packageFlags = 0

    /**
     * @return the first export of the given type
     * @throws IllegalArgumentException if there is no export of the given type
     */
    @Throws(IllegalArgumentException::class)
    inline fun <reified T : UObject> getExportOfType() = getExportsOfType<T>().first()

    /**
     * @return the first export of the given type or null if there is no
     */
    inline fun <reified T : UObject> getExportOfTypeOrNull() = getExportsOfType<T>().firstOrNull()

    /**
     * @return the all exports of the given type
     */
    inline fun <reified T : UObject> getExportsOfType() = exports.filterIsInstance<T>()

    abstract fun <T : UObject> findObject(index: FPackageIndex?): Lazy<T>?
    fun <T : UObject> loadObject(index: FPackageIndex?) = findObject<T>(index)?.value
    abstract fun findObjectByName(objectName: String, className: String? = null): Lazy<UObject>?
    abstract fun findObjectMinimal(index: FPackageIndex?): ResolvedObject?

    companion object {
        fun constructExport(struct: UStruct?): UObject {
            var current = struct
            while (current != null) {
                (current as? UScriptStruct)?.structClass?.let {
                    return (it.newInstance() as UObject).apply { clazz = struct }
                }
                current = current.superStruct?.value
            }
            return UObject().apply { clazz = struct }
        }

        val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(jsonSerializer<UByte> { JsonPrimitive(it.src.toShort()) })
            .registerTypeAdapter(jsonSerializer<UShort> { JsonPrimitive(it.src.toInt()) })
            .registerTypeAdapter(jsonSerializer<UInt> { JsonPrimitive(it.src.toLong()) })
            .registerTypeAdapter(jsonSerializer<ULong> { JsonPrimitive(BigInteger(it.src.toString())) })
            .registerTypeAdapter(JsonSerializer.importSerializer)
            .registerTypeAdapter(JsonSerializer.exportSerializer)
            .registerTypeAdapter(jsonSerializer<FMinimalName> { JsonPrimitive(it.src.toName().text) })
            .registerTypeAdapter(jsonSerializer<FNameMap> { it.context.serialize(it.src.nameEntries) })
            .registerTypeAdapter(jsonSerializer<FPackageId> { it.context.serialize(it.src.value()) })
            .registerTypeAdapter(jsonSerializer<FPackageObjectIndex> {
                JsonObject().apply {
                    addProperty("type", it.src.type().name)
                    add("value", it.context.serialize(it.src.value()))
                }
            })
            .create()
    }
}

abstract class ResolvedObject(private val _pkg: Package?, val exportIndex: Int = -1) {
    val pkg get() = _pkg!!

    abstract fun getName(): FName
    open fun getOuter(): ResolvedObject? = null
    open fun getClazz(): ResolvedObject? = null
    open fun getSuper(): ResolvedObject? = null
    open fun getObject(): Lazy<UObject?>? = null

    @JvmOverloads
    fun getFullName(includePackageName: Boolean = true, includeClassPackage: Boolean = false): String {
        val result = StringBuilder(128)
        getFullName(includePackageName, result, includeClassPackage)
        return result.toString()
    }

    fun getFullName(includePackageName: Boolean, resultString: StringBuilder, includeClassPackage: Boolean = false) {
        if (includeClassPackage) {
            resultString.append(getClazz()?.getPathName())
        } else {
            resultString.append(getClazz()?.getName())
        }
        resultString.append(' ')
        getPathName(includePackageName, resultString)
    }

    @JvmOverloads
    fun getPathName(includePackageName: Boolean = true): String {
        val result = StringBuilder()
        getPathName(includePackageName, result)
        return result.toString()
    }

    fun getPathName(includePackageName: Boolean, resultString: StringBuilder) {
        val objOuter = getOuter()
        if (objOuter != null) {
            val objOuterOuter = objOuter.getOuter()
            if (objOuterOuter != null || includePackageName) {
                objOuter.getPathName(includePackageName, resultString)
                // SUBOBJECT_DELIMITER_CHAR is used to indicate that this object's outer is not a UPackage
                resultString.append(if (objOuterOuter != null && objOuterOuter.getOuter() == null) ':' else '.')
            }
        }
        resultString.append(getName())
    }

    override fun toString() = getFullName()
}

class ResolvedLoadedObject(val obj: UObject) : ResolvedObject(obj as? Package ?: obj.owner) {
    override fun getName() = FName(obj.name)
    override fun getOuter() = obj.outer?.let { ResolvedLoadedObject(it) }
    override fun getClazz() = obj.clazz?.let { ResolvedLoadedObject(it) }
    override fun getObject() = lazy { obj }
}