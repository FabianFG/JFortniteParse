package me.fungames.jfortniteparse.ue4.objects.core.i18n

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.assets.enums.EDateTimeStyle
import me.fungames.jfortniteparse.ue4.assets.enums.ETextHistoryType
import me.fungames.jfortniteparse.ue4.assets.exports.UStringTable
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.objects.core.misc.FDateTime
import me.fungames.jfortniteparse.ue4.objects.uobject.FName
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

enum class EFormatArgumentType { Int, UInt, Float, Double, Text, Gender }

class FText {
    var flags: UInt
    var historyType: ETextHistoryType
    var textHistory: FTextHistory
    var text: String

    constructor(Ar: FArchive) {
        flags = Ar.readUInt32()
        historyType = ETextHistoryType.valueOfByte(Ar.readInt8())
        textHistory = when (historyType) {
            ETextHistoryType.None -> FTextHistory.None(Ar)
            ETextHistoryType.Base -> FTextHistory.Base(Ar)
            ETextHistoryType.NamedFormat,
            ETextHistoryType.OrderedFormat -> FTextHistory.OrderedFormat(Ar)
            ETextHistoryType.ArgumentFormat -> TODO()
            ETextHistoryType.AsNumber,
            ETextHistoryType.AsPercent,
            ETextHistoryType.AsCurrency -> FTextHistory.FormatNumber(Ar)
            ETextHistoryType.AsDate -> TODO()
            ETextHistoryType.AsTime -> TODO()
            ETextHistoryType.AsDateTime -> TODO()
            ETextHistoryType.Transform -> TODO()
            ETextHistoryType.StringTableEntry -> FTextHistory.StringTableEntry(Ar)
            ETextHistoryType.TextGenerator -> TODO()
        }
        text = textHistory.text
    }

    fun copy() = FText(flags, historyType, textHistory)

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeUInt32(flags)
        Ar.writeInt8(historyType.value)
        textHistory.serialize(Ar)
    }

    override fun toString() = text

    constructor(sourceString: String) : this("", "", sourceString)

    constructor(namespace: String, key: String, sourceString: String) : this(namespace, key, sourceString, 0u, ETextHistoryType.Base)

    constructor(namespace: String, key: String, sourceString: String, flags: UInt = 0u, historyType: ETextHistoryType = ETextHistoryType.Base) :
        this(flags, historyType, FTextHistory.Base(namespace, key, sourceString))

    constructor(flags: UInt, historyType: ETextHistoryType, textHistory: FTextHistory) {
        this.flags = flags
        this.historyType = historyType
        this.textHistory = textHistory
        this.text = textHistory.text
    }

    fun textForLocres(locres: Locres?): String {
        val history = textHistory
        return if (history is FTextHistory.Base)
            locres?.texts?.stringData?.get(history.namespace)?.get(history.key) ?: text
        else text
    }
}

sealed class FTextHistory {
    class None : FTextHistory {
        var cultureInvariantString: String? = null
        override val text: String
            get() = cultureInvariantString ?: ""

        constructor()

        constructor(Ar: FArchive) {
            val bHasCultureInvariantString = Ar.readBoolean()
            if (bHasCultureInvariantString) {
                cultureInvariantString = Ar.readString()
            }
        }

        override fun serialize(Ar: FArchiveWriter) {
            val bHasCultureInvariantString = cultureInvariantString.isNullOrEmpty()
            Ar.writeBoolean(bHasCultureInvariantString)
            if (bHasCultureInvariantString) {
                Ar.writeString(cultureInvariantString!!)
            }
        }
    }

    class Base : FTextHistory {
        var namespace: String
        var key: String
        var sourceString: String
        override val text: String
            get() = sourceString

        constructor(Ar: FArchive) {
            this.namespace = Ar.readString()
            this.key = Ar.readString()
            this.sourceString = Ar.readString()
        }

        constructor(namespace: String, key: String, sourceString: String) {
            this.namespace = namespace
            this.key = key
            this.sourceString = sourceString
        }

        override fun serialize(Ar: FArchiveWriter) {
            Ar.writeString(namespace)
            Ar.writeString(key)
            Ar.writeString(sourceString)
        }
    }

    class DateTime : FTextHistory {
        var sourceDateTime: FDateTime
        var dateStyle: EDateTimeStyle
        var timeStyle: EDateTimeStyle
        var timeZone: String
        var targetCulture: String
        override val text: String
            get() = "$timeZone: ${sourceDateTime.date}"

        constructor(Ar: FArchive) {
            this.sourceDateTime = FDateTime(Ar)
            this.dateStyle = EDateTimeStyle.values()[Ar.readInt8().toInt()]
            this.timeStyle = EDateTimeStyle.values()[Ar.readInt8().toInt()]
            this.timeZone = Ar.readString()
            this.targetCulture = Ar.readString()
        }

        constructor(
            sourceDateTime: FDateTime,
            dateStyle: EDateTimeStyle,
            timeStyle: EDateTimeStyle,
            timeZone: String,
            targetCulture: String
        ) : super() {
            this.sourceDateTime = sourceDateTime
            this.dateStyle = dateStyle
            this.timeStyle = timeStyle
            this.timeZone = timeZone
            this.targetCulture = targetCulture
        }

        override fun serialize(Ar: FArchiveWriter) {
            sourceDateTime.serialize(Ar)
            Ar.writeInt8(dateStyle.ordinal.toByte())
            Ar.writeInt8(timeStyle.ordinal.toByte())
            Ar.writeString(timeZone)
            Ar.writeString(targetCulture)
        }
    }

    class OrderedFormat : FTextHistory {
        var sourceFmt: FText
        var arguments: Array<FFormatArgumentValue>

        override val text: String
            get() = sourceFmt.text //TODO

        constructor(Ar: FArchive) {
            this.sourceFmt = FText(Ar)
            this.arguments = Ar.readTArray { FFormatArgumentValue(Ar) }
        }

        constructor(sourceFmt: FText, arguments: Array<FFormatArgumentValue>) {
            this.sourceFmt = sourceFmt
            this.arguments = arguments
        }

        override fun serialize(Ar: FArchiveWriter) {
            sourceFmt.serialize(Ar)
            Ar.writeTArray(arguments) { it.serialize(Ar) }
        }
    }

    class FormatNumber : FTextHistory {
        /** The source value to format from */
        var sourceValue: FFormatArgumentValue
        /** The culture to format using */
        var timeZone: String
        var targetCulture: String

        override val text: String
            get() = sourceValue.toString()

        constructor(Ar: FArchive) {
            this.sourceValue = FFormatArgumentValue(Ar)
            this.timeZone = Ar.readString()
            this.targetCulture = Ar.readString()
        }

        constructor(sourceValue: FFormatArgumentValue, timeZone: String, targetCulture: String) {
            this.sourceValue = sourceValue
            this.timeZone = timeZone
            this.targetCulture = targetCulture
        }

        override fun serialize(Ar: FArchiveWriter) {
            sourceValue.serialize(Ar)
            Ar.writeString(timeZone)
            Ar.writeString(targetCulture)
        }
    }

    class StringTableEntry : FTextHistory {
        /** The string table ID being referenced */
        var tableId: FName
        /** The key within the string table being referenced */
        var key: String

        override val text: String

        constructor(Ar: FArchive) {
            if (Ar !is FAssetArchive) {
                throw ParserException("Tried to load a string table entry with wrong archive type")
            }
            this.tableId = Ar.readFName()
            this.key = Ar.readString()
            val table = Ar.provider?.loadObject<UStringTable>(tableId.text) ?: throw ParserException("Failed to load string table '$tableId'")
            text = table.entries[key] ?: throw ParserException("Didn't find needed in key in string table")
        }

        constructor(tableId: FName, key: String, text: String) {
            this.tableId = tableId
            this.key = key
            this.text = text
        }

        override fun serialize(Ar: FArchiveWriter) {
            if (Ar !is FAssetArchiveWriter) {
                throw ParserException("Tried to save a string table entry with wrong archive type")
            }
            Ar.writeFName(tableId)
            Ar.writeString(key)
        }
    }

    abstract fun serialize(Ar: FArchiveWriter)
    abstract val text: String
}

class FFormatArgumentValue {
    var type: EFormatArgumentType
    var value: Any

    constructor(Ar: FArchive) {
        type = EFormatArgumentType.values()[Ar.readInt8().toInt()]
        value = when (type) {
            EFormatArgumentType.Int -> Ar.readInt64()
            EFormatArgumentType.UInt -> Ar.readUInt64()
            EFormatArgumentType.Float -> Ar.readFloat32()
            EFormatArgumentType.Double -> Ar.readDouble()
            EFormatArgumentType.Text -> FText(Ar)
            EFormatArgumentType.Gender -> TODO("Gender Argument not supported yet")
        }
    }

    constructor(type: EFormatArgumentType, value: Any) {
        this.type = type
        this.value = value
    }

    fun serialize(Ar: FArchiveWriter) {
        Ar.writeInt8(type.ordinal.toByte())
        when (type) {
            EFormatArgumentType.Int -> Ar.writeInt64(value as Long)
            EFormatArgumentType.UInt -> Ar.writeUInt64(value as ULong)
            EFormatArgumentType.Float -> Ar.writeFloat32(value as Float)
            EFormatArgumentType.Double -> Ar.writeDouble(value as Double)
            EFormatArgumentType.Text -> (value as FText).serialize(Ar)
            EFormatArgumentType.Gender -> TODO("Gender Argument not supported yet")
        }
    }
}