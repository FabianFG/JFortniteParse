package me.fungames.jfortniteparse.ue4.pak.reader

import me.fungames.jfortniteparse.ue4.versions.VersionContainer
import java.io.File
import java.io.RandomAccessFile

class FPakFileArchive(val rafile: RandomAccessFile, val file: File, versions: VersionContainer = VersionContainer.DEFAULT) : FPakArchive(file.name, versions) {
    override var littleEndian = true

    override fun clone(): FPakFileArchive {
        val clone = FPakFileArchive(rafile, file)
        if (hasPakInfo)
            clone.pakInfo = pakInfo
        return clone
    }

    override fun seek(pos: Long) { rafile.seek(pos) }
    override fun seek(pos: Int) = seek(pos.toLong())

    override fun skip(n: Long): Long {
        //rangeCheck(pakPos() + n)
        seek(pakPos() + n)
        return n
    }

    override fun pakSize() = rafile.length()
    override fun size() = pakSize().toInt()

    override fun pakPos() = rafile.filePointer
    override fun pos() = pakPos().toInt()

    override fun read() = rafile.read()
    override fun read(b: ByteArray, off: Int, len: Int) = rafile.read(b, off, len)
    override fun read(b: ByteArray) = rafile.read(b)

    override fun close() = rafile.close()

    override fun printError() = "FPakArchive Info: pos ${pakPos()}, stopper ${pakSize()}, file $file"
}