package me.fungames.jfortniteparse.ue4.assets

import com.github.salomonbrys.kotson.jsonSerializer
import com.github.salomonbrys.kotson.registerTypeAdapter
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import me.fungames.jfortniteparse.fileprovider.FileProvider
import me.fungames.jfortniteparse.fort.exports.FortItemDefinition
import me.fungames.jfortniteparse.fort.exports.variants.FortCosmeticVariant
import me.fungames.jfortniteparse.ue4.assets.exports.UExport
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.asyncloading2.FNameMap
import me.fungames.jfortniteparse.ue4.asyncloading2.FPackageObjectIndex
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageId
import me.fungames.jfortniteparse.ue4.objects.uobject.FPackageIndex
import me.fungames.jfortniteparse.ue4.versions.Ue4Version
import me.fungames.jfortniteparse.valorant.exports.*
import java.math.BigInteger

abstract class Package(var fileName: String,
                       val provider: FileProvider? = null,
                       val game: Ue4Version = provider?.game ?: Ue4Version.GAME_UE4_LATEST) : UObject() {
    abstract val exportsLazy: List<Lazy<UExport>>
    open val exports: List<UExport>
        get() = exportsLazy.map { it.value }
    var packageFlags = 0

    protected fun constructExport(exportType: String) = when (exportType) {
        // Valorant specific classes
        "CharacterAbilityUIData" -> CharacterAbilityUIData()
        "BaseCharacterPrimaryDataAsset_C",
        "CharacterDataAsset" -> CharacterDataAsset()
        "CharacterRoleDataAsset" -> CharacterRoleDataAsset()
        "CharacterRoleUIData" -> CharacterRoleUIData()
        "CharacterUIData" -> CharacterUIData()
        // Fortnite specific classes
        "FortDailyRewardScheduleTokenDefinition" -> FortItemDefinition()
        else -> {
            val obj = ObjectTypeRegistry.constructClass(exportType)
            if (obj.javaClass != UObject::class.java) {
                obj
            } else if (exportType.contains("ItemDefinition")) {
                FortItemDefinition()
            } else if (exportType.startsWith("FortCosmetic") && exportType.endsWith("Variant")) {
                FortCosmeticVariant()
            } else {
                UObject()
            }
        }
    }

    /**
     * @return the first export of the given type
     * @throws IllegalArgumentException if there is no export of the given type
     */
    @Throws(IllegalArgumentException::class)
    inline fun <reified T : UExport> getExportOfType() = getExportsOfType<T>().first()

    /**
     * @return the first export of the given type or null if there is no
     */
    inline fun <reified T : UExport> getExportOfTypeOrNull() = getExportsOfType<T>().firstOrNull()

    /**
     * @return the all exports of the given type
     */
    inline fun <reified T : UExport> getExportsOfType() = exports.filterIsInstance<T>()

    inline fun <reified T> loadObject(index: FPackageIndex?) = index?.let { loadObjectGeneric(it) as? T }

    abstract fun loadObjectGeneric(index: FPackageIndex?): UExport?
    abstract fun findExport(objectName: String, className: String? = null): UExport?

    override fun toString() = fileName

    companion object {
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