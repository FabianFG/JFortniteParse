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
import kotlin.experimental.and

/**
 * UE4 Generic Binary reader
 */
abstract class FArchive : Cloneable, InputStream() {
    var game = GAME_UE4(LATEST_SUPPORTED_UE4_VERSION)
    var ver = GAME_UE4_GET_AR_VER(game)
    /** Whether tagged property serialization is replaced by faster unversioned serialization. This assumes writer and reader share the same property definitions. */
    var useUnversionedPropertySerialization = false
    /** Whether editor only properties are being filtered from the archive (or has been filtered). */
    var isFilterEditorOnly = true
    abstract var littleEndian: Boolean

    abstract override fun clone(): FArchive

    abstract fun seek(pos: Int)
    abstract fun size(): Int
    abstract fun pos(): Int

    open fun readBuffer(size: Int): ByteBuffer {
        //if (!rangeCheck(pos() + size))
        //    throw ParserException("Serializing behind stopper (${pos()}+${size} > ${size()})", this)
        val buffer = ByteBuffer.allocate(size).order(if (littleEndian) ByteOrder.LITTLE_ENDIAN else ByteOrder.BIG_ENDIAN)
        read(buffer.array())
        return buffer
    }

    open fun readBuffer(buffer: ByteBuffer) {
        val pos = buffer.position()
        buffer.put(read(buffer.remaining()))
        buffer.position(pos)
    }

    abstract override fun read(b: ByteArray, off: Int, len: Int): Int
    abstract override fun skip(n: Long): Long

    abstract fun printError(): String

    open fun read(size: Int): ByteArray {
        //if (!rangeCheck(pos() + size))
        //    throw ParserException("Serializing behind stopper (${pos()}+${size} > ${size()})", this)
        val res = ByteArray(size)
        read(res)
        return res
    }

    open fun readBits(b: ByteArray, sizeBits: Int) {
        read(b, 0, (sizeBits + 7) / 8);
        if (sizeBits % 8 != 0) {
            b[sizeBits / 8] = b[sizeBits / 8] and ((1 shl (sizeBits and 7)) - 1).toByte()
        }
    }

    fun isAtStopper() = pos() == size()
    //protected open fun rangeCheck(pos: Int) = (0..size()).contains(pos)

    open fun readInt8() = read().toByte()

    open fun readInt16(): Short {
        val b = read(2)
        return if (littleEndian) {
            ((b[1].toInt() and 0xFF) shl 8) or
             (b[0].toInt() and 0xFF)
        } else {
            ((b[0].toInt() and 0xFF) shl 8) or
             (b[1].toInt() and 0xFF)
        }.toShort()
    }

    open fun readInt32(): Int {
        val b = read(4)
        return if (littleEndian) {
            ((b[3].toInt() and 0xFF) shl 24) or
            ((b[2].toInt() and 0xFF) shl 16) or
            ((b[1].toInt() and 0xFF) shl 8) or
             (b[0].toInt() and 0xFF)
        } else {
            ((b[0].toInt() and 0xFF) shl 24) or
            ((b[1].toInt() and 0xFF) shl 16) or
            ((b[2].toInt() and 0xFF) shl 8) or
             (b[3].toInt() and 0xFF)
        }
    }

    open fun readInt64(): Long {
        val b = read(8)
        return if (littleEndian) {
            ((b[7].toLong() and 0xFF) shl 56) or
            ((b[6].toLong() and 0xFF) shl 48) or
            ((b[5].toLong() and 0xFF) shl 40) or
            ((b[4].toLong() and 0xFF) shl 32) or
            ((b[3].toLong() and 0xFF) shl 24) or
            ((b[2].toLong() and 0xFF) shl 16) or
            ((b[1].toLong() and 0xFF) shl 8) or
             (b[0].toLong() and 0xFF)
        } else {
            ((b[0].toLong() and 0xFF) shl 56) or
            ((b[1].toLong() and 0xFF) shl 48) or
            ((b[2].toLong() and 0xFF) shl 40) or
            ((b[3].toLong() and 0xFF) shl 32) or
            ((b[4].toLong() and 0xFF) shl 24) or
            ((b[5].toLong() and 0xFF) shl 16) or
            ((b[6].toLong() and 0xFF) shl 8) or
             (b[7].toLong() and 0xFF)
        }
    }

    fun readUInt8() = readInt8().toUByte()
    fun readUInt16() = readInt16().toUShort()
    fun readUInt32() = readInt32().toUInt()
    fun readUInt64() = readInt64().toULong()

    fun readFloat16() = readUInt16().toFloat16()
    open fun readFloat32() = java.lang.Float.intBitsToFloat(readInt32())
    open fun readDouble() = java.lang.Double.longBitsToDouble(readInt64())

    fun readBoolean(): Boolean {
        val int = readInt32()
        if (int != 0 && int != 1) {
            throw ParserException("Invalid bool value ($int)", this)
        }
        return int != 0
    }

    fun readFlag(): Boolean {
        val int = readUInt8().toInt()
        if (int != 0 && int != 1) {
            throw ParserException("Invalid bool value ($int)", this)
        }
        return int != 0
    }

    //FString
    fun readString(): String {
        val length = readInt32()
        if (length < -65536 || length > 65536)
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
        val res = LinkedHashMap<K, V>(length)
        for (i in 0 until length) {
            val (key, value) = init(this)
            res[key] = value
        }
        return res
    }

    inline fun <reified K, reified V> readTMap(init: (FArchive) -> Pair<K, V>) = readTMap(readInt32(), init)

    inline fun <reified T> readTArray(init: (Int) -> T) = Array(readInt32()) { init(it) }

    inline fun <reified T> readBulkTArray(init: (Int) -> T): Array<T> {
        val elementSize = readInt32()
        val savePos = pos()
        val array = readTArray(init)
        if (pos() != savePos + 4 + array.size * elementSize)
            throw ParserException("RawArray item size mismatch: expected %d, serialized %d".format(elementSize, (pos() - savePos) / array.size))
        return array
    }

    inline fun <reified T> readArray(init: (FArchive) -> T) = MutableList(readInt32()) { init(this) }

    open fun readFName() = FName.NAME_None
}