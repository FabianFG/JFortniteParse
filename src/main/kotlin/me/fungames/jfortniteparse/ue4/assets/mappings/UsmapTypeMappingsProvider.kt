package me.fungames.jfortniteparse.ue4.assets.mappings

import me.fungames.jfortniteparse.compression.Compression
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.exceptions.UnknownCompressionMethodException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.pak.reader.FPakFileArchive
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import java.io.File
import java.io.RandomAccessFile

class UsmapTypeMappingsProvider(val path: String) : TypeMappingsProvider() {
    companion object {
        val FILE_MAGIC = 0x30C4.toShort()
    }

    override fun reload(): Boolean {
        val data = readCompressedUsmap()
        parseData(FByteArchive(data))
        return true
    }

    private fun readCompressedUsmap(): ByteArray {
        val file = File(path)
        val Ar = FPakFileArchive(RandomAccessFile(file, "r"), file)

        val magic = Ar.readInt16()
        if (magic != FILE_MAGIC) {
            throw ParserException(".usmap file has an invalid magic constant")
        }

        val version = Ar.read()
        if (version != Version.latest().ordinal) {
            throw ParserException(".usmap file has invalid version $version")
        }

        val method = Ar.read()
        val compSize = Ar.readInt32()
        val decompSize = Ar.readInt32()
        if (Ar.size() - Ar.pos() < compSize) {
            throw ParserException("There is not enough data in the .usmap file")
        }

        val compData = ByteArray(compSize)
        Ar.read(compSize)
        val data = ByteArray(decompSize)
        Compression.uncompressMemory(when (method) {
            0 -> FName.NAME_None
            1 -> FName.dummy("Oodle")
            2 -> FName.dummy("Brotli")
            else -> throw UnknownCompressionMethodException("Unknown compression method index $method")
        }, data, 0, decompSize, compData, 0, compSize)
        return data
    }

    private fun parseData(Ar: FArchive) {
        val nameLUT = Ar.readTArray { String(Ar.read(Ar.read())) }
        mappings.enums = Ar.readTMap {
            val enumName = nameLUT[Ar.readInt32()]
            val enumValues = Ar.readArray { nameLUT[Ar.readInt32()] }
            enumName to enumValues
        }
    }

    enum class Version {
        Initial;

        companion object {
            fun latest() = values().last()
        }
    }
}