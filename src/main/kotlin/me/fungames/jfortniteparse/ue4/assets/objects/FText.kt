package me.fungames.jfortniteparse.ue4.assets.objects

import me.fungames.jfortniteparse.exceptions.ParserException
import me.fungames.jfortniteparse.ue4.UClass
import me.fungames.jfortniteparse.ue4.assets.enums.ETextHistoryType
import me.fungames.jfortniteparse.ue4.assets.reader.FAssetArchive
import me.fungames.jfortniteparse.ue4.assets.writer.FAssetArchiveWriter
import me.fungames.jfortniteparse.ue4.locres.Locres
import me.fungames.jfortniteparse.ue4.reader.FArchive
import me.fungames.jfortniteparse.ue4.writer.FArchiveWriter

@Suppress("EXPERIMENTAL_API_USAGE", "EXPERIMENTAL_OVERRIDE")
class FText : UClass {
    var flags: UInt
    var historyType: ETextHistoryType
    var textHistory : FTextHistory
    var text: String

    constructor(Ar: FAssetArchive) {
        super.init(Ar)
        flags = Ar.readUInt32()
        historyType =
            ETextHistoryType.valueOfByte(Ar.readInt8())
        textHistory = when (historyType) {
            ETextHistoryType.None -> FTextHistory.None()
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
        super.complete(Ar)
    }

    fun copy() = FText(flags, historyType, textHistory)

    fun serialize(Ar: FAssetArchiveWriter) {
        super.initWrite(Ar)
        Ar.writeUInt32(flags)
        Ar.writeInt8(historyType.value)
        textHistory.serialize(Ar)
        super.completeWrite(Ar)
    }

    override fun toString() = text

    @Suppress("EXPERIMENTAL_UNSIGNED_LITERALS")
    constructor(nameSpace: String, key: String, sourceString: String, flags: UInt = 0u, historyType: ETextHistoryType = ETextHistoryType.Base) : this(flags, historyType,
        FTextHistory.Base(nameSpace, key, sourceString)
    )

    constructor(flags: UInt, historyType: ETextHistoryType, textHistory: FTextHistory) : super() {
        this.flags = flags
        this.historyType = historyType
        this.textHistory = textHistory
        this.text = textHistory.text
    }

    fun applyLocres(locres: Locres?) {
        val history = textHistory
        if (locres != null && history is FTextHistory.Base)
            text = locres.texts.stringData[history.nameSpace]?.get(history.key) ?: return
    }

    fun textForLocres(locres: Locres?) : String {
        val history = textHistory
        return if (history is FTextHistory.Base)
            locres?.texts?.stringData?.get(history.nameSpace)?.get(history.key) ?: text
        else text
    }
}