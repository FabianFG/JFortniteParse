package me.fungames.jfortniteparse.ue4.pak.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.pak.objects.FPakInfo
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4_GET_AR_VER
import java.nio.ByteBuffer
import java.nio.ByteOrder

@ExperimentalUnsignedTypes
abstract class FPakArchive(val fileName : String) : FArchive() {

    lateinit var pakInfo : FPakInfo

    public abstract override fun clone(): FPakArchive

    abstract fun seek(pos : Long)
    override fun seek(pos: Int) = seek(pos.toLong())

    abstract fun pakSize() : Long
    override fun size() = pakSize().toInt()

    abstract fun pakPos() : Long
    override fun pos() = pakPos().toInt()

    abstract override fun read(buffer: ByteArray) : Int
    override fun readBuffer(size: Int) : ByteBuffer {
        //if (!rangeCheck(pakPos() + size))
        //    throw ParserException("Serializing behind stopper (${pos()}+${size} > ${size()})", this)
        val buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN)
        read(buffer.array())
        return buffer
    }
    override fun read(size: Int): ByteArray {
        //if (!rangeCheck(pakPos() + size))
        //    throw ParserException("Serializing behind stopper (${pos()}+${size} > ${size()})", this)
        val res = ByteArray(size)
        read(res)
        return res
    }
    //fun rangeCheck(pos: Long) = (0..pakSize()).contains(pos)

    override fun printError() = "FPakArchive Info: pos ${pakPos()}, stopper ${pakSize()}"

    fun readAndCreateReader(size : Int): FBytePakArchive {
        if (this is FBytePakArchive)
            throw IllegalStateException("This is already a temp reader")
        val readerPos = pakPos()
        return FBytePakArchive(read(size), fileName, readerPos, pakSize())
    }

    fun createReader(data : ByteArray, offset : Long) = FBytePakArchive(data, fileName, offset, pakSize()).apply { this.game = this@FPakArchive.game; this.ver = GAME_UE4_GET_AR_VER(this@FPakArchive.game) }

}