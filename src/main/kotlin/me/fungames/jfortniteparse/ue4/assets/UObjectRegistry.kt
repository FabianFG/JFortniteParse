package me.fungames.jfortniteparse.ue4.assets

import me.fungames.jfortniteparse.fort.exports.AthenaBackpackItemDefinition
import me.fungames.jfortniteparse.fort.exports.AthenaCharacterItemDefinition
import me.fungames.jfortniteparse.fort.exports.AthenaEmojiItemDefinition
import me.fungames.jfortniteparse.fort.exports.AthenaPickaxeItemDefinition
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import java.util.concurrent.ConcurrentHashMap

object UObjectRegistry {
    private val registry = ConcurrentHashMap<String, Class<out UObject>>()

    init {
        register(AthenaBackpackItemDefinition::class.java)
        register(AthenaCharacterItemDefinition::class.java)
        register(AthenaEmojiItemDefinition::class.java)
        register(AthenaPickaxeItemDefinition::class.java)
        register(UTexture2D::class.java)
    }

    fun register(clazz: Class<out UObject>) {
        var name = clazz.simpleName
        if (name[0] == 'U' && name[1].isUpperCase()) {
            name = name.substring(1)
        }
        register(name, clazz)
    }

    fun register(objectName: String, clazz: Class<out UObject>) {
        registry[objectName] = clazz
    }

    fun constructObject(objectName: String): UObject {
        val clazz = registry[objectName] ?: UObject::class.java
        return clazz.newInstance()
    }
}

fun String.unprefix(): String {
    if ((get(0) == 'U' || get(0) == 'F' || get(0) == 'A') && get(1).isUpperCase()) {
        return substring(1)
    }
    return this
}