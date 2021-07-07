package me.fungames.jfortniteparse.ue4.objects.uobject

class FUniqueNetId<T>(val type: String, val contents: T) {
    override fun toString() = contents.toString()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as FUniqueNetId<*>

        if (type != other.type) return false
        if (contents != other.contents) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + contents.hashCode()
        return result
    }
}

open class FUniqueNetIdWrapper {
    open var uniqueNetId: FUniqueNetId<*>? = null

    /** Convert this value to a string */
    override fun toString() = uniqueNetId?.toString() ?: "INVALID"

    /** Convert this value to a string with additional information */
    fun toDebugString() = uniqueNetId?.let { it.type + ':' + it.contents } ?: "INVALID"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FUniqueNetIdWrapper) return false

        val lhsValid = uniqueNetId != null
        return lhsValid == (other.uniqueNetId != null) && (!lhsValid || uniqueNetId == other.uniqueNetId)
    }

    override fun hashCode() = uniqueNetId?.hashCode() ?: -1
}