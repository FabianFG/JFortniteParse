package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.EDateTimeStyle
import me.fungames.jfortniteparse.ue4.assets.exports.UStringTable
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.util.FName
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@ExperimentalUnsignedTypes
sealed class FTextHistory : UClass() {
    class None : FTextHistory() {
        override val text = ""
        override fun serialize(Ar: FAssetArchiveWriter) {}
    }

    class Base : FTextHistory {
        var nameSpace : String
        var key : String
        var sourceString : String
        override val text : String
            get() = sourceString

        constructor(Ar: FArchive) {
            super.init(Ar)
            this.nameSpace = Ar.readString()
            this.key = Ar.readString()
            this.sourceString = Ar.readString()
            super.complete(Ar)
        }

        constructor(nameSpace: String, key: String, sourceString: String) {
            this.nameSpace = nameSpace
            this.key = key
            this.sourceString = sourceString
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeString(nameSpace)
            Ar.writeString(key)
            Ar.writeString(sourceString)
            super.completeWrite(Ar)
        }
    }

    class DateTime : FTextHistory {
        var sourceDateTime : FDateTime
        var dateStyle : EDateTimeStyle
        var timeStyle : EDateTimeStyle
        var timeZone : String
        var targetCulture : String
        override val text : String
            get() = "$timeZone: ${sourceDateTime.date}"

        constructor(Ar: FArchive) {
            super.init(Ar)
            this.sourceDateTime = FDateTime(Ar)
            this.dateStyle = EDateTimeStyle.values()[Ar.readInt8().toInt()]
            this.timeStyle = EDateTimeStyle.values()[Ar.readInt8().toInt()]
            this.timeZone = Ar.readString()
            this.targetCulture = Ar.readString()
            super.complete(Ar)
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

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            sourceDateTime.serialize(Ar)
            Ar.writeInt8(dateStyle.ordinal.toByte())
            Ar.writeInt8(timeStyle.ordinal.toByte())
            Ar.writeString(timeZone)
            Ar.writeString(targetCulture)
            super.completeWrite(Ar)
        }
    }

    class OrderedFormat : FTextHistory {
        var sourceFmt : FText
        var arguments : Array<FFormatArgumentValue>

        override val text : String
            get() = sourceFmt.text //TODO

        constructor(Ar: FAssetArchive) {
            super.init(Ar)
            this.sourceFmt = FText(Ar)
            this.arguments = Ar.readTArray { FFormatArgumentValue(Ar) }
            super.complete(Ar)
        }

        constructor(sourceFmt: FText, arguments: Array<FFormatArgumentValue>) : super() {
            this.sourceFmt = sourceFmt
            this.arguments = arguments
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            sourceFmt.serialize(Ar)
            Ar.writeTArray(arguments) { it.serialize(Ar) }
            super.completeWrite(Ar)
        }
    }

    class FormatNumber : FTextHistory {
        /** The source value to format from */
        var sourceValue : FFormatArgumentValue
        /** The culture to format using */
        var timeZone : String
        var targetCulture : String

        override val text : String
            get() = sourceValue.toString()

        constructor(Ar: FAssetArchive) {
            super.init(Ar)
            this.sourceValue = FFormatArgumentValue(Ar)
            this.timeZone = Ar.readString()
            this.targetCulture = Ar.readString()
            super.complete(Ar)
        }

        constructor(sourceValue: FFormatArgumentValue, timeZone: String, targetCulture: String) {
            this.sourceValue = sourceValue
            this.timeZone = timeZone
            this.targetCulture = targetCulture
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            sourceValue.serialize(Ar)
            Ar.writeString(timeZone)
            Ar.writeString(targetCulture)
            super.completeWrite(Ar)
        }
    }

    class StringTableEntry : FTextHistory {
        /** The string table ID being referenced */
        var tableId : FName
        /** The key within the string table being referenced */
        var key : String

        override val text : String

        constructor(Ar: FAssetArchive) {
            super.init(Ar)
            this.tableId = Ar.readFName()
            this.key = Ar.readString()
            val table = Ar.loadImport(tableId.text)?.getExportOfTypeOrNull<UStringTable>() ?: throw ParserException("Failed to load string table '$tableId'")
            text = table.entries[key] ?: throw ParserException("Didn't find needed in key in string table")
            super.complete(Ar)
        }

        constructor(tableId: FName, key: String, text : String) {
            this.tableId = tableId
            this.key = key
            this.text = text
        }

        override fun serialize(Ar: FAssetArchiveWriter) {
            super.initWrite(Ar)
            Ar.writeFName(tableId)
            Ar.writeString(key)
            super.completeWrite(Ar)
        }
    }

    abstract fun serialize(Ar : FAssetArchiveWriter)
    abstract val text : String
}