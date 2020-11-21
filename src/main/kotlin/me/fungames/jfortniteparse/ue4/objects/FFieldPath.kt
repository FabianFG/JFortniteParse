package me.fungames.jfortniteparse.ue4.objects

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FFieldPath {
    val names: Array<String>

    @JvmOverloads
    constructor(names: Array<String> = emptyArray()) {
        this.names = names
    }

    constructor(Ar: FArchive) : this(Ar.readTArray { Ar.readString() })
}