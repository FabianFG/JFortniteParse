package me.fungames.jfortniteparse.ue4.objects.gameplaytags

import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FGameplayTagContainer : Iterable<FName> {
    var gameplayTags: MutableList<FName>

    constructor(Ar: FArchive) {
        val length = Ar.readUInt32()
        gameplayTags = mutableListOf()
        for (i in 0u until length) {
            gameplayTags.add(Ar.readFName())
        }
    }

    fun getValue(parent: String) = gameplayTags.firstOrNull { it.text.startsWith(parent, true) }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt32(gameplayTags.size.toUInt())
        gameplayTags.forEach { Ar.writeFName(it) }
    }

    constructor() : this(mutableListOf())

    constructor(gameplayTags: MutableList<FName>) {
        this.gameplayTags = gameplayTags
    }

    override fun iterator() = gameplayTags.iterator()
}