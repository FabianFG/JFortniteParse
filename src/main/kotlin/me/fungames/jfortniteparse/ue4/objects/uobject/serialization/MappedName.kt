package me.fungames.jfortniteparse.ue4.objects.uobject.serialization

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.uobject.FMinimalName
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.objects.uobject.FNameEntryId
import me.fungames.jfortniteparse.ue4.objects.uobject.loadNameBatch
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import me.fungames.jfortniteparse.util.get

class FMappedName {
    companion object {
        private val INVALID_INDEX = 0u.inv()
        private val INDEX_BITS = 30u
        private val INDEX_MASK = (1u shl INDEX_BITS.toInt()) - 1u
        private val TYPE_MASK = INDEX_MASK.inv()
        private val TYPE_SHIFT = INDEX_BITS

        @JvmStatic
        fun create(index: UInt, number: UInt, type: EType, Ar: FArchive? = null): FMappedName {
            if (index > Int.MAX_VALUE.toUInt()) {
                if (Ar != null) throw ParserException("Bad name index", Ar)
                else throw ParserException("Bad name index")
            }
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

    //fun toUnresolvedMinimalName() = FMinimalName(index, number.toInt())

    fun isValid() = index != INVALID_INDEX && number != INVALID_INDEX

    fun getType() = EType.values()[(index and TYPE_MASK) shr TYPE_SHIFT.toInt()]

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

class FNameMap {
    internal var nameEntries = emptyList<String>()
    private var nameMapType = FMappedName.EType.Global

    fun size() = nameEntries.size

    fun load(Ar: FArchive, nameMapType: FMappedName.EType) {
        nameEntries = loadNameBatch(Ar)
        this.nameMapType = nameMapType
    }

    fun load(nameBuffer: ByteArray, hashBuffer: ByteArray, nameMapType: FMappedName.EType) {
        nameEntries = loadNameBatch(FByteArchive(nameBuffer), FByteArchive(hashBuffer))
        this.nameMapType = nameMapType
    }

    fun load(nameBuffer: FArchive, hashBuffer: FArchive, nameMapType: FMappedName.EType) {
        nameEntries = loadNameBatch(nameBuffer, hashBuffer)
        this.nameMapType = nameMapType
    }

    fun getName(mappedName: FMappedName): FName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        return FName(nameEntries, mappedName.getIndex().toInt(), mappedName.number.toInt())
    }

    fun getNameOrNull(mappedName: FMappedName): FName? {
        check(mappedName.getType() == nameMapType)
        val index = mappedName.getIndex().toInt()
        if (index < nameEntries.size) {
            return FName(nameEntries, index, mappedName.number.toInt())
        }
        return null
    }

    fun getMinimalName(mappedName: FMappedName): FMinimalName {
        check(mappedName.getType() == nameMapType)
        check(mappedName.getIndex() < nameEntries.size.toUInt())
        return FMinimalName(FNameEntryId(mappedName.getIndex()), mappedName.number.toInt(), nameEntries)
    }
}