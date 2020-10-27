package me.fungames.jfortniteparse.ue4.objects.uobject

@Suppress("EXPERIMENTAL_API_USAGE")
open class FName(
    private val nameMap: List<FNameEntry>,
    /** Index into the Names array (used to find String portion of the string/number pair used for display) */
    val index: Int,
    /** Number portion of the string/number pair (stored internally as 1 more than actual, so zero'd memory will be the default, no-instance case) */
    val number: Int
) {
    constructor() : this(emptyList(), -1, 0)

    class FNameDummy(override var text: String) : FName(emptyList(), -1, 0)
    companion object {
        @JvmField
        val NAME_None: FName = dummy("None")

        fun dummy(text: String) = FNameDummy(text)
        fun getByNameMap(text: String, nameMap: List<FNameEntry>): FName? {
            val nameEntry = nameMap.find { text == it.name } ?: return null
            return FName(nameMap, nameMap.indexOf(nameEntry), 0)
        }
    }

    override fun toString() = text

    open var text: String
        get() = if (number == 0) nameMap[index].name else "${nameMap[index].name}_${number - 1}"
        set(value) {
            nameMap[index].name = value
        }

    inline fun isNone() = this == NAME_None
}