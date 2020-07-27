package me.fungames.jfortniteparse.ue4.objects.engine

import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FPerPlatformInt : UClass {
    var cooked: Boolean
    var value: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        cooked = Ar.readBoolean()
        value = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeBoolean(cooked)
        Ar.writeInt32(value)
        super.completeWrite(Ar)
    }

    constructor(cooked: Boolean, value: Int) {
        this.cooked = cooked
        this.value = value
    }
}

@ExperimentalUnsignedTypes
class FPerPlatformFloat : UClass {
    var cooked: Boolean
    var value: Float

    constructor(Ar: FArchive) {
        super.init(Ar)
        cooked = Ar.readBoolean()
        value = Ar.readFloat32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeBoolean(cooked)
        Ar.writeFloat32(value)
        super.completeWrite(Ar)
    }

    constructor(cooked: Boolean, value: Float) {
        this.cooked = cooked
        this.value = value
    }
}

@ExperimentalUnsignedTypes
class FPerPlatformBool : UClass {
    var cooked: Boolean
    var value: Boolean

    constructor(Ar: FArchive) {
        super.init(Ar)
        cooked = Ar.readBoolean()
        value = Ar.readBoolean()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeBoolean(cooked)
        Ar.writeBoolean(value)
        super.completeWrite(Ar)
    }

    constructor(cooked: Boolean, value: Boolean) {
        this.cooked = cooked
        this.value = value
    }
}