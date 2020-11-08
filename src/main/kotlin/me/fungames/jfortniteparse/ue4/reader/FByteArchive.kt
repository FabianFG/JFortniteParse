package me.fungames.jfortniteparse.ue4.reader

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

@ExperimentalUnsignedTypes
open class FByteArchive(val data: ByteBuffer) : FArchive() {
    init {
        data.order(ByteOrder.LITTLE_ENDIAN)
    }

    constructor(data: ByteArray) : this(ByteBuffer.wrap(data))

    override var littleEndian: Boolean
        get() = data.order() == ByteOrder.LITTLE_ENDIAN
        set(value) {
            if (value)
                data.order(ByteOrder.LITTLE_ENDIAN)
            else
                data.order(ByteOrder.BIG_ENDIAN)
        }

    protected var pos: Int
        get() = data.position()
        set(value) {
            data.position(value)
        }
    protected val size = data.limit()

    override fun clone(): FByteArchive {
        val clone = FByteArchive(data)
        clone.pos = pos
        return clone
    }

    override fun seek(pos: Int) {
        //rangeCheck(pos)
        this.pos = pos
    }

    override fun skip(n: Long): Long {
        //rangeCheck(pos + n.toInt())
        this.pos += n.toInt()
        return n
    }

    override fun size() = size

    override fun pos() = pos


    override fun readBuffer(size: Int): ByteBuffer {
        return data.duplicate().apply {
            order(data.order())
            limit(position() + size)
            pos += size
        }
    }

    override fun read(b: ByteArray, off: Int, len: Int): Int {
        val count = min(size - pos, len)
        if (count == 0) return -1
        data.get(b, off, len)
        return count
    }

    override fun readDouble() = data.double
    override fun readFloat32() = data.float
    override fun readInt8() = data.get()
    override fun readInt16() = data.short
    override fun readInt32() = data.int
    override fun readInt64() = data.long

    override fun printError() = "FByteArchive Info: pos $pos, stopper $size"
}