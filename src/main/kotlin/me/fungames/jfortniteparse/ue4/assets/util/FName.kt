package me.fungames.jfortniteparse.ue4.assets.util

import me.fungames.jfortniteparse.ue4.assets.objects.FNameEntry

@Suppress("EXPERIMENTAL_API_USAGE")
open class FName(private val nameMap: List<FNameEntry>, val index: Int, val extraIndex: Int) {
    class FNameDummy(override var text: String) : FName(emptyList(), -1, -1)
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
        get() = if (extraIndex == 0) nameMap[index].name else "${nameMap[index].name}_${extraIndex - 1}"
        set(value) {
            nameMap[index].name = value
        }
}