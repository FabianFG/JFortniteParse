package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.CityHash.cityHash64
import java.util.*

class FPackageId {
    companion object {
        @JvmStatic
        private val INVALID_ID = 0uL.inv()

        @JvmStatic
        fun fromName(name: FName): FPackageId {
            val nameStr = name.toString().toLowerCase(Locale.ENGLISH)
            val nameBuf = nameStr.toByteArray(Charsets.UTF_16LE)
            val hash = cityHash64(nameBuf, 0, nameBuf.size).toULong()
            check(hash != INVALID_ID) { "Package name hash collision \"$nameStr\" and InvalidId" }
            return FPackageId(hash)
        }
    }

    private var id = INVALID_ID

    constructor()

    private constructor(id: ULong) {
        this.id = id
    }

    constructor(Ar: FArchive) {
        id = Ar.readUInt64()
    }

    fun isValid() = id != INVALID_ID

    fun value(): ULong {
        check(id != INVALID_ID)
        return id
    }

    fun valueForDebugging() = id

    operator fun compareTo(other: FPackageId) = id.compareTo(other.id)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FPackageId

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id.toInt()
}