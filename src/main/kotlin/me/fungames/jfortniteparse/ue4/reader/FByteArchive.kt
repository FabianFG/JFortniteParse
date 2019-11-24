package me.fungames.jfortniteparse.ue4.reader

import me.fungames.jfortniteparse.exceptions.ParserException

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
        if (!rangeCheck(pos() + buffer.size))
            throw ParserException("Serializing behind stopper (${pos()}+${buffer.size} > ${size()})", this)
        for (i in buffer.indices) {
            buffer[i] = data[pos + i]
        }
        pos += buffer.size
        return buffer.size
    }

    override fun printError() = "FByteArrayArchive Info: pos $pos, stopper $size"
}