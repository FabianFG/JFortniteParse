package me.fungames.jfortniteparse.ue4.io

/**
 * @constructor Creates a pointer for the primitive type byte from the given array
 * @author FunGames
 */
class BytePointer(private val data: ByteArray) {

    /**
     * Creates a pointer for the primitive type byte from a newly allocated array with the given size
     */
    constructor(size: Int) : this(ByteArray(size))

    /**
     * Creates a pointer for the primitive type byte from a newly allocated array with the given size and the given initialization
     */
    constructor(size: Int, init: (Int) -> Byte) : this(ByteArray(size, init))

    constructor(other: BytePointer) : this(other.asArray()) {
        pos = other.pos
    }

    fun asArray() = data

    var pos = 0
        private set
    val size: Int = data.size

    operator fun inc(): BytePointer {
        pos++
        return this
    }

    operator fun dec(): BytePointer {
        pos--
        return this
    }

    operator fun get(i: Int): Byte {
        return data[pos + i]
    }

    operator fun set(i: Int, b: Byte) {
        data[pos + i] = b
    }

    operator fun plus(increment: Int): BytePointer {
        val p = BytePointer(this.data)
        p.pos = pos + increment
        return p
    }

    operator fun minus(decrement: Int): BytePointer {
        val p = BytePointer(this.data)
        p.pos = pos - decrement
        return p
    }

    operator fun plusAssign(i: Int) {
        pos += i
    }
}