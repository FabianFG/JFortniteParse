package me.fungames.jfortniteparse.ue4.assets.exports.fort

import me.fungames.jfortniteparse.ue4.assets.FText

enum class EFortRarity(val rarityName : FText) {

    Masterwork(FText("Fort.Rarity", "Transcendent", "Transcendent")),
    Transcendent(FText("Fort.Rarity", "Transcendent", "Transcendent")),

    Elegant(FText("Fort.Rarity", "Mythic", "Mythic")),
    Mythic(FText("Fort.Rarity", "Mythic", "Mythic")),

    Fine(FText("Fort.Rarity", "Legendary", "Legendary")),
    Legendary(FText("Fort.Rarity", "Legendary", "Legendary")),

    Quality(FText("Fort.Rarity", "Epic", "Epic")),
    Epic(FText("Fort.Rarity", "Epic", "Epic")),

    Sturdy(FText("Fort.Rarity", "Rare", "Rare")),
    Rare(FText("Fort.Rarity", "Rare", "Rare")),

    Uncommon(FText("Fort.Rarity", "Uncommon", "Uncommon")),

    Handmade(FText("Fort.Rarity", "Common", "Common")),
    Common(FText("Fort.Rarity", "Common ", "Common"));

    companion object {
        fun getEnum(rarity: String?) : EFortRarity {
            if (rarity == null)
                return Uncommon
            val name = rarity.substringAfterLast("EFortRarity::")
            values().forEach {
                if (name == it.name)
                    return it
            }
            return Common
        }
    }
}