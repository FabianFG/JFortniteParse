package me.fungames.jfortniteparse.ue4.objects.gameplaytags

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.objects.coreuobject.uobject.FName

@ExperimentalUnsignedTypes
class FGameplayTagContainer : UClass {
    var gameplayTags: MutableList<FName>

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        val length = Ar.readUInt32()
        gameplayTags = mutableListOf()
        for (i in 0u until length) {
            gameplayTags.add(Ar.readFName())
        }
        super.complete(Ar)
    }

    fun getValue(category: String) = gameplayTags.firstOrNull { it.text.startsWith(category) }

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(gameplayTags.size.toUInt())
        gameplayTags.forEach {
            Ar.writeFName(it)
        }
        super.completeWrite(Ar)
    }

    constructor(gameplayTags: MutableList<FName>) {
        this.gameplayTags = gameplayTags
    }
}