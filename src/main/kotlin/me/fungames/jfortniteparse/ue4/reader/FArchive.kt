package me.fungames.jfortniteparse.ue4.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4_GET_AR_VER
import me.fungames.jfortniteparse.ue4.versions.LATEST_SUPPORTED_UE4_VERSION
import me.fungames.jfortniteparse.util.toFloat16
import java.io.InputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * UE4 Generic Binary reader
 */
@ExperimentalUnsignedTypes
abstract class FArchive : Cloneable, InputStream() {
    var game = GAME_UE4(LATEST_SUPPORTED_UE4_VERSION)
    var ver = GAME_UE4_GET_AR_VER(game)
    abstract var littleEndian: Boolean

    abstract override fun clone(): FArchive

    abstract fun seek(pos: Int)
    abstract fun size(): Int
    abstract fun pos(): Int

    abstract override fun read(buffer: ByteArray) : Int
    abstract override fun skip(n: Long): Long
    override fun read() = read(1)[0].toInt()
    abstract fun printError(): String

    open fun read(size: Int): ByteArray {
        if (!rangeCheck(pos() + size))
            throw ParserException("Serializing behind stopper (${pos()}+${size} > ${size()})", this)
        val res = ByteArray(size)
        read(res)
        return res
    }

    fun isAtStopper() = pos() == size()
    protected open fun rangeCheck(pos: Int) = (0..size()).contains(pos)

    open fun readInt8() = read(1)[0]

    fun readUInt8() = readInt8().toUByte()

    open fun readInt16(): Short {
        val data = read(2)
        val bb = ByteBuffer.wrap(data)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        return bb.short
    }

    fun readUInt16() = readInt16().toUShort()

    open fun readInt32(): Int {
        val data = read(4)
        val bb = ByteBuffer.wrap(data)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        return bb.int
    }

    fun readUInt32() = readInt32().toUInt()

    open fun readInt64(): Long {
        val data = read(8)
        val bb = ByteBuffer.wrap(data)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        return bb.long
    }

    fun readUInt64() = readInt64().toULong()

    open fun readFloat32(): Float {
        val data = read(4)
        val bb = ByteBuffer.wrap(data)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        return bb.float
    }

    open fun readDouble(): Double {
        val data = read(8)
        val bb = ByteBuffer.wrap(data)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        return bb.double
    }

    fun readFloat16() = readUInt16().toFloat16()

    fun readBoolean() = readInt32() != 0

    fun readFlag() = readUInt8() != 0.toUByte()

    //FString
    fun readString() : String {
        val length = this.readInt32()
        if (!(-65536..65536).contains(length))
            throw ParserException("Invalid String length '$length'", this)
        return if (length < 0) {
            val utf16length = length * -1
            val data = IntArray(utf16length)
            for (i in 0 until (utf16length - 1))
                data[i] = readUInt16().toInt()
            if (readUInt16() != 0.toUShort())
                throw ParserException("Serialized FString is not null-terminated", this)
            String(data, 0, utf16length - 1)
        } else {
            if (length == 0)
                return ""
            val string = read(length - 1).toString(Charsets.UTF_8)
            if (readUInt8() != 0.toUByte())
                throw ParserException("Serialized FString is not null-terminated", this)
            //if (string == "") For re-serializing replacing actually empty strings with these ones to detect the difference, causes issues with locres
            //    " "
            //else
            string
        }
    }

    inline fun <reified K, reified V> readTMap(length: Int, init : (FArchive) -> Pair<K,V>): MutableMap<K, V> {
        val res = mutableMapOf<K, V>()
        for (i in 0 until length) {
            val (key, value) = init(this)
            res[key] = value
        }
        return res
    }

    inline fun <reified K, reified V> readTMap(init : (FArchive) -> Pair<K, V>) = readTMap(readInt32(), init)

    inline fun <reified T> readTArray(length : Int, init : (FArchive) -> T) = Array(length) {init(this)}

    inline fun <reified T> readTArray(init: (FArchive) -> T) = readTArray(readInt32(), init)

    inline fun <reified T> readBulkTArray(init: (FArchive) -> T): Array<T> {
        val elementSize = readInt32()
        val savePos = pos()
        val array = readTArray(readInt32(), init)
        if (pos() != savePos + 4 + array.size * elementSize)
            throw ParserException("RawArray item size mismatch: expected %d, serialized %d".format(elementSize, (pos() - savePos) / array.size))
        return array
    }
}