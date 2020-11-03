package me.fungames.jfortniteparse.ue4.objects.uobject

import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.util.get

private const val NAME_NO_NUMBER_INTERNAL = 0

/** Opaque id to a deduplicated name */
class FNameEntryId(var value: UInt = 0u) {
    constructor(Ar: FArchive) : this(Ar.readUInt32())

    operator fun compareTo(rhs: FNameEntryId) = value.compareTo(rhs.value)
}

/**
 * This struct is only used during loading/saving and is not part of the runtime costs
 */
inline class FNameEntrySerialized(val name: String)

/**
 * The minimum amount of data required to reconstruct a name
 * This is smaller than FName, but you lose the case-preserving behavior
 */
class FMinimalName(
    /** Index into the Names array (used to find String portion of the string/number pair) */
    var index: FNameEntryId,
    /** Number portion of the string/number pair (stored internally as 1 more than actual, so zero'd memory will be the default, no-instance case) */
    var number: Int = NAME_NO_NUMBER_INTERNAL,
    private val nameMap: List<String>) {
    constructor(Ar: FArchive, nameMap: List<String>) : this(FNameEntryId(Ar), Ar.readInt32(), nameMap)

    fun toName() = FName.dummy(nameMap[index.value], number)
}