package me.fungames.jfortniteparse.ue4.pak.reader

import kotlin.math.min

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

    override fun read(buffer: ByteArray) : Int {
        val count = min(size - pos, buffer.size)
        if (count == 0) return -1
        data.copyInto(buffer, 0, pos, pos + count)
        pos += count
        return count
    }
}