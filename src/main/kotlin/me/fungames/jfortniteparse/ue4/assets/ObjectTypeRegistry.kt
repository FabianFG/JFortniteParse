package me.fungames.jfortniteparse.ue4.assets

import me.fungames.jfortniteparse.fort.exports.*
import me.fungames.jfortniteparse.fort.objects.FortPhoenixLevelRewardData
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.exports.*
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture
import me.fungames.jfortniteparse.ue4.assets.exports.tex.UTexture2D
import java.util.concurrent.ConcurrentHashMap

object ObjectTypeRegistry {
    val classes = ConcurrentHashMap<String, Class<out UObject>>()
    val structs = ConcurrentHashMap<String, Class<*>>()

    init {
        registerEngine()
        registerFortnite()
    }

    private inline fun registerEngine() {
        registerClass(UCurveTable::class.java)
        registerClass(UDataAsset::class.java)
        registerClass(UDataTable::class.java)
        registerClass(ULevel::class.java)
        registerClass(UPaperSprite::class.java)
        registerClass(UPrimaryDataAsset::class.java)
        registerClass(USoundWave::class.java)
        registerClass(UStaticMesh::class.java)
        registerClass(UStreamableRenderAsset::class.java)
        registerClass(UStringTable::class.java)
        registerClass(UTexture2D::class.java)
        registerClass(UTexture::class.java)
    }

    private inline fun registerFortnite() {
        registerClass(AthenaBackpackItemDefinition::class.java)
        registerClass(AthenaCharacterItemDefinition::class.java)
        registerClass(AthenaCharacterPartItemDefinition::class.java)
        registerClass(AthenaCosmeticItemDefinition::class.java)
        registerClass(AthenaDailyQuestDefinition::class.java)
        registerClass(AthenaEmojiItemDefinition::class.java)
        registerClass(AthenaPickaxeItemDefinition::class.java)
        registerClass(FortAccountItemDefinition::class.java)
        registerClass(FortCatalogMessaging::class.java)
        registerClass(FortCosmeticParticleVariant::class.java)
        registerClass(FortCosmeticVariant::class.java)
        registerClass(FortItemCategory::class.java)
        registerClass(FortItemDefinition::class.java)
        registerClass(FortItemSeriesDefinition::class.java)
        registerClass(FortMtxOfferData::class.java)
        registerClass(FortPersistableItemDefinition::class.java)
        registerClass(FortQuestItemDefinition::class.java)
        registerClass(McpItemDefinitionBase::class.java)

        registerStruct(FortPhoenixLevelRewardData::class.java)
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

    fun registerStruct(clazz: Class<*>) {
        var name = clazz.simpleName
        if (name[0] == 'F' && name[1].isUpperCase()) {
            name = name.substring(1)
        }
        registerStruct(name, clazz)
    }

    fun registerStruct(serializedName: String, clazz: Class<*>) {
        structs[serializedName] = clazz
    }

    fun constructClass(serializedName: String): UObject {
        if (serializedName.startsWith("/Script/") || serializedName.startsWith("Default__")) {
            return UObject().apply { exportType = serializedName }
        }
        var clazz = classes[serializedName]
        if (clazz == null) {
            UClass.logger.warn("Didn't find class $serializedName in registry")
            clazz = UObject::class.java
        }
        return clazz.newInstance().apply {
            readGuid = true
            exportType = serializedName
        }
    }

    fun constructStruct(serializedName: String): Any {
        if (serializedName.startsWith("/Script/") || serializedName.startsWith("Default__")) {
            return Object()
        }
        val clazz = structs[serializedName]
        if (clazz == null) {
            UClass.logger.warn("Didn't find class $serializedName in registry")
            return Object()
        }
        return clazz.newInstance()
    }
}

fun String.unprefix(): String {
    if ((get(0) == 'U' || get(0) == 'F' || get(0) == 'A') && get(1).isUpperCase()) {
        return substring(1)
    }
    return this
}