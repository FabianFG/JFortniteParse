package me.fungames.jfortniteparse.ue4.objects.gameplaytags

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FGameplayTagContainer : UClass, Iterable<FName> {
    var gameplayTags: MutableList<FName>

    constructor(Ar: FArchive) {
        super.init(Ar)
        val length = Ar.readUInt32()
        gameplayTags = mutableListOf()
        for (i in 0u until length) {
            gameplayTags.add(Ar.readFName())
        }
        super.complete(Ar)
    }

    fun getValue(parent: String) = gameplayTags.firstOrNull { it.text.startsWith(parent, true) }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(gameplayTags.size.toUInt())
        gameplayTags.forEach { Ar.writeFName(it) }
        super.completeWrite(Ar)
    }

    constructor() : this(mutableListOf())

    constructor(gameplayTags: MutableList<FName>) {
        this.gameplayTags = gameplayTags
    }

    override fun iterator() = gameplayTags.iterator()
}