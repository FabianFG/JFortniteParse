package me.fungames.jfortniteparse.ue4.assets.exports.mats

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.reader.FArchiveProxy

class FMaterialResourceProxyReader : FArchiveProxy {
    lateinit var names: List<String>
    var offsetToFirstResource = 0

    constructor(Ar: FArchive) : super(Ar) {
        initialize()
    }

    override fun readFName(): FName {
        val nameIdx = readInt32()
        val instNum = readInt32()
        if (nameIdx >= 0 && nameIdx < names.size) {
            return FName(names, nameIdx, instNum)
        }
        throw ParserException("FMaterialResourceProxyReader: deserialized an invalid FName, NameIdx=%d, Names.Num()=%d (Offset=%d, InnerArchive.Tell()=%d, OffsetToFirstResource=%d)"
            .format(nameIdx, names.size, pos(), wrappedAr.pos(), 0))
    }

    fun initialize() {
        names = readArray {
            val name = readString()
            skip(4)
            name
        }
        val locs = readTArray { FMaterialResourceLocOnDisk(readUInt32(), readUInt8(), readUInt8()) }
        check(locs[0].offset == 0u)
        val numBytes = readInt32()

        offsetToFirstResource = wrappedAr.pos()
    }
}

class FMaterialResourceLocOnDisk(var offset: UInt, var featureLevel: UByte, var qualityLevel: UByte)