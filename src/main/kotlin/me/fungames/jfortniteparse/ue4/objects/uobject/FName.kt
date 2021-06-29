package me.fungames.jfortniteparse.ue4.objects.uobject

import kotlin.jvm.JvmField as F

open class FName {
    val names: List<String>
    /** Index into the Names array (used to find String portion of the string/number pair used for comparison) */
    val index: Int
    /** Number portion of the string/number pair (stored internally as 1 more than actual, so zero'd memory will be the default, no-instance case) */
    val number: Int

    @JvmOverloads
    constructor(names: List<String>, index: Int, number: Int = 0) {
        this.names = names
        this.index = index
        this.number = number
    }

    @JvmOverloads
    constructor(name: String, number: Int = 0) : this(listOf(name.intern()), 0, number)

    constructor() : this(NONE_SINGLETON_LIST, 0, 0)

    /**
     * Converts an FName to a readable format
     *
     * @return String representation of the name
     */
    override fun toString() = text

    open var text: String
        get() {
            val name = if (index == -1) "None" else names[index]
            return if (number == 0) name else "${name}_${number - 1}"
        }
        set(value) {
            val nameMap = names
            if (nameMap is MutableList) {
                nameMap[index] = value
            }
        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FName) return false

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

    class FNameDummy(var name: String, number: Int = 0) : FName(emptyList(), -1, number) {
        override var text = name
            get() = if (number == 0) name else "${name}_${number - 1}"
            set(value) {
                field = value
                name = value
            }
    }

    companion object {
        @F val NONE_SINGLETON_LIST = listOf("None")
        @F val NAME_None = FName()

        fun getByNameMap(text: String, nameMap: List<String>): FName? {
            val nameEntry = nameMap.firstOrNull { it == text } ?: return null
            return FName(nameMap, nameMap.indexOf(nameEntry), 0)
        }
    }
}