package me.fungames.jfortniteparse.ue4.objects.core.i18n

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.objects.core.misc.FGuid
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
class FTextLocalizationResource : UClass {
    companion object {
        // val locMetaMagic = FGuid(0xA14CEE4Fu, 0x83554868u, 0xBD464C6Cu, 0x7C50DA70u)
        val locResMagic = FGuid(0x7574140Eu, 0xFC034A67u, 0x9D90154Au, 0x1B7F37C3u)
        const val indexNone = -1L
    }

    var version: UByte
    var strArrayOffset: Long
    val stringData: MutableMap<String, MutableMap<String, String>>

    constructor(Ar: FArchive) {
        super.init(Ar)
        val magic = FGuid(Ar)
        if (magic != locResMagic)
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

@ExperimentalUnsignedTypes
class FTextLocalizationResourceString : UClass {
    var data: String
    var refCount: Int

    constructor(Ar: FArchive) {
        super.init(Ar)
        data = Ar.readString()
        refCount = Ar.readInt32()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeString(data)
        Ar.writeInt32(refCount)
        super.completeWrite(Ar)
    }

    constructor(data: String, refCount: Int) : super() {
        this.data = data
        this.refCount = refCount
    }
}

@ExperimentalUnsignedTypes
class FTextKey : UClass {
    var stringHash: UInt
    var text: String

    constructor(Ar: FArchive) {
        super.init(Ar)
        stringHash = Ar.readUInt32()
        text = Ar.readString()
        super.complete(Ar)
    }

    fun serialize(Ar: FArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(stringHash)
        Ar.writeString(text)
        super.completeWrite(Ar)
    }

    constructor(stringHash: UInt, text: String) : super() {
        this.stringHash = stringHash
        this.text = text
    }
}