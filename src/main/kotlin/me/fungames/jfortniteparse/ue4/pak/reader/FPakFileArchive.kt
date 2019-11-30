package me.fungames.jfortniteparse.ue4.pak.reader

import java.io.File
import java.io.RandomAccessFile

@ExperimentalUnsignedTypes
class FPakFileArchive(val rafile : RandomAccessFile, val file : File) : FPakArchive(file.name) {
    override var littleEndian = true

    override fun clone(): FPakFileArchive {
        val clone = FPakFileArchive(rafile, file)
        clone.pakInfo = pakInfo
        return clone
    }

    override fun seek(pos : Long) { rafile.seek(pos) }
    override fun seek(pos: Int) = seek(pos.toLong())

    override fun pakSize() = rafile.length()
    override fun size() = pakSize().toInt()

    override fun pakPos() = rafile.filePointer
    override fun pos() = pakPos().toInt()

    override fun read(buffer: ByteArray) : Int {
        return rafile.read(buffer)
    }

    override fun printError() = "FPakArchive Info: pos ${pakPos()}, stopper ${pakSize()}"

}