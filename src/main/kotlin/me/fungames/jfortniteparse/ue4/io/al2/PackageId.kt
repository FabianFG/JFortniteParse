package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.reader.FArchive

// TODO move to uobject
@ExperimentalUnsignedTypes
class FPackageId {
    companion object {
        @JvmStatic
        val INVALID_ID = 0u.inv()

        @JvmStatic
        fun fromIndex(index: UInt) = FPackageId(index)
    }

    private var id = INVALID_ID

    constructor()

    private constructor(id: UInt) {
        this.id = id
    }

    constructor(Ar: FArchive) {
        id = Ar.readUInt32()
    }

    fun isValid() = id != INVALID_ID

    fun toIndex(): UInt {
        check(id != INVALID_ID)
        return id
    }

    fun toIndexForDebugging() = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FPackageId

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id.toInt()
}