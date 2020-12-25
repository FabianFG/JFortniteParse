package me.fungames.jfortniteparse.ue4.assets.mappings

import me.fungames.jfortniteparse.compression.Compression
import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.exceptions.UnknownCompressionMethodException
import me.fungames.jfortniteparse.ue4.assets.exports.UScriptStruct
import me.fungames.jfortniteparse.ue4.assets.mappings.UsmapTypeMappingsProvider.EUsmapPropertyType.*
import me.fungames.jfortniteparse.ue4.assets.objects.PropertyInfo
import me.fungames.jfortniteparse.ue4.assets.objects.PropertyType
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.pak.reader.FPakFileArchive
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FArchiveProxy
import me.fungames.jfortniteparse.ue4.reader.FByteArchive
import java.io.File
import java.io.RandomAccessFile

open class UsmapTypeMappingsProvider(val file: File) : TypeMappingsProvider() {
    companion object {
        val FILE_MAGIC = 0x30C4.toShort()
    }

    override fun reload(): Boolean {
        val data = readCompressedUsmap(FPakFileArchive(RandomAccessFile(file, "r"), file))
        parseData(FUsmapNameTableArchive(FByteArchive(data)))
        return true
    }

    protected fun readCompressedUsmap(Ar: FArchive): ByteArray {
        val magic = Ar.readInt16()
        if (magic != FILE_MAGIC) {
            throw ParserException(".usmap file has an invalid magic constant")
        }

        val version = Ar.read()
        if (version != EUsmapVersion.latest().ordinal) {
            throw ParserException(".usmap file has invalid version $version")
        }

        val method = Ar.read()
        val compSize = Ar.readInt32()
        val decompSize = Ar.readInt32()
        if (Ar.size() - Ar.pos() < compSize) {
            throw ParserException("There is not enough data in the .usmap file")
        }

        val compData = ByteArray(compSize)
        Ar.read(compData)
        val data = ByteArray(decompSize)
        Compression.uncompressMemory(when (method) {
            0 -> FName.NAME_None
            1 -> FName.dummy("Oodle")
            2 -> FName.dummy("Brotli")
            else -> throw UnknownCompressionMethodException("Unknown compression method index $method")
        }, data, 0, decompSize, compData, 0, compSize)
        return data
    }

    private fun deserializePropData(Ar: FUsmapNameTableArchive): PropertyType {
        val propType = values()[Ar.read()]
        val type = PropertyType(FName.dummy(propType.name))
        when (propType) {
            EnumProperty -> {
                type.innerType = deserializePropData(Ar)
                type.enumName = Ar.readFName()
            }
            StructProperty -> type.structType = Ar.readFName()
            SetProperty, ArrayProperty -> type.innerType = deserializePropData(Ar)
            MapProperty -> {
                type.innerType = deserializePropData(Ar)
                type.valueType = deserializePropData(Ar)
            }
        }
        if (!type.structType.isNone()) {
            type.structClass = lazy { mappings.types[type.structType.text]!! }
        }
        return type
    }

    private fun parseData(Ar: FUsmapNameTableArchive) {
        Ar.nameMap = Ar.readTArray { String(Ar.read(Ar.read())) }
        var cnt = 0
        mappings.enums = Ar.readTMap {
            val enumName = Ar.readFName().text
            val enumValues = List(Ar.read()) { Ar.readFName().text }
            ++cnt
            enumName to enumValues
        }
        repeat(Ar.readInt32()) {
            val struct = UScriptStruct()
            struct.name = Ar.readFName().text
            val superStructName = Ar.readFName()
            struct.superStruct = if (!superStructName.isNone()) lazy { mappings.types[superStructName.text]!! } else null
            struct.propertyCount = Ar.readUInt16().toInt()
            val serializablePropCount = Ar.readUInt16()
            struct.childProperties2 = List(serializablePropCount.toInt()) {
                val schemaIdx = Ar.readUInt16()
                val arraySize = Ar.readUInt8()
                val propertyName = Ar.readFName()
                val type = deserializePropData(Ar)
                PropertyInfo(propertyName.text, type, arraySize.toInt()).apply { index = schemaIdx.toInt() }
            }
            mappings.types[struct.name] = struct
        }
    }

    class FUsmapNameTableArchive(innerAr: FArchive) : FArchiveProxy(innerAr) {
        lateinit var nameMap: Array<String>

        override fun readFName(): FName {
            val nameIndex = readInt32()
            if (nameIndex == -1) {
                return FName.NAME_None
            }
            if (!nameMap.indices.contains(nameIndex)) {
                throw ParserException("FName could not be read, requested index $nameIndex, name map size ${nameMap.size}", this)
            }
            return FName.dummy(nameMap[nameIndex])
        }
    }

    enum class EUsmapVersion {
        Initial;

        companion object {
            fun latest() = values().last()
        }
    }

    enum class EUsmapPropertyType {
        ByteProperty,
        BoolProperty,
        IntProperty,
        FloatProperty,
        ObjectProperty,
        NameProperty,
        DelegateProperty,
        DoubleProperty,
        ArrayProperty,
        StructProperty,
        StrProperty,
        TextProperty,
        InterfaceProperty,
        MulticastDelegateProperty,
        WeakObjectProperty, //
        LazyObjectProperty, // When deserialized, these 3 properties will be SoftObjects
        AssetObjectProperty, //
        SoftObjectProperty,
        UInt64Property,
        UInt32Property,
        UInt16Property,
        Int64Property,
        Int16Property,
        Int8Property,
        MapProperty,
        SetProperty,
        EnumProperty,
        FieldPathProperty
    }
}