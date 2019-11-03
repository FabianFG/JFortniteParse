package me.fungames.jfortniteparse.ue4.pak.reader

import me.fungames.jfortniteparse.exceptions.ParserException

@ExperimentalUnsignedTypes
class FBytePakArchive(val data : ByteArray, fileName: String, val offsetInPakFile : Long, val pakFileSize : Long) : FPakArchive(fileName) {
    override var littleEndian = true

    private var pos = 0
    private val size = data.size

    override fun clone(): FBytePakArchive {
        val clone = FBytePakArchive(data, fileName, offsetInPakFile, pakFileSize)
        clone.pos = pos
        return clone
    }

    override fun seek(pos: Long) {
        rangeCheck(pos.toInt())
        this.pos = pos.toInt()
    }

    override fun pakSize() = pakFileSize

    override fun pakPos() = offsetInPakFile

    override fun read(buffer: ByteArray) {
        if (!rangeCheck(pakPos() + buffer.size))
            throw ParserException("Serializing behind stopper (${pakPos()}+${buffer.size} > ${pakSize()})", this)
        for (i in buffer.indices) {
            buffer[i] = data[pos + i]
        }
        pos += buffer.size
    }
}