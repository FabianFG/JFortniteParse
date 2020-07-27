package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.uobject.FName

@ExperimentalUnsignedTypes
class FStructFallback : UClass {
    var properties : MutableList<FPropertyTag>

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        properties = mutableListOf()
        while (true) {
            val tag = FPropertyTag(Ar, true)
            if (tag.name.text == "None")
                break
            properties.add(tag)
        }
        super.complete(Ar)
    }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        properties.forEach {
            it.serialize(Ar, true)
        }
        Ar.writeFName(FName.getByNameMap("None", Ar.nameMap) ?: throw ParserException("NameMap must contain \"None\""))
        super.completeWrite(Ar)
    }

    inline fun <reified T> set(name: String, value : T) {
        if(getOrNull<T>(name) != null)
            properties.first { it.name.text == name }.setTagTypeValue(value)
    }

    inline fun <reified T> getOrDefault(name : String, default : T) : T {
        val value : T? = getOrNull(name)
        return value ?: default
    }

    inline fun <reified T> getOrNull(name : String) : T? {
        val value = properties.firstOrNull { it.name.text == name }?.getTagTypeValueLegacy()
        return if (value is T)
            value
        else
            null
    }

    inline fun <reified T> get(name: String) : T = getOrNull(name) ?: throw KotlinNullPointerException("$name must be not-null")

    constructor(properties: MutableList<FPropertyTag>) {
        this.properties = properties
    }


}