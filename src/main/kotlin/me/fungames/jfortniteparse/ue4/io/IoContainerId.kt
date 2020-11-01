package me.fungames.jfortniteparse.ue4.io

import me.fungames.jfortniteparse.ue4.reader.FArchive

class FIoContainerId {
    companion object {
        @JvmStatic
        private val INVALID_ID = (-1).toULong()
    }

    private var id = INVALID_ID

    constructor()

    constructor(id: ULong) {
        this.id = id
    }

    constructor(Ar: FArchive) {
        id = Ar.readUInt64()
    }

    fun value() = id

    fun isValid() = id != INVALID_ID

    operator fun compareTo(other: FIoContainerId) = id.compareTo(other.id)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FIoContainerId

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id.toInt()
}