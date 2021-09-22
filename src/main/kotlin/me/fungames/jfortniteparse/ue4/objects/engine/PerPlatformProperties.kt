package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

class FPerPlatformInt {
    var cooked: Boolean
    var value: Int

    constructor(Ar: FArchive) {
        cooked = Ar.readBoolean()
        value = Ar.readInt32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeBoolean(cooked)
        Ar.writeInt32(value)
    }

    constructor(cooked: Boolean, value: Int) {
        this.cooked = cooked
        this.value = value
    }
}

class FPerPlatformFloat {
    var cooked: Boolean
    var value: Float

    constructor(Ar: FArchive) {
        cooked = Ar.readBoolean()
        value = Ar.readFloat32()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeBoolean(cooked)
        Ar.writeFloat32(value)
    }

    constructor(cooked: Boolean, value: Float) {
        this.cooked = cooked
        this.value = value
    }
}

class FPerPlatformBool {
    var cooked: Boolean
    var value: Boolean

    constructor(Ar: FArchive) {
        cooked = Ar.readBoolean()
        value = Ar.readBoolean()
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeBoolean(cooked)
        Ar.writeBoolean(value)
    }

    constructor(cooked: Boolean, value: Boolean) {
        this.cooked = cooked
        this.value = value
    }
}