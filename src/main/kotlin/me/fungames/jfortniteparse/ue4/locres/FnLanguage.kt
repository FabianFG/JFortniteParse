package me.fungames.jfortniteparse.ue4.locres

enum class FnLanguage(val languageCode: String) {

    AR("ar"),
    DE("de"),
    EN("en"),
    ES("es"),
    ES_419("es-419"),
    FR("fr"),
    IT("it"),
    JA("ja"),
    KO("ko"),
    PL("pl"),
    PT_BR("pt-BR"),
    RU("ru"),
    TR("tr"),
    ZH_CN("zh-CN"),
    ZH_HANT("zh-Hant"),
    UNKNOWN("unknown");

    companion object {
        @JvmStatic
        fun valueOfLanguageCode(lang : String) = FnLanguage.values().firstOrNull { l -> l.languageCode == lang } ?: UNKNOWN
    }

    val path = "FortniteGame/Content/Localization/Game_BR/$languageCode/Game_BR.locres"


}