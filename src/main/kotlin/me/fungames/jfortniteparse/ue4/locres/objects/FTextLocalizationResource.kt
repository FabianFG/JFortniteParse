package me.fungames.jfortniteparse.ue4.locres.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.FGuid
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.reader.FArchive

@ExperimentalUnsignedTypes
class FTextLocalizationResource : UClass {
    companion object {
        val locresMagic = FGuid(1970541582u, 4228074087u, 2643465546u, 461322179u)
        const val indexNone = -1L
    }

    var version : UByte
    var strArrayOffset: Long
    val stringData : MutableMap<String, MutableMap<String, String>>

    constructor(Ar : FArchive) {
        super.init(Ar)
        val magic = FGuid(Ar)
        if (magic != locresMagic)
            throw ParserException("Wrong locres guid")
        version = Ar.readUInt8()
        strArrayOffset = Ar.readInt64()
        if (strArrayOffset == indexNone)
            throw ParserException("No offset found")

        //Only works for version 'optimized'
        val cOffset = Ar.pos()

        Ar.seek(strArrayOffset.toInt())
        val localizedStrings = Ar.readTArray { FTextLocalizationResourceString(Ar) }
        Ar.seek(cOffset)

        Ar.readUInt32() // entryCount
        val nameSpaceCount = Ar.readUInt32()
        stringData = mutableMapOf()
        for (i in 0 until nameSpaceCount.toInt()) {
            val nameSpace = FTextKey(Ar)
            val keyCount = Ar.readUInt32()

            val strings = mutableMapOf<String, String>()
            for (j in 0 until keyCount.toInt()) {
                val textKey = FTextKey(Ar)
                Ar.readUInt32() // source hash
                val stringIndex = Ar.readInt32()
                if (stringIndex > 0 && stringIndex < localizedStrings.size)
                    strings[textKey.text] = localizedStrings[stringIndex].data
            }
            stringData[nameSpace.text] = strings
        }
        super.complete(Ar)
    }
}
