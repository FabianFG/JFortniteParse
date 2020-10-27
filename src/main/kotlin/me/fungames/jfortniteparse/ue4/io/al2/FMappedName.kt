package me.fungames.jfortniteparse.ue4.io.al2

import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FMappedName {
    companion object {
        private val INVALID_INDEX = 0u.inv()
        private val INDEX_BITS = 30u
        private val INDEX_MASK = (1u shl INDEX_BITS.toInt()) - 1u
        private val TYPE_MASK = INDEX_MASK.inv()
        private val TYPE_SHIFT = INDEX_BITS

        @JvmStatic
        fun create(index: UInt, number: UInt, type: EType): FMappedName {
            check(index <= Int.MAX_VALUE.toUInt())
            return FMappedName((type.ordinal.toUInt() shl TYPE_SHIFT.toInt()) or index, number)
        }

        @JvmStatic
        fun fromMinimalName(minimalName: FMinimalName) =
            FMappedName(minimalName.index.value, minimalName.number.toUInt())

        @JvmStatic
        inline fun isResolvedToMinimalName(minimalName: FMinimalName): Boolean {
            // Not completely safe, relies on that no FName will have its Index and Number equal to Max_uint32
            val mappedName = fromMinimalName(minimalName)
            return mappedName.isValid()
        }

        /*@JvmStatic
        inline fun safeMinimalNameToName(minimalName: FMinimalName): FName {
            return if (isResolvedToMinimalName(minimalName)) minimalNameToName(minimalName) else NAME_None;
        }*/
    }

    enum class EType {
        Package,
        Container,
        Global
    }

    private val index: UInt
    val number: UInt

    private constructor(index: UInt = INVALID_INDEX, number: UInt = INVALID_INDEX) {
        this.index = index
        this.number = number
    }

    constructor(Ar: FArchive) : this(Ar.readUInt32(), Ar.readUInt32())

    fun toUnresolvedMinimalName() = FMinimalName(FNameEntryId(index), number.toInt())

    fun isValid() = index != INVALID_INDEX && number != INVALID_INDEX

    fun getType() = EType.values()[((index and TYPE_MASK) shr TYPE_SHIFT.toInt()).toInt()]

    fun isGlobal() = ((index and TYPE_MASK) shr TYPE_SHIFT.toInt()) != 0u

    fun getIndex() = index and INDEX_MASK

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FMappedName

        if (index != other.index) return false
        if (number != other.number) return false

        return true
    }

    override fun hashCode(): Int {
        var result = index.hashCode()
        result = 31 * result + number.hashCode()
        return result
    }
}