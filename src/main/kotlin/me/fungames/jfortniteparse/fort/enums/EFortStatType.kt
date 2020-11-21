package me.fungames.jfortniteparse.fort.enums

import me.fungames.jfortniteparse.ue4.objects.core.i18n.FText
import java.util.*

enum class EFortStatType(val displayName: FText, val icon: String?) {
    Fortitude(FText("", "346D2FEB418D3973CF56DBBC0470B609", "Fortitude"), "/Game/UI/Foundation/Textures/Icons/Stats/T-Icon-Fortitude-128.T-Icon-Fortitude-128"),
    Offense(FText("", "1DE7246343524B947762ADA1E12A72C4", "Offense"), "/Game/UI/Foundation/Textures/Icons/Stats/T-Icon-Offense-128.T-Icon-Offense-128"),
    Resistance(FText("", "3E936E1F48EF8472EC75428D59A4A51A", "Resistance"), "/Game/UI/Foundation/Textures/Icons/Stats/T-Icon-Resistance-128.T-Icon-Resistance-128"),
    Technology(FText("", "290015B64F1F0EE56B6BBC8AAD9B7839", "Tech"), "/Game/UI/Foundation/Textures/Icons/Stats/T-Icon-Tech-128.T-Icon-Tech-128"),
    Fortitude_Team(FText("", "F7C8CD9243FF2C5D1C18F4A305DFCAF0", "Party Fortitude"), "/Game/UI/Foundation/Textures/Icons/Stats/T-Icon-TeamFortitude-128.T-Icon-TeamFortitude-128"),
    Offense_Team(FText("", "62C521184CC70DA924E1EE884064BA64", "Party Offense"), "/Game/UI/Foundation/Textures/Icons/Stats/T-Icon-TeamOffense-128.T-Icon-TeamOffense-128"),
    Resistance_Team(FText("", "316EF9FF41EFDABDB0C9A2B86DADC89B", "Party Resistance"), "/Game/UI/Foundation/Textures/Icons/Stats/T-Icon-TeamResistance-128.T-Icon-TeamResistance-128"),
    Technology_Team(FText("", "5A1B6A88440EFDF034CEDEA6F776DEF0", "Party Tech"), "/Game/UI/Foundation/Textures/Icons/Stats/T-Icon-TeamTech-128.T-Icon-TeamTech-128"),
    Invalid(FText("Invalid"), null);

    companion object {
        @JvmStatic
        fun from(s: String) = when (s.toLowerCase(Locale.ENGLISH)) {
			"fortitude" -> Fortitude
			"offense" -> Offense
			"resistance" -> Resistance
			"tech", "technology" -> Technology
			"fortitude_team" -> Fortitude_Team
			"offense_team" -> Offense_Team
			"resistance_team" -> Resistance_Team
			"tech_team", "technology_team" -> Technology_Team
            else -> Invalid
        }
    }
}