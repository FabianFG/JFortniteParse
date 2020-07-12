package me.fungames.jfortniteparse.ue4.pak.reader

import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.min

@ExperimentalUnsignedTypes
class FBytePakArchive(val data : ByteBuffer, fileName: String, val offsetInPakFile : Long, val pakFileSize : Long) : FPakArchive(fileName) {
    init {
        data.order(ByteOrder.LITTLE_ENDIAN)
    }

    constructor(data : ByteArray, fileName: String, offsetInPakFile : Long, pakFileSize : Long) : this(ByteBuffer.wrap(data), fileName, offsetInPakFile, pakFileSize)

    override var littleEndian : Boolean
        get() = data.order() == ByteOrder.LITTLE_ENDIAN
        set(value) {
            if (value)
                data.order(ByteOrder.LITTLE_ENDIAN)
            else
                data.order(ByteOrder.BIG_ENDIAN)
        }

    private var pos : Int
        get() = data.position()
        set(value) { data.position(value) }
    private val size = data.limit()

    override fun clone(): FBytePakArchive {
        val clone = FBytePakArchive(data, fileName, offsetInPakFile, pakFileSize)
        clone.pos = pos
        clone.pakInfo = pakInfo
        return clone
    }

    override fun seek(pos: Long) {
        rangeCheck(pos.toInt())
        this.pos = pos.toInt()
    }

    override fun skip(n: Long): Long {
        rangeCheck(pos + n.toInt())
        this.pos += n.toInt()
        return n
    }

    override fun pakSize() = pakFileSize

    override fun pakPos() = offsetInPakFile

    override fun read(buffer: ByteArray) : Int {
        val count = min(size - pos, buffer.size)
        if (count == 0) return -1
        data.get(buffer, 0, count)
        return count
    }
}