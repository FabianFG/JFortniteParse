package me.fungames.jfortniteparse.ue4.objects.uobject

open class FName(
    var nameMap: List<FNameEntry>,
    /** Index into the Names array (used to find String portion of the string/number pair used for comparison) */
    var index: Int,
    /** Number portion of the string/number pair (stored internally as 1 more than actual, so zero'd memory will be the default, no-instance case) */
    var number: Int
) {
    constructor() : this(NONE_SINGLETON_LIST, 0, 0)

    /**
     * Converts an FName to a readable format
     *
     * @return String representation of the name
     */
    override fun toString() = text

    open var text: String
        get() {
            val name = if (index == -1) "None" else nameMap[index].name
            return if (number == 0) name else "${name}_${number - 1}"
        }
        set(value) {
            nameMap[index].name = value
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FName) return false

        if (number != other.number) return false
        if (!text.equals(other.text, true)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = number
        result = 31 * result + text.toLowerCase().hashCode()
        return result
    }

    /** True for FName(), FName(NAME_None) and FName("None") */
    inline fun isNone() = text == "None"

    class FNameDummy(override var text: String, number: Int = 0): FName(emptyList(), -1, number)

    companion object {
        val NONE_SINGLETON_LIST = listOf(FNameEntry("None", 0u, 0u))

        @JvmField
        val NAME_None = FName()

        fun dummy(text: String, number: Int = 0) = FNameDummy(text.intern(), number)

        fun getByNameMap(text: String, nameMap: List<FNameEntry>): FName? {
            val nameEntry = nameMap.firstOrNull { text == it.name } ?: return null
            return FName(nameMap, nameMap.indexOf(nameEntry), 0)
        }

        inline fun createFromDisplayId(text: String, number: Int) = dummy(text, number)
    }
}