package me.fungames.jfortniteparse.ue4.assets

import me.fungames.jfortniteparse.fort.exports.*
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.UObject
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import java.util.concurrent.ConcurrentHashMap

object UObjectRegistry {
    private val classes = ConcurrentHashMap<String, Class<out UObject>>()

    init {
        registerClass(AthenaBackpackItemDefinition::class.java)
        registerClass(AthenaCharacterItemDefinition::class.java)
        registerClass(AthenaEmojiItemDefinition::class.java)
        registerClass(AthenaPickaxeItemDefinition::class.java)
        registerClass(FortCosmeticParticleVariant::class.java)
        registerClass(FortQuestItemDefinition::class.java)
        registerClass(UTexture2D::class.java)
    }

    fun registerClass(clazz: Class<out UObject>) {
        var name = clazz.simpleName
        if (name[0] == 'U' && name[1].isUpperCase()) {
            name = name.substring(1)
        }
        registerClass(name, clazz)
    }

    fun registerClass(serializedName: String, clazz: Class<out UObject>) {
        classes[serializedName] = clazz
    }

    fun constructClass(serializedName: String): UObject {
        var clazz = classes[serializedName]
        if (clazz == null) {
            UClass.logger.warn("Didn't find class $serializedName in registry")
            clazz = UObject::class.java
        }
        return clazz.newInstance().apply { readGuid = true }
    }
}

fun String.unprefix(): String {
    if ((get(0) == 'U' || get(0) == 'F' || get(0) == 'A') && get(1).isUpperCase()) {
        return substring(1)
    }
    return this
}