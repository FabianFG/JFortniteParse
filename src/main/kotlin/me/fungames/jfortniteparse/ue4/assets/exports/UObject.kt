package me.fungames.jfortniteparse.ue4.assets.exports

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.assets.objects.FObjectExport
import me.fungames.jfortniteparse.ue4.assets.objects.FPropertyTag
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter

@ExperimentalUnsignedTypes
open class UObject : UExport {
    override var baseObject = this
    var properties : MutableList<FPropertyTag>
    var objectGuid : FGuid? = null
    var readGuid = false

    constructor(Ar: FAssetArchive, exportType: String, readGuid: Boolean = true) : super(exportType) {
        properties =
            deserializeProperties(Ar)
        if (readGuid && Ar.readBoolean() && Ar.pos() + 16 <= Ar.size())
            objectGuid = FGuid(Ar)
    }

    constructor(Ar: FAssetArchive, exportObject : FObjectExport, readGuid : Boolean = true) : super(exportObject) {
        properties =
            deserializeProperties(Ar)
        if (readGuid && Ar.readBoolean() && Ar.pos() + 16 <= Ar.size())
            objectGuid = FGuid(Ar)
    }

    inline fun <reified T> set(name: String, value : T) {
        if(getOrNull<T>(name) != null)
            properties.first { it.name.text == name }.setTagTypeValue(value)
    }

    inline fun <reified T> getOrDefault(name : String, default : T, Ar: FAssetArchive? = null) : T {
        val value : T? = getOrNull(name, Ar)
        return value ?: default
    }

    inline fun <reified T> getOrNull(name : String, Ar: FAssetArchive? = null) = properties.firstOrNull { it.name.text == name }?.getTagTypeValue<T>(Ar)

    inline fun <reified T> get(name: String, Ar: FAssetArchive? = null) : T = getOrNull(name, Ar) ?: throw KotlinNullPointerException("$name must be not-null")

    override fun serialize(Ar: FAssetArchiveWriter) {
        serializeProperties(
            Ar,
            properties
        )
        if (readGuid) {
            Ar.writeBoolean(objectGuid != null)
            if (objectGuid != null) objectGuid?.serialize(Ar)
        }
    }

    companion object {
        fun serializeProperties(Ar: FAssetArchiveWriter, properties: List<FPropertyTag>) {
            properties.forEach {
                it.serialize(Ar, true)
            }
            Ar.writeFName(FName.getByNameMap("None", Ar.nameMap) ?: throw ParserException("NameMap must contain \"None\""))
        }

        fun deserializeProperties(Ar : FAssetArchive) : MutableList<FPropertyTag> {
            val properties = mutableListOf<FPropertyTag>()
            while (true) {
                val tag = FPropertyTag(Ar, true)
                if (tag.name.text == "None")
                    break
                properties.add(tag)
            }
            return properties
        }
    }

    constructor(properties : MutableList<FPropertyTag>, objectGuid : FGuid?, exportType: String) : super(exportType) {
        this.properties = properties
        this.objectGuid = objectGuid
    }
}