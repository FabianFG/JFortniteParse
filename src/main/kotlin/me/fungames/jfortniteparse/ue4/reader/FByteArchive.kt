package me.fungames.jfortniteparse.ue4.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import kotlin.math.min

@ExperimentalUnsignedTypes
open class FByteArchive(val data : ByteArray) : FArchive() {
    override var littleEndian = true

    protected var pos = 0
    protected val size = data.size

    override fun clone(): FByteArchive {
        val clone = FByteArchive(data)
        clone.pos = pos
        return clone
    }

    override fun seek(pos: Int) {
        rangeCheck(pos)
        this.pos = pos
    }

    override fun size() = size

    override fun pos() = pos

    override fun read(buffer: ByteArray) : Int {
        val count = min(size - pos, buffer.size)
        if (count == 0) return -1
        data.copyInto(buffer, 0, pos, pos + count)
        pos += count
        return count
    }

    override fun printError() = "FByteArrayArchive Info: pos $pos, stopper $size"
}