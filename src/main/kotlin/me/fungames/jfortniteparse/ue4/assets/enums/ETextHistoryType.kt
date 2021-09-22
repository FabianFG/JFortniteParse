package me.fungames.jfortniteparse.ue4.assets.enums

import me.fungames.jfortniteparse.LOG_JFP

enum class ETextHistoryType(val value : Byte) {
    None(-1),
    Base(0),
    NamedFormat(1),
    OrderedFormat(2),
    ArgumentFormat(3),
    AsNumber(4),
    AsPercent(5),
    AsCurrency(6),
    AsDate(7),
    AsTime(8),
    AsDateTime(9),
    Transform(10),
    StringTableEntry(11),
    TextGenerator(12);

    companion object {
        fun valueOfByte(byte: Byte) : ETextHistoryType {
            val value = values().firstOrNull { it.value == byte}
            if (value == null)
                LOG_JFP.warn { "Unsupported ETextHistoryType $byte, using ETextHistoryType::None as fallback" }
            return value ?: None
        }
    }
}