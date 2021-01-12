package me.fungames.jfortniteparse.ue4.writer

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4
import me.fungames.jfortniteparse.ue4.versions.GAME_UE4_GET_AR_VER
import me.fungames.jfortniteparse.ue4.versions.LATEST_SUPPORTED_UE4_VERSION
import java.io.OutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

abstract class FArchiveWriter : OutputStream() {
    var game = GAME_UE4(LATEST_SUPPORTED_UE4_VERSION)
    var ver = GAME_UE4_GET_AR_VER(game)
    abstract var littleEndian: Boolean
    abstract fun pos() : Int
    abstract override fun write(buffer : ByteArray)
    override fun write(b: Int) = write(byteArrayOf(b.toByte()))
    abstract fun printError() : String

    fun writeInt8(i : Byte) {
        write(byteArrayOf(i))
    }
    fun writeUInt8(i : UByte) {
        writeInt8(i.toByte())
    }

    fun writeInt16(i : Short) {
        val bb = ByteBuffer.allocate(2)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        bb.putShort(i)
        write(bb.array())
    }

    fun writeUInt16(i : UShort) {
        writeInt16(i.toShort())
    }

    fun writeInt32(i : Int) {
        val bb = ByteBuffer.allocate(4)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        bb.putInt(i)
        write(bb.array())
    }

    fun writeUInt32(i : UInt) {
        writeInt32(i.toInt())
    }

    fun writeInt64(i : Long) {
        val bb = ByteBuffer.allocate(8)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        bb.putLong(i)
        write(bb.array())
    }

    fun writeUInt64(i : ULong) {
        writeInt64(i.toLong())
    }

    fun writeFloat32(i : Float) {
        val bb = ByteBuffer.allocate(4)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        bb.putFloat(i)
        write(bb.array())
    }

    fun writeDouble(i : Double) {
        val bb = ByteBuffer.allocate(8)
        if (this.littleEndian)
            bb.order(ByteOrder.LITTLE_ENDIAN)
        bb.putDouble(i)
        write(bb.array())
    }

    fun writeBoolean(i : Boolean) {
        if (i) writeInt32(1) else writeInt32(0)
    }
    fun writeFlag(i : Boolean) {
        if (i) writeInt8(1) else writeInt8(0)
    }

    fun writeString(i : String) {
        if (!(-65536..65536).contains(i.length))
            throw ParserException("Invalid String length '${i.length}'", this)
        when {
//            i == " " -> {
//                writeInt32(1)
//                writeInt8(0)
//            }
            i.isNotEmpty() -> {
                writeInt32(i.length + 1)
                write(i.toByteArray(Charsets.UTF_8))
                writeInt8(0)
            }
            else -> writeInt32(0)
        }
    }

    fun <K, V> writeTMapWithoutSize(map : Map<K, V>, write : (K, V) -> Unit) {
        map.forEach{(key, value) -> write(key, value)}
    }

    fun <K, V> writeTMap(map: Map<K, V>, write : (K, V) -> Unit) {
        writeInt32(map.size)
        writeTMapWithoutSize(map, write)
    }

    fun <T> writeTArrayWithoutSize(array: Array<T>, write : (T) -> Unit) {
        array.forEach { write(it) }
    }

    fun <T> writeTArray(array : Array<T>, write : (T) -> Unit) {
        writeInt32(array.size)
        writeTArrayWithoutSize(array, write)
    }

    open fun writeFName(name: FName) {}

    //Util functions

    fun Float.toUInt16() : UShort {
        val fbits = this.toBits()
        val sign = fbits.ushr(16) and 0x8000          // sign only
        var `val` = (fbits and 0x7fffffff) + 0x1000 // rounded value

        if (`val` >= 0x47800000)
        // might be or become NaN/Inf
        {                                     // avoid Inf due to rounding
            return (if (fbits and 0x7fffffff >= 0x47800000) {                                 // is or must become NaN/Inf
                if (`val` < 0x7f800000) sign or 0x7c00 else sign or 0x7c00 or        // remains +/-Inf or NaN

                        (fbits and 0x007fffff).ushr(13)     // make it +/-Inf
                // keep NaN (and Inf) bits
            } else sign or 0x7bff).toUShort()
// unrounded not quite Inf
        }
        if (`val` >= 0x38800000)
        // remains normalized value
            return (sign or (`val` - 0x38000000).ushr(13)).toUShort() // exp - 127 + 15
        if (`val` < 0x33000000)
        // too small for subnormal
            return sign.toUShort()                      // becomes +/-0
        `val` = (fbits and 0x7fffffff).ushr(23)  // tmp exp for subnormal calc
        return (sign or ((fbits and 0x7fffff or 0x800000) // add subnormal bit
                + 0x800000.ushr(`val` - 102))     // round depending on cut off
            .ushr(126 - `val`)).toUShort()   // div by 2^(1-(exp-127+15)) and >> 13 | exp=0
    }
}