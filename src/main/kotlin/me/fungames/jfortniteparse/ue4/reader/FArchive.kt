package me.fungames.jfortniteparse.ue4.reader

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
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
abstract class FArchive : Cloneable, InputStream() {
    var game = GAME_UE4(LATEST_SUPPORTED_UE4_VERSION)
    var ver = GAME_UE4_GET_AR_VER(game)
    /** Whether tagged property serialization is replaced by faster unversioned serialization. This assumes writer and reader share the same property definitions. */
    var useUnversionedPropertySerialization = false
    abstract var littleEndian: Boolean

    abstract override fun clone(): FArchive

    abstract fun seek(pos: Int)
    abstract fun size(): Int
    abstract fun pos(): Int

    open fun readBuffer(size: Int): ByteBuffer {
        //if (!rangeCheck(pos() + size))
        //    throw ParserException("Serializing behind stopper (${pos()}+${size} > ${size()})", this)
        val buffer = ByteBuffer.allocate(size).order(ByteOrder.LITTLE_ENDIAN)
        read(buffer.array())
        return buffer
    }

    open fun readBuffer(buffer: ByteBuffer) {
        val pos = buffer.position()
        buffer.put(read(buffer.remaining()))
        buffer.position(pos)
    }

    abstract override fun read(b: ByteArray, off: Int, len: Int): Int
    override fun read(buffer: ByteArray) = read(buffer, 0, buffer.size)
    abstract override fun skip(n: Long): Long
    override fun read() = try {
        readUInt8().toInt()
    } catch (t: Throwable) {
        -1
    }

    abstract fun printError(): String

    open fun read(size: Int): ByteArray {
        //if (!rangeCheck(pos() + size))
        //    throw ParserException("Serializing behind stopper (${pos()}+${size} > ${size()})", this)
        val res = ByteArray(size)
        read(res)
        return res
    }

    fun isAtStopper() = pos() == size()
    //protected open fun rangeCheck(pos: Int) = (0..size()).contains(pos)

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
    fun readString(): String {
        val length = readInt32()
        if (!(-65536..65536).contains(length))
            throw ParserException("Invalid String length '$length'", this)
        return if (length < 0) {
            val utf16length = -length
            val data = IntArray(utf16length - 1) { readUInt16().toInt() }
            if (readUInt16() != 0.toUShort())
                throw ParserException("Serialized FString is not null-terminated", this)
            String(data, 0, utf16length - 1)
        } else {
            if (length == 0)
                return ""
            String
            val string = Charsets.UTF_8.decode(readBuffer(length - 1)).toString()
            if (readUInt8() != 0.toUByte())
                throw ParserException("Serialized FString is not null-terminated", this)
            //if (string == "") For re-serializing replacing actually empty strings with these ones to detect the difference, causes issues with locres
            //    " "
            //else
            string
        }
    }

    inline fun <reified K, reified V> readTMap(length: Int, init: (FArchive) -> Pair<K, V>): MutableMap<K, V> {
        val res = HashMap<K, V>(length)
        for (i in 0 until length) {
            val (key, value) = init(this)
            res[key] = value
        }
        return res
    }

    inline fun <reified K, reified V> readTMap(init: (FArchive) -> Pair<K, V>) = readTMap(readInt32(), init)

    inline fun <reified T> readTArray(length: Int, init: (FArchive) -> T) = Array(length) { init(this) }

    inline fun <reified T> readTArray(init: (FArchive) -> T) = readTArray(readInt32(), init)

    inline fun <reified T> readBulkTArray(init: (FArchive) -> T): Array<T> {
        val elementSize = readInt32()
        val savePos = pos()
        val array = readTArray(readInt32(), init)
        if (pos() != savePos + 4 + array.size * elementSize)
            throw ParserException("RawArray item size mismatch: expected %d, serialized %d".format(elementSize, (pos() - savePos) / array.size))
        return array
    }

    open fun readFName() = FName.NAME_None
}